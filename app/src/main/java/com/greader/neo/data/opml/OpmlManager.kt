package com.greader.neo.data.opml

import android.content.Context
import android.net.Uri
import android.util.Xml
import com.greader.neo.data.auth.AuthManager
import com.greader.neo.data.db.FeedEntity
import com.greader.neo.data.feedly.FeedlyApi
import com.greader.neo.data.feedly.FeedlySubscriptionRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import java.io.OutputStreamWriter

data class OpmlEntry(
    val title: String?,
    val xmlUrl: String,
    val htmlUrl: String?,
)

/** Importação/exportação de OPML (SAF), integrando com o Feedly. */
class OpmlManager(
    private val context: Context,
    private val api: FeedlyApi,
    private val auth: AuthManager,
) {

    suspend fun read(uri: Uri): List<OpmlEntry> = withContext(Dispatchers.IO) {
        val entries = mutableListOf<OpmlEntry>()
        context.contentResolver.openInputStream(uri)?.use { stream ->
            val parser = Xml.newPullParser()
            parser.setInput(stream, null)
            var event = parser.eventType
            while (event != XmlPullParser.END_DOCUMENT) {
                if (event == XmlPullParser.START_TAG && parser.name == "outline") {
                    val xmlUrl = parser.getAttributeValue(null, "xmlUrl")
                    if (!xmlUrl.isNullOrBlank()) {
                        entries += OpmlEntry(
                            title = parser.getAttributeValue(null, "title")
                                ?: parser.getAttributeValue(null, "text"),
                            xmlUrl = xmlUrl,
                            htmlUrl = parser.getAttributeValue(null, "htmlUrl"),
                        )
                    }
                }
                event = parser.next()
            }
        }
        entries
    }

    /** Assina no Feedly cada feed do OPML. Retorna (sucessos, total). */
    suspend fun importIntoFeedly(uri: Uri): Pair<Int, Int> {
        val token = auth.validAccessToken() ?: return 0 to 0
        val header = "OAuth $token"
        val entries = read(uri)
        var ok = 0
        for (entry in entries) {
            val feedId = if (entry.xmlUrl.startsWith("feed/")) {
                entry.xmlUrl
            } else {
                "feed/${entry.xmlUrl}"
            }
            val result = runCatching { api.addSubscription(header, FeedlySubscriptionRequest(feedId)) }
            if (result.getOrNull()?.isSuccessful == true) ok++
            delay(120) // gentileza com rate limit do Feedly
        }
        return ok to entries.size
    }

    /** Exporta os feeds atuais para um arquivo OPML. */
    suspend fun export(uri: Uri, feeds: List<FeedEntity>) = withContext(Dispatchers.IO) {
        context.contentResolver.openOutputStream(uri)?.use { stream ->
            val writer = OutputStreamWriter(stream, Charsets.UTF_8)
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
            writer.write("<opml version=\"2.0\">\n<head><title>gReader Neo</title></head>\n<body>\n")
            feeds.forEach { feed ->
                val xmlUrl = feed.feedUrl ?: feed.uid.removePrefix("feed/")
                val htmlAttr = feed.htmlUrl?.let { " htmlUrl=\"${escape(it)}\"" } ?: ""
                writer.write(
                    "<outline type=\"rss\" text=\"${escape(feed.title)}\" " +
                        "title=\"${escape(feed.title)}\" xmlUrl=\"${escape(xmlUrl)}\"$htmlAttr/>\n",
                )
            }
            writer.write("</body>\n</opml>\n")
            writer.flush()
        }
    }

    private fun escape(value: String): String = value
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
}
