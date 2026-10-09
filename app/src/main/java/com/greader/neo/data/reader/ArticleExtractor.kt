package com.greader.neo.data.reader

import com.greader.neo.data.sharedHttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.dankito.readability4j.Readability4J
import okhttp3.Request
import java.io.IOException

data class ExtractedArticle(
    val title: String,
    val html: String,
    val byline: String?,
)

/**
 * Extração do conteúdo principal do artigo (modo leitura),
 * usando a Readability4J — o mesmo motor do gReader original (Relatório 01).
 */
class ArticleExtractor {

    suspend fun extract(url: String): ExtractedArticle = withContext(Dispatchers.IO) {
        val html = fetch(url)
        val article = Readability4J(url, html).parse()
        val content = article.content
        if (content.isNullOrBlank()) {
            throw IllegalStateException("Não foi possível extrair o conteúdo")
        }
        ExtractedArticle(
            title = article.title.orEmpty(),
            html = content,
            byline = article.byline,
        )
    }

    private fun fetch(url: String): String {
        val request = Request.Builder()
            .url(url)
            .header(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 15) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Mobile Safari/537.36",
            )
            .build()
        sharedHttpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("HTTP ${response.code}")
            return response.body.string()
        }
    }
}
