package com.greader.neo.data

import android.content.Context
import com.greader.neo.R
import com.greader.neo.data.auth.AuthManager
import com.greader.neo.data.db.FeedEntity
import com.greader.neo.data.db.NeoDatabase
import com.greader.neo.data.db.Tag2SubEntity
import com.greader.neo.data.db.TagEntity
import com.greader.neo.data.feedly.FeedlyApi
import com.greader.neo.data.feedly.FeedlyCategory
import com.greader.neo.data.feedly.FeedlySubscriptionUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Edição de assinaturas: envia ao Feedly e reflete no banco local.
 *
 * Endpoints confirmados no cliente original (análise REA):
 * - Atualizar/renomear/mover: `POST /v3/subscriptions` (categories substitui as pastas)
 * - Remover: `DELETE /v3/subscriptions/{id}` (id url-encoded)
 */
class SubscriptionActions(
    private val db: NeoDatabase,
    private val api: FeedlyApi,
    private val auth: AuthManager,
    private val context: Context,
) {

    suspend fun update(feed: FeedEntity, newTitle: String, folder: TagEntity?): Result<Unit> =
        runCatching {
            val token = auth.validAccessToken()
                ?: error(context.getString(R.string.login_required))
            val body = FeedlySubscriptionUpdate(
                id = feed.uid,
                title = newTitle,
                updated = System.currentTimeMillis(),
                categories = folder?.let {
                    listOf(FeedlyCategory(id = it.uid, label = it.label))
                } ?: emptyList(),
            )
            val response = api.updateSubscription("OAuth $token", body)
            if (!response.isSuccessful) error("HTTP ${response.code()}")

            withContext(Dispatchers.IO) {
                db.feedDao().upsert(feed.copy(title = newTitle))
                db.tag2SubDao().deleteByFeed(feed.id)
                if (folder != null) {
                    db.tag2SubDao().upsertAll(
                        listOf(
                            Tag2SubEntity(
                                tagUid = folder.uid,
                                feedId = feed.id,
                                syncTime = System.currentTimeMillis() / 1000,
                            ),
                        ),
                    )
                }
            }
        }

    suspend fun delete(feed: FeedEntity): Result<Unit> =
        runCatching {
            val token = auth.validAccessToken()
                ?: error(context.getString(R.string.login_required))
            val response = api.deleteSubscription("OAuth $token", feed.uid)
            if (!response.isSuccessful) error("HTTP ${response.code()}")

            withContext(Dispatchers.IO) {
                db.articleDao().deleteByFeed(feed.id)
                db.tag2SubDao().deleteByFeed(feed.id)
                db.feedDao().deleteById(feed.id)
            }
        }
}
