package com.greader.neo.data.sync

import android.content.Context
import android.util.Log
import com.greader.neo.R
import com.greader.neo.data.auth.AuthManager
import com.greader.neo.data.db.ArticleEntity
import com.greader.neo.data.db.FeedEntity
import com.greader.neo.data.db.NeoDatabase
import com.greader.neo.data.db.SyncOperationType
import com.greader.neo.data.db.Tag2SubEntity
import com.greader.neo.data.db.TagEntity
import com.greader.neo.data.feedly.FeedlyApi
import com.greader.neo.data.feedly.FeedlyItem
import com.greader.neo.data.feedly.FeedlyMarkerRequest
import com.greader.neo.data.prefs.SettingsRepository
import retrofit2.HttpException
import java.io.IOException
import java.net.URLDecoder

sealed interface SyncOutcome {
    data class Success(val newArticles: Int) : SyncOutcome
    data object NotLoggedIn : SyncOutcome
    data class AuthError(val message: String) : SyncOutcome
    data class Error(val message: String, val retryable: Boolean) : SyncOutcome
}

/**
 * Motor de sincronização com o Feedly v3.
 *
 * Sequência (baseada no engine original — Relatório 02):
 * 1. Replay da outbox (`sync_operations`) via markers
 * 2. Pull de assinaturas + categorias → feeds/tags/tag2sub (substituição)
 * 3. Pull de artigos (`streams/contents` com continuation + newerThan)
 * 4. Reconciliação de leitura (`streams/ids` + regras keep_unread/read_time)
 * 5. Contadores de não lidos + last_sync_time
 */
