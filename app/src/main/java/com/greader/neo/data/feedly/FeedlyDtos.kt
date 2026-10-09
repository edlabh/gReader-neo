package com.greader.neo.data.feedly

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTOs do Feedly v3 (campos mapeados do cliente original — Relatório 02).
 */

@Serializable
data class FeedlyProfile(
    val id: String,
    val email: String? = null,
)

@Serializable
data class FeedlyCategory(
    val id: String,
    val label: String? = null,
)

@Serializable
data class FeedlySubscription(
    val id: String,
    val title: String? = null,
    val website: String? = null,
    @SerialName("visualUrl") val visualUrl: String? = null,
    val updated: Long? = null,
    val categories: List<FeedlyCategory> = emptyList(),
)

@Serializable
data class FeedlyTag(
    val id: String,
    val label: String? = null,
)

@Serializable
data class FeedlyStreamResponse(
    val id: String? = null,
    val continuation: String? = null,
    val items: List<FeedlyItem> = emptyList(),
)

@Serializable
data class FeedlyItem(
    val id: String,
    val title: String? = null,
    val unread: Boolean? = null,
    val published: Long? = null,
    val crawled: Long? = null,
    val author: String? = null,
    val summary: FeedlyContent? = null,
    val content: FeedlyContent? = null,
    val alternate: List<FeedlyLink> = emptyList(),
    val enclosure: List<FeedlyEnclosure> = emptyList(),
    val visual: FeedlyVisual? = null,
    val origin: FeedlyOrigin? = null,
    val categories: List<FeedlyCategoryRef> = emptyList(),
    val tags: List<FeedlyCategoryRef> = emptyList(),
)

/** Categorias/tags de um item vêm como objetos {id, label} (comprovado no parser original). */
@Serializable
data class FeedlyCategoryRef(
    val id: String,
    val label: String? = null,
)

@Serializable
data class FeedlyOrigin(
    val streamId: String? = null,
    val title: String? = null,
    val htmlUrl: String? = null,
)

@Serializable
data class FeedlyContent(val content: String? = null)

@Serializable
data class FeedlyLink(val href: String? = null, val type: String? = null)

@Serializable
data class FeedlyEnclosure(val href: String? = null, val type: String? = null)

@Serializable
data class FeedlyVisual(val url: String? = null)

/** POST /v3/markers — mesmos payloads do app original. */
@Serializable
data class FeedlyMarkerRequest(
    val action: String,
    val type: String,
    val entryIds: List<String>? = null,
    val feedIds: List<String>? = null,
    val categoryIds: List<String>? = null,
    val asOf: Long? = null,
)

@Serializable
data class FeedlyIdsResponse(
    val ids: List<String> = emptyList(),
    val continuation: String? = null,
)

@Serializable
data class FeedlyTokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String? = null,
    @SerialName("expires_in") val expiresIn: Long? = null,
)

// ---------- Busca e assinatura de feeds ----------

@Serializable
data class FeedlySearchResponse(
    val results: List<FeedlyFeedSearchResult> = emptyList(),
)

@Serializable
data class FeedlyFeedSearchResult(
    val feedId: String,
    val title: String? = null,
    val description: String? = null,
    val subscribers: Long? = null,
    val website: String? = null,
    val visualUrl: String? = null,
)

@Serializable
data class FeedlySubscriptionRequest(
    val id: String,
    val title: String? = null,
    val categories: List<FeedlyCategory> = emptyList(),
)

/**
 * Atualização de assinatura (renomear/mover) — mesmo contrato do app original:
 * POST /v3/subscriptions com id + title (+ updated) + categories (substitui as pastas).
 */
@Serializable
data class FeedlySubscriptionUpdate(
    val id: String,
    val title: String,
    val updated: Long? = null,
    val categories: List<FeedlyCategory> = emptyList(),
)
