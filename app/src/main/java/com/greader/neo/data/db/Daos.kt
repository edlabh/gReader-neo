package com.greader.neo.data.db

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/** Projeção leve para reconciliação de leitura. */
data class ArticleStateRow(
    val uid: String,
    val read: Boolean,
    val readTime: Long,
)

/** Linha da lista de artigos (artigo + dados do feed). */
data class ArticleListItem(
    @Embedded val article: ArticleEntity,
    val feedTitle: String,
    val feedIcon: String?,
)

@Dao
interface FeedDao {

    @Query("SELECT * FROM feeds WHERE hidden = 0 ORDER BY sort ASC, title ASC")
    fun observeAll(): Flow<List<FeedEntity>>

    @Query("SELECT * FROM feeds")
    suspend fun all(): List<FeedEntity>

    @Query("SELECT * FROM feeds WHERE id = :id")
    suspend fun byId(id: Long): FeedEntity?

    @Query("SELECT * FROM feeds WHERE uid = :uid")
    suspend fun byUid(uid: String): FeedEntity?

    @Upsert
    suspend fun upsert(feed: FeedEntity): Long

    @Upsert
    suspend fun upsertAll(feeds: List<FeedEntity>)

    @Query("DELETE FROM feeds")
    suspend fun deleteAll()

    /** Remove feeds que não vieram no último pull (semântica de substituição do original). */
    @Query("DELETE FROM feeds WHERE syncTime <> :syncTime")
    suspend fun deleteNotSynced(syncTime: Long)

    @Query("DELETE FROM feeds WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM feeds")
    suspend fun count(): Int

    @Query("SELECT COALESCE(SUM(unreadCount), 0) FROM feeds WHERE hidden = 0")
    fun observeTotalUnread(): Flow<Int>

    @Query("SELECT COALESCE(SUM(unreadCount), 0) FROM feeds WHERE hidden = 0")
    suspend fun totalUnread(): Int

    /** Recalcula contadores de não lidos por feed (equivalente ao update_unreads do original). */
    @Query(
        "UPDATE feeds SET unreadCount = (" +
            "SELECT COUNT(*) FROM articles WHERE articles.feedId = feeds.id AND articles.read = 0" +
            ")",
    )
    suspend fun updateUnreadCounts()

    /** Recalcula newest_item_time por feed (equivalente ao update_newest_time). */
    @Query(
        "UPDATE feeds SET newestItemTime = COALESCE((" +
            "SELECT MAX(updatedTime) FROM articles WHERE articles.feedId = feeds.id" +
            "), 0)",
    )
    suspend fun updateNewestTimes()
}

@Dao
interface ArticleDao {

    @Query("SELECT * FROM articles WHERE feedId = :feedId ORDER BY publishedTime DESC")
    fun observeByFeed(feedId: Long): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles ORDER BY publishedTime DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<ArticleEntity>>

    /**
     * Lista para a UI: filtra por feed, pasta (via tag2sub), busca local no título
     * e, opcionalmente, somente não lidos.
     * feedId < 0 = todos; folderUid null = todas as pastas.
     * oldestFirst = true ordena do mais antigo para o mais recente.
     */
    @Query(
        "SELECT a.*, f.title AS feedTitle, f.iconUrl AS feedIcon FROM articles a " +
            "JOIN feeds f ON f.id = a.feedId " +
            "WHERE (:feedId < 0 OR a.feedId = :feedId) " +
            "AND (:folderUid IS NULL OR a.feedId IN (SELECT feedId FROM tag2sub WHERE tagUid = :folderUid)) " +
            "AND (:query = '' OR a.title LIKE '%' || :query || '%') " +
            "AND (:unreadOnly = 0 OR a.read = 0) " +
            "ORDER BY CASE WHEN :oldestFirst = 1 THEN a.publishedTime END ASC, " +
            "CASE WHEN :oldestFirst = 0 THEN a.publishedTime END DESC " +
            "LIMIT 500",
    )
    fun observeList(
        feedId: Long,
        folderUid: String?,
        query: String,
        oldestFirst: Boolean,
        unreadOnly: Boolean,
    ): Flow<List<ArticleListItem>>

