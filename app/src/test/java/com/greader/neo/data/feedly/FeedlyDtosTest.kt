package com.greader.neo.data.feedly

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Regressão do bug corrigido no sync: categorias/tags de itens vindos do Feedly
 * são objetos `{id, label}` (não strings) — validado no parser do app original.
 * Também garante que o corpo de assinatura continua serializando nesse formato.
 */
class FeedlyDtosTest {

    private val json = Json { ignoreUnknownKeys = true }

    private val streamJson = """
        {
          "id": "user/1/category/global.all",
          "continuation": "abc123",
          "items": [
            {
              "id": "entry-1",
              "title": "Um título",
              "unread": true,
              "published": 1700000000000,
              "crawled": 1700000001000,
              "author": "Autor",
              "summary": { "content": "<p>resumo</p>" },
              "content": { "content": "<p>completo</p>" },
              "alternate": [ { "href": "https://example.com/a", "type": "text/html" } ],
              "visual": { "url": "https://example.com/img.jpg" },
              "origin": { "streamId": "feed/https://example.com/feed", "title": "Exemplo" },
              "categories": [ { "id": "user/1/category/tech", "label": "Tecnologia" } ],
              "tags": [ { "id": "user/1/tag/global.saved", "label": "saved" } ]
            }
          ]
        }
    """.trimIndent()

    @Test
    fun streamItemsDeserializeWithObjectCategoriesAndTags() {
        val page = json.decodeFromString<FeedlyStreamResponse>(streamJson)

        assertEquals("abc123", page.continuation)
        assertEquals(1, page.items.size)

        val item = page.items.first()
        assertEquals("entry-1", item.id)
        assertEquals("feed/https://example.com/feed", item.origin?.streamId)
        assertEquals("Tecnologia", item.categories.singleOrNull()?.label)
        assertTrue((item.categories + item.tags).any { it.id.endsWith("/global.saved") })
        assertEquals("<p>resumo</p>", item.summary?.content)
        assertEquals("https://example.com/img.jpg", item.visual?.url)
    }

    @Test
    fun itemWithoutOptionalFieldsUsesDefaults() {
        val page = json.decodeFromString<FeedlyStreamResponse>("""{"items":[{"id":"e2"}]}""")

        val item = page.items.single()
        assertTrue(item.categories.isEmpty())
        assertTrue(item.tags.isEmpty())
        assertEquals(null, item.origin)
        assertEquals(null, item.unread)
    }

    @Test
    fun subscriptionUpdateSerializesCategoriesAsObjects() {
        val body = FeedlySubscriptionUpdate(
            id = "feed/https://example.com/feed",
            title = "Exemplo",
            updated = 1700000000000,
            categories = listOf(FeedlyCategory(id = "user/1/category/tech", label = "Tecnologia")),
        )

        val encoded = json.encodeToString(FeedlySubscriptionUpdate.serializer(), body)

        assertTrue(encoded.contains("\"categories\":["))
        assertTrue(encoded.contains("""{"id":"user/1/category/tech","label":"Tecnologia"}"""))
    }
}
