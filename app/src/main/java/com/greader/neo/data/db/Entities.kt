package com.greader.neo.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Modelo de dados baseado no schema do gReader original (Relatório 03),
 * adaptado para Room e para o escopo do novo app (sem áudio/vídeo/TTS).
 */

@Entity(tableName = "feeds", indices = [Index(value = ["uid"], unique = true)])
data class FeedEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val uid: String,
    val title: String,
    val feedUrl: String? = null,
    val htmlUrl: String? = null,
    val iconUrl: String? = null,
    val sort: Int = 0,
    val unreadCount: Int = 0,
    val newestItemTime: Long = 0,
    val syncTime: Long = 0,
    val syncExcluded: Boolean = false,
    val notification: Boolean = false,
    val hidden: Boolean = false,
)

@Entity(tableName = "tags", indices = [Index(value = ["uid"], unique = true)])
data class TagEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val uid: String,
    /** 1 = estrelados, 9 = estado fixo, 10 = pasta, 11 = pasta com feeds, 12 = label */
    val type: Int,
    val label: String,
    val sort: Int = 0,
    val unreadCount: Int = 0,
    val syncTime: Long = 0,
    val hidden: Boolean = false,
)

@Entity(tableName = "tag2sub", indices = [Index(value = ["tagUid", "feedId"], unique = true)])
data class Tag2SubEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tagUid: String,
    val feedId: Long,
    val syncTime: Long = 0,
)

@Entity(
    tableName = "articles",
    indices = [Index(value = ["uid"], unique = true), Index(value = ["feedId"])],
)
data class ArticleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val feedId: Long,
    val uid: String,
    val title: String,
    val content: String? = null,
    val author: String? = null,
    val link: String? = null,
    val image: String? = null,
    val publishedTime: Long = 0,
    val updatedTime: Long = 0,
    val starred: Boolean = false,
    val cached: Boolean = false,
    val read: Boolean = false,
    val readTime: Long = 0,
    /** Protege o artigo de "marcar tudo como lido" (keep unread do Feedly). */
    val keepUnread: Boolean = false,
    val syncTime: Long = 0,
)

@Entity(tableName = "tag2article", indices = [Index(value = ["tagUid", "articleUid"], unique = true)])
data class Tag2ArticleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tagUid: String,
    val articleUid: String,
    /** 0 = sincronizado, 1 = adicionar, 2 = remover */
    val action: Int = 0,
    val syncTime: Long = 0,
)

/**
 * Outbox de sincronização (substitui a tabela `history` do app original).
 * Tipos mapeados do Relatório 02; parâmetros em formato compacto chave=valor.
 */
@Entity(tableName = "sync_operations", indices = [Index(value = ["done"])])
data class SyncOperationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: Int,
    val params: String,
    val createdAt: Long = System.currentTimeMillis(),
    val done: Boolean = false,
)

object SyncOperationType {
    const val MARK_ALL_READ = 1
    const val TOGGLE_READ = 2
    const val TOGGLE_STARRED = 3
    const val TOGGLE_KEEP_UNREAD = 4
    const val EDIT_SUBSCRIPTION = 5
    const val RENAME_TAG = 6
    const val DELETE_TAG = 7
    const val EDIT_ARTICLE_TAG = 8
}
