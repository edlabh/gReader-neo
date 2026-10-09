package com.greader.neo.data

import com.greader.neo.data.db.NeoDatabase
import com.greader.neo.data.db.SyncOperationEntity
import com.greader.neo.data.db.SyncOperationType

/**
 * Ações do usuário sobre artigos: atualizam o estado local imediatamente
 * e enfileiram a operação na outbox para o próximo sync (padrão offline-first
 * do gReader original — Relatório 02).
 */
class ArticleActions(private val db: NeoDatabase) {

    suspend fun setRead(articleId: Long, uid: String, read: Boolean) {
        db.articleDao().updateReadState(articleId, read, System.currentTimeMillis() / 1000)
        db.syncOperationDao().insert(
            SyncOperationEntity(
                type = SyncOperationType.TOGGLE_READ,
                params = "uid=$uid&read=${if (read) 1 else 0}",
            ),
        )
    }

    /**
     * "Manter não lido" (flag local do gReader original): marca como não lido e
     * impede que marcações automáticas (sync/marcar tudo) o leiam de novo.
     * Ao desligar a flag, o artigo volta a ser considerado lido.
     */
    suspend fun setKeepUnread(articleId: Long, uid: String, keepUnread: Boolean) {
        val read = !keepUnread
        db.articleDao().updateKeepUnread(articleId, keepUnread)
        db.articleDao().updateReadState(articleId, read, System.currentTimeMillis() / 1000)
        db.syncOperationDao().insert(
            SyncOperationEntity(
                type = SyncOperationType.TOGGLE_READ,
                params = "uid=$uid&read=${if (read) 1 else 0}",
            ),
        )
    }

    suspend fun setStarred(articleId: Long, uid: String, starred: Boolean) {
        db.articleDao().updateStarred(articleId, starred)
        db.syncOperationDao().insert(
            SyncOperationEntity(
                type = SyncOperationType.TOGGLE_STARRED,
                params = "uid=$uid&starred=${if (starred) 1 else 0}",
            ),
        )
    }

    /**
     * Marca todo o escopo como lido localmente e enfileira o marker no servidor.
     * Retorna os uids que estavam não lidos (usados pelo "Desfazer").
     *
     * @param feedId id local do feed ou -1 para pasta/todos.
     * @param folderUid uid da pasta (null para feed/todos) — corrige o escopo por pasta.
     * @param streamUid uid do feed ("feed/…"), da pasta ou "global.all" para tudo.
     */
    suspend fun markAllRead(feedId: Long, folderUid: String?, streamUid: String): List<String> {
        val uids = db.articleDao().unreadUids(feedId, folderUid)
        db.articleDao().markAllRead(feedId, folderUid, System.currentTimeMillis() / 1000)
        db.syncOperationDao().insert(
            SyncOperationEntity(
                type = SyncOperationType.MARK_ALL_READ,
                params = "streamId=$streamUid",
            ),
        )
        return uids
    }

    /** Desfaz um "marcar tudo como lido": reverte apenas os uids afetados + markers em lote. */
    suspend fun undoMarkAllRead(uids: List<String>) {
        if (uids.isEmpty()) return
        uids.chunked(500).forEach { chunk ->
            db.articleDao().markUnreadByUids(chunk)
            db.syncOperationDao().insert(
                SyncOperationEntity(
                    type = SyncOperationType.MARK_UNREAD,
                    params = "ids=${chunk.joinToString(",")}",
                ),
            )
        }
    }
}
