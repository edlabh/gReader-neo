package com.greader.neo.ui.reader

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/** Gera o HTML do artigo para o WebView (feed, modo leitura ou fallback). */
fun buildReaderHtml(
    title: String,
    author: String?,
    feedTitle: String?,
    publishedTime: Long,
    contentHtml: String,
    dark: Boolean,
): String {
    val background = if (dark) "#121318" else "#FBF8FF"
    val foreground = if (dark) "#E4E1E9" else "#1A1B21"
    val muted = if (dark) "#C7C5D0" else "#46464F"
    val link = if (dark) "#BAC3FF" else "#4C5BD4"
    val codeBackground = if (dark) "#1E1F26" else "#F0EEF6"
    val border = if (dark) "#33343D" else "#E3E1EC"

    val meta = listOfNotNull(
        feedTitle,
        author,
        formatDate(publishedTime),
    ).joinToString(" · ") { escapeHtml(it) }

    return """
        <!DOCTYPE html>
        <html>
        <head>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <style>
          :root { color-scheme: ${if (dark) "dark" else "light"}; }
          * { box-sizing: border-box; }
          body {
            margin: 0 auto; padding: 20px 20px 64px; max-width: 720px;
            background: $background; color: $foreground;
            font-family: Roboto, 'Segoe UI', sans-serif;
            font-size: 17px; line-height: 1.7;
            word-wrap: break-word; overflow-wrap: anywhere;
            -webkit-text-size-adjust: 100%;
          }
          h1 {
            font-size: 1.55em; line-height: 1.25; letter-spacing: -0.015em;
            margin: 0 0 10px; font-weight: 700;
          }
          .meta {
            color: $muted; font-size: 0.85em; margin-bottom: 24px;
            padding-bottom: 16px; border-bottom: 1px solid $border;
          }
          h2, h3, h4 { line-height: 1.3; letter-spacing: -0.01em; margin: 1.4em 0 0.5em; }
          p { margin: 0 0 1em; }
          img, video, iframe { max-width: 100%; height: auto; border-radius: 12px; }
          figure { margin: 20px 0; }
          figcaption { font-size: 0.8em; color: $muted; margin-top: 6px; }
          a { color: $link; text-decoration: none; }
          pre {
            background: $codeBackground; border-radius: 12px;
            padding: 14px 16px; overflow-x: auto;
            font-size: 0.85em; line-height: 1.5;
          }
          code { background: $codeBackground; border-radius: 6px; padding: 2px 6px; font-size: 0.9em; }
          pre code { background: none; padding: 0; }
          blockquote {
            margin: 20px 0; padding: 4px 18px;
            border-left: 3px solid $link; color: $muted; font-style: italic;
          }
          hr { border: none; border-top: 1px solid $border; margin: 28px 0; }
          table { max-width: 100%; border-collapse: collapse; }
          td, th { padding: 6px 10px; border: 1px solid $border; }
        </style>
        </head>
        <body>
          <h1>${escapeHtml(title)}</h1>
          <div class="meta">$meta</div>
          $contentHtml
        </body>
        </html>
    """.trimIndent()
}

private fun formatDate(epochSeconds: Long): String? {
    if (epochSeconds <= 0) return null
    return runCatching {
        Instant.ofEpochSecond(epochSeconds)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT))
    }.getOrNull()
}

private fun escapeHtml(value: String): String = value
    .replace("&", "&amp;")
    .replace("<", "&lt;")
    .replace(">", "&gt;")
    .replace("\"", "&quot;")
