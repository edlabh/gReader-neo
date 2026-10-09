package com.greader.neo.data.prefs

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Filtro salvo pelo usuário: escopo (feed/pasta/todos) + busca + opções de exibição.
 * `feedId` tem prioridade sobre `folderUid`; ambos nulos = todos os artigos.
 */
@Serializable
data class SavedFilter(
    val name: String,
    val feedId: Long? = null,
    val folderUid: String? = null,
    val query: String = "",
    val unreadOnly: Boolean = false,
    val sortOldest: Boolean = false,
)

/** Codec JSON da lista de filtros salvos (persistida no DataStore). */
object SavedFiltersCodec {

    private val json = Json { ignoreUnknownKeys = true }

    fun encode(filters: List<SavedFilter>): String = json.encodeToString(filters)

    /** Decodifica com tolerância: JSON ausente/corrompido vira lista vazia. */
    fun decode(raw: String?): List<SavedFilter> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching { json.decodeFromString<List<SavedFilter>>(raw) }.getOrDefault(emptyList())
    }
}