    @Query("SELECT * FROM articles WHERE id = :id")
    suspend fun byId(id: Long): ArticleEntity?

    @Query("SELECT * FROM articles WHERE uid IN (:uids)")
    suspend fun byUids(uids: List<String>): List<ArticleEntity>

    @Query("SELECT uid, read, readTime FROM articles WHERE syncTime = :syncTime")
    suspend fun statesSince(syncTime: Long): List<ArticleStateRow>

    @Upsert
    suspend fun upsertAll(articles: List<ArticleEntity>)

    @Query("DELETE FROM articles WHERE feedId = :feedId")
    suspend fun deleteByFeed(feedId: Long)

    /** Remove artigos antigos (retenção), preservando estrelados. */
    @Query("DELETE FROM articles WHERE publishedTime < :cutoff AND starred = 0")
    suspend fun deleteOlderThan(cutoff: Long)

    @Query("UPDATE articles SET read = 1, readTime = :readTime WHERE uid IN (:uids) AND keepUnread = 0")
    suspend fun markRead(uids: List<String>, readTime: Long)

    @Query("UPDATE articles SET read = 0 WHERE uid IN (:uids) AND readTime < :syncTime")
    suspend fun markUnread(uids: List<String>, syncTime: Long)

    @Query("UPDATE articles SET read = :read, readTime = :readTime WHERE id = :id")
    suspend fun updateReadState(id: Long, read: Boolean, readTime: Long)

    @Query("UPDATE articles SET starred = :starred WHERE id = :id")
    suspend fun updateStarred(id: Long, starred: Boolean)

    @Query(
        "UPDATE articles SET read = 1, readTime = :readTime " +
            "WHERE (:feedId < 0 OR feedId = :feedId) AND keepUnread = 0",
    )
    suspend fun markAllRead(feedId: Long, readTime: Long)

    @Query("SELECT COUNT(*) FROM articles")
    suspend fun count(): Int
}

@Dao
interface TagDao {

    @Query("SELECT * FROM tags ORDER BY sort ASC, label ASC")
    fun observeAll(): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags")
    suspend fun all(): List<TagEntity>

    @Upsert
    suspend fun upsertAll(tags: List<TagEntity>)

    /** Mantém estados fixos (1 = estrelados, 9 = sistema); remove pastas/labels obsoletos. */
    @Query("DELETE FROM tags WHERE syncTime <> :syncTime AND type NOT IN (1, 9)")
    suspend fun deleteNotSynced(syncTime: Long)

    /** Pastas (type 11) somam o não lido dos feeds. */
    @Query(
        "UPDATE tags SET unreadCount = COALESCE((" +
            "SELECT SUM(f.unreadCount) FROM feeds f, tag2sub t2s " +
            "WHERE t2s.tagUid = tags.uid AND f.id = t2s.feedId" +
            "), 0) WHERE type = 11",
    )
    suspend fun updateFolderUnreadCounts()
}

@Dao
interface Tag2SubDao {

    @Query("SELECT * FROM tag2sub")
    suspend fun all(): List<Tag2SubEntity>

    @Query("SELECT * FROM tag2sub")
    fun observeAll(): Flow<List<Tag2SubEntity>>

    @Upsert
    suspend fun upsertAll(items: List<Tag2SubEntity>)

    @Query("DELETE FROM tag2sub WHERE syncTime <> :syncTime")
    suspend fun deleteNotSynced(syncTime: Long)

    @Query("DELETE FROM tag2sub WHERE feedId NOT IN (SELECT id FROM feeds)")
    suspend fun deleteOrphans()

    @Query("DELETE FROM tag2sub WHERE feedId = :feedId")
    suspend fun deleteByFeed(feedId: Long)
}

@Dao
interface SyncOperationDao {

    @Insert
    suspend fun insert(operation: SyncOperationEntity): Long

    @Query("SELECT * FROM sync_operations WHERE done = 0 ORDER BY createdAt ASC")
    suspend fun pending(): List<SyncOperationEntity>

    @Query("UPDATE sync_operations SET done = 1 WHERE id = :id")
    suspend fun markDone(id: Long)

    @Query("DELETE FROM sync_operations WHERE done = 1")
    suspend fun purgeDone()
}