class FeedlySyncEngine(
    private val db: NeoDatabase,
    private val api: FeedlyApi,
    private val auth: AuthManager,
    private val settings: SettingsRepository,
    private val context: Context,
) {

    suspend fun sync(): SyncOutcome {
        val token = auth.validAccessToken() ?: return SyncOutcome.NotLoggedIn
        val header = "OAuth $token"
        val syncTime = System.currentTimeMillis() / 1000
        var stepRes = R.string.sync_step_outbox

        return try {
            replayOutbox(header)
            stepRes = R.string.sync_step_feeds
            val feedIdsByUid = pullSubscriptions(header, syncTime)
            stepRes = R.string.sync_step_articles
            val newCount = pullArticles(header, syncTime, feedIdsByUid)
            if (settings.current().syncServerReads) {
                stepRes = R.string.sync_step_reads
                reconcileReads(header, syncTime)
            }
            stepRes = R.string.sync_step_cleanup
            cleanupOldArticles()
            stepRes = R.string.sync_step_counters
            db.feedDao().updateUnreadCounts()
            db.feedDao().updateNewestTimes()
            db.tagDao().updateFolderUnreadCounts()
            db.syncOperationDao().purgeDone()
            settings.setLastSyncTime(syncTime)
            Log.i(TAG, "Sync concluído: $newCount artigos novos")
            SyncOutcome.Success(newCount)
        } catch (e: HttpException) {
            if (e.code() == 401 || e.code() == 403) {
                auth.invalidateAccessToken()
                SyncOutcome.AuthError(context.getString(R.string.sync_session_expired))
            } else {
                val detail = runCatching {
                    e.response()?.errorBody()?.string()?.take(200)
                }.getOrNull().orEmpty()
                SyncOutcome.Error(
                    context.getString(
                        R.string.sync_failed_at,
                        context.getString(stepRes),
                        "HTTP ${e.code()}$detail".trim(),
                    ),
                    retryable = e.code() >= 500,
                )
            }
        } catch (e: IOException) {
            SyncOutcome.Error(
                context.getString(
                    R.string.sync_failed_at,
                    context.getString(stepRes),
                    e.message ?: e.javaClass.simpleName,
                ),
                retryable = true,
            )
        } catch (e: Exception) {
            SyncOutcome.Error(
                context.getString(
                    R.string.sync_failed_at,
                    context.getString(stepRes),
                    e.message ?: e.javaClass.simpleName,
                ),
                retryable = false,
            )
        }
    }

    // ---------- 1. Outbox ----------

    private suspend fun replayOutbox(header: String) {
        val now = System.currentTimeMillis()
        for (op in db.syncOperationDao().pending()) {
            val params = parseParams(op.params)
            val ok = when (op.type) {
                SyncOperationType.TOGGLE_READ -> {
                    val uid = params["uid"] ?: continue
                    marker(header, if (params["read"] == "1") "markAsRead" else "markAsUnread", listOf(uid))
                }

                SyncOperationType.TOGGLE_STARRED -> {
                    val uid = params["uid"] ?: continue
                    marker(header, if (params["starred"] == "1") "markAsSaved" else "markAsUnsaved", listOf(uid))
                }

                SyncOperationType.TOGGLE_KEEP_UNREAD -> {
                    val uid = params["uid"] ?: continue
                    // Feedly tem "keepUnread" público; remover keep-unread não tem marker documentado.
                    if (params["keep"] == "1") marker(header, "keepUnread", listOf(uid)) else true
                }

                SyncOperationType.MARK_ALL_READ -> {
                    val streamId = params["streamId"] ?: continue
                    val userId = ensureUserId(header)
                    val request = when {
                        streamId == "global.all" -> FeedlyMarkerRequest(
                            action = "markAsRead",
                            type = "categories",
                            categoryIds = listOf("user/$userId/category/global.all"),
                            asOf = now + 999,
                        )

                        streamId.startsWith("feed/") -> FeedlyMarkerRequest(
                            action = "markAsRead",
                            type = "feeds",
                            feedIds = listOf(streamId),
                            asOf = now + 999,
                        )

                        else -> FeedlyMarkerRequest(
                            action = "markAsRead",
                            type = "categories",
                            categoryIds = listOf(streamId),
                            asOf = now + 999,
                        )
                    }
                    api.markers(header, request).isSuccessful
                }

                SyncOperationType.MARK_UNREAD -> {
                    val ids = params["ids"]
                        ?.split(',')
                        ?.filter { it.isNotBlank() }
                        .orEmpty()
                    ids.isEmpty() || marker(header, "markAsUnread", ids)
                }

                // Tipos de edição de pastas/labels ainda não expostos na UI — descartar.
                else -> true
            }
            if (ok) db.syncOperationDao().markDone(op.id) else break
        }
    }

    private suspend fun marker(header: String, action: String, entryIds: List<String>): Boolean =
        api.markers(
            header,
            FeedlyMarkerRequest(action = action, type = "entries", entryIds = entryIds),
        ).isSuccessful

    // ---------- 2. Assinaturas + categorias ----------

    private suspend fun pullSubscriptions(header: String, syncTime: Long): Map<String, Long> {
        val subs = api.subscriptions(header)

        val existingFeeds = db.feedDao().all().associateBy { it.uid }
        val feeds = subs.mapIndexed { index, sub ->
            val prev = existingFeeds[sub.id]
            FeedEntity(
                id = prev?.id ?: 0,
                uid = sub.id,
                title = sub.title ?: prev?.title ?: sub.id,
                feedUrl = sub.id.removePrefix("feed/"),
                htmlUrl = sub.website ?: prev?.htmlUrl,
                iconUrl = sub.visualUrl ?: prev?.iconUrl,
                sort = index,
                unreadCount = prev?.unreadCount ?: 0,
                newestItemTime = prev?.newestItemTime ?: 0,
                syncTime = syncTime,
                syncExcluded = prev?.syncExcluded ?: false,
                notification = prev?.notification ?: false,
                hidden = prev?.hidden ?: false,
            )
        }
        db.feedDao().upsertAll(feeds)
        db.feedDao().deleteNotSynced(syncTime)

        // Categorias viram pastas (type 11); vínculos feed↔pasta em tag2sub.
        val existingTags = db.tagDao().all().associateBy { it.uid }
        val existingLinks = db.tag2SubDao().all().associateBy { "${it.tagUid}|${it.feedId}" }
        val feedIdsByUid = db.feedDao().all().associate { it.uid to it.id }

        val tags = mutableListOf<TagEntity>()
        val links = mutableListOf<Tag2SubEntity>()
        subs.forEach { sub ->
            val feedId = feedIdsByUid[sub.id] ?: return@forEach
            sub.categories.forEach { category ->
                val prev = existingTags[category.id]
                tags += TagEntity(
                    id = prev?.id ?: 0,
                    uid = category.id,
                    type = 11,
                    label = category.label ?: category.id,
                    sort = prev?.sort ?: 0,
                    unreadCount = prev?.unreadCount ?: 0,
                    syncTime = syncTime,
                    hidden = prev?.hidden ?: false,
                )
                links += Tag2SubEntity(
                    id = existingLinks["${category.id}|$feedId"]?.id ?: 0,
                    tagUid = category.id,
                    feedId = feedId,
                    syncTime = syncTime,
                )
            }
        }
        db.tagDao().upsertAll(tags.distinctBy { it.uid })
        db.tag2SubDao().upsertAll(links.distinctBy { "${it.tagUid}|${it.feedId}" })
        db.tag2SubDao().deleteNotSynced(syncTime)
        db.tagDao().deleteNotSynced(syncTime)
        db.tag2SubDao().deleteOrphans()

        return feedIdsByUid
    }

    // ---------- 3. Artigos ----------

    private suspend fun pullArticles(
        header: String,
        syncTime: Long,
        feedIdsByUid: Map<String, Long>,
    ): Int {
        val userId = ensureUserId(header)
        val streamId = "user/$userId/category/global.all"
        val lastSync = settings.lastSyncTime()
        val limit = settings.current().syncItemLimit

        var continuation: String? = null
        var fetched = 0
        var newCount = 0

        while (true) {
            val page = api.streamContents(
                authorization = header,
                streamId = streamId,
                count = 1000,
                newerThan = lastSync.takeIf { it > 0 }?.times(1000),
                continuation = continuation,
            )
            if (page.items.isNotEmpty()) {
                newCount += upsertItems(page.items, feedIdsByUid, syncTime)
                fetched += page.items.size
            }
            continuation = page.continuation
            if (continuation.isNullOrBlank() || fetched >= limit) break
        }
        return newCount
    }

    private suspend fun upsertItems(
        items: List<FeedlyItem>,
        feedIdsByUid: Map<String, Long>,
        syncTime: Long,
    ): Int {
        val existing = db.articleDao().byUids(items.map { it.id }).associateBy { it.uid }
        var newCount = 0

        val entities = items.mapNotNull { item ->
            val feedId = feedIdsByUid[item.origin?.streamId] ?: return@mapNotNull null
            val prev = existing[item.id]

            val link = item.alternate.firstOrNull()?.href?.let(::unwrapGoogleRedirect)
            val image = item.visual?.url
                ?: item.enclosure.firstOrNull { it.type?.startsWith("image/") == true }?.href
            val content = item.content?.content ?: item.summary?.content
            val serverStarred = (item.categories + item.tags).any { it.id.endsWith("/global.saved") }

            if (prev == null) {
                newCount++
                val read = !(item.unread ?: false)
                ArticleEntity(
                    feedId = feedId,
                    uid = item.id,
                    title = item.title ?: "(sem título)",
                    content = content,
                    author = item.author,
                    link = link,
                    image = image,
                    publishedTime = (item.published ?: 0) / 1000,
                    updatedTime = (item.crawled ?: item.published ?: 0) / 1000,
                    starred = serverStarred,
                    read = read,
                    readTime = if (read) syncTime else 0,
                    syncTime = syncTime,
                )
            } else {
                // Existente: atualiza conteúdo/estrela, preserva estado de leitura local.
                prev.copy(
                    feedId = feedId,
                    title = item.title ?: prev.title,
                    content = content ?: prev.content,
                    author = item.author ?: prev.author,
                    link = link ?: prev.link,
                    image = image ?: prev.image,
                    updatedTime = ((item.crawled ?: item.published ?: 0) / 1000)
                        .takeIf { it > 0 } ?: prev.updatedTime,
                    starred = prev.starred || serverStarred,
                    syncTime = syncTime,
                )
            }
        }

        if (entities.isNotEmpty()) db.articleDao().upsertAll(entities)
        return newCount
    }

    // ---------- 4. Reconciliação de leitura ----------

    private suspend fun reconcileReads(header: String, syncTime: Long) {        val userId = ensureUserId(header)
        val streamId = "user/$userId/category/global.all"
        val serverUnread = api.streamIds(
            authorization = header,
            streamId = streamId,
            count = 10000,
            unreadOnly = true,
        ).ids.toHashSet()

        val states = db.articleDao().statesSince(syncTime)
        val toMarkRead = states.filter { !it.read && it.uid !in serverUnread }.map { it.uid }
        val toMarkUnread = states.filter { it.read && it.uid in serverUnread }.map { it.uid }

        toMarkRead.chunked(500).forEach { db.articleDao().markRead(it, syncTime) }
        toMarkUnread.chunked(500).forEach { db.articleDao().markUnread(it, syncTime) }
    }

    // ---------- 5. Retenção ----------

    /** Remove artigos além do período de retenção configurado (estrelados são preservados). */
    private suspend fun cleanupOldArticles() {
        val days = settings.current().retentionDays
        if (days <= 0) return
        val cutoff = System.currentTimeMillis() / 1000 - days * 86_400L
        db.articleDao().deleteOlderThan(cutoff)
    }

    // ---------- utilitários ----------

    private suspend fun ensureUserId(header: String): String {
        auth.userId?.let { return it }
        val profile = api.profile(header)
        auth.setUserId(profile.id)
        return profile.id
    }

    private fun parseParams(params: String): Map<String, String> =
        params.split('&').mapNotNull { part ->
            val idx = part.indexOf('=')
            if (idx <= 0) null else part.substring(0, idx) to part.substring(idx + 1)
        }.toMap()

    private fun unwrapGoogleRedirect(url: String): String = when {
        url.startsWith("https://www.google.com/url?") -> {
            val idx = url.indexOf("url=")
            if (idx < 0) {
                url
            } else {
                runCatching {
                    URLDecoder.decode(url.substring(idx + 4).substringBefore('&'), "UTF-8")
                }.getOrDefault(url)
            }
        }

        else -> url
    }

    private companion object {
        const val TAG = "FeedlySync"
    }
}
