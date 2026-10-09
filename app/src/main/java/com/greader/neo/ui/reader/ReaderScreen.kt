package com.greader.neo.ui.reader

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greader.neo.R
import com.greader.neo.app
import com.greader.neo.data.db.ArticleEntity
import com.greader.neo.data.prefs.AppSettings
import com.greader.neo.data.reader.ExtractedArticle
import kotlinx.coroutines.launch

private enum class ReaderMode { FEED, READING, WEB }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(articleId: Long, onBack: () -> Unit) {
    val context = LocalContext.current
    val app = context.app
    val scope = rememberCoroutineScope()
    val dark = isSystemInDarkTheme()

    // Preferência: navegador embutido (padrão) ou externo.
    val settings by app.settings.settings
        .collectAsStateWithLifecycle(initialValue = AppSettings())
    val embedded = settings.webViewEmbedded

    var article by remember { mutableStateOf<ArticleEntity?>(null) }
    var feedTitle by remember { mutableStateOf<String?>(null) }
    var mode by remember { mutableStateOf(ReaderMode.FEED) }
    var extracted by remember { mutableStateOf<ExtractedArticle?>(null) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    // Voltar: navega no histórico do WebView embutido antes de sair da tela.
    BackHandler {
        val webView = webViewRef
        if (embedded && webView != null && webView.canGoBack()) {
            webView.goBack()
        } else {
            onBack()
        }
    }

    // Carrega o artigo e marca como lido (offline-first: local + outbox).
    LaunchedEffect(articleId) {
        val loaded = app.database.articleDao().byId(articleId)
        article = loaded
        feedTitle = loaded?.let { app.database.feedDao().byId(it.feedId)?.title }
        if (loaded != null && !loaded.read) {
            app.articleActions.setRead(loaded.id, loaded.uid, true)
            article = loaded.copy(read = true, readTime = System.currentTimeMillis() / 1000)
        }
    }

    // Extração do modo leitura (Readability4J), sob demanda.
    LaunchedEffect(mode, article?.id) {
        val current = article ?: return@LaunchedEffect
        val link = current.link
        if (mode == ReaderMode.READING && extracted == null && !link.isNullOrBlank()) {
            loading = true
            error = null
            runCatching { app.articleExtractor.extract(link) }
                .onSuccess { extracted = it }
                .onFailure { error = it.message ?: "Falha na extração" }
            loading = false
        }
    }

    val current = article
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = current?.title ?: "",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                        )
                    }
                },
                actions = {
                    current?.let { item ->
                        IconButton(onClick = {
                            scope.launch {
                                app.articleActions.setStarred(item.id, item.uid, !item.starred)
                                article = item.copy(starred = !item.starred)
                            }
                        }) {
                            Icon(
                                imageVector = if (item.starred) {
                                    Icons.Filled.Star
                                } else {
                                    Icons.Filled.StarBorder
                                },
                                contentDescription = stringResource(R.string.cd_star),
                                tint = if (item.starred) {
                                    MaterialTheme.colorScheme.tertiary
                                } else {
                                    LocalContentColor.current
                                },
                            )
                        }
                        IconButton(onClick = { shareArticle(context, item) }) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = stringResource(R.string.cd_share),
                            )
                        }
                        if (!item.link.isNullOrBlank()) {
                            IconButton(onClick = {
                                if (embedded) {
                                    mode = ReaderMode.WEB
                                } else {
                                    openInBrowser(context, item.link)
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = stringResource(R.string.cd_browser),
                                )
                            }
                        }
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (current == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                ReaderMode.entries.forEachIndexed { index, entry ->
                    SegmentedButton(
                        selected = mode == entry,
                        onClick = {
                            if (entry == ReaderMode.WEB && !embedded) {
                                // Navegador externo: abre direto, sem trocar de modo.
                                current.link?.let { openInBrowser(context, it) }
                            } else {
                                mode = entry
                            }
                        },
                        shape = SegmentedButtonDefaults.itemShape(index, ReaderMode.entries.size),
                    ) {
                        Text(
                            when (entry) {
                                ReaderMode.FEED -> stringResource(R.string.reader_mode_feed)
                                ReaderMode.READING -> stringResource(R.string.reader_mode_reading)
                                ReaderMode.WEB -> stringResource(R.string.reader_mode_web)
                            },
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            when (mode) {
                ReaderMode.FEED -> ArticleWebView(
                    html = buildReaderHtml(
                        title = current.title,
                        author = current.author,
                        feedTitle = feedTitle,
                        publishedTime = current.publishedTime,
                        contentHtml = current.content ?: "<p>${current.title}</p>",
                        dark = dark,
                    ),
                    baseUrl = current.link,
                    jsEnabled = false,
                    allowInternalNavigation = embedded,
                    onCreated = { webViewRef = it },
                )

                ReaderMode.READING -> when {
                    loading -> Centered { CircularProgressIndicator() }
                    error != null -> Centered {
                        Text(
                            text = stringResource(R.string.reader_extract_failed),
                            textAlign = TextAlign.Center,
                        )
                    }

                    extracted != null -> {
                        val result = extracted!!
                        ArticleWebView(
                            html = buildReaderHtml(
                                title = result.title,
                                author = result.byline ?: current.author,
                                feedTitle = feedTitle,
                                publishedTime = current.publishedTime,
                                contentHtml = result.html,
                                dark = dark,
                            ),
                            baseUrl = current.link,
                            jsEnabled = false,
                            allowInternalNavigation = embedded,
                            onCreated = { webViewRef = it },
                        )
                    }

                    else -> Centered { Text(stringResource(R.string.reader_no_link)) }
                }

                ReaderMode.WEB -> if (current.link.isNullOrBlank()) {
                    Centered { Text(stringResource(R.string.reader_no_link)) }
                } else {
                    ArticleWebView(
                        url = current.link,
                        jsEnabled = true,
                        allowInternalNavigation = embedded,
                        onCreated = { webViewRef = it },
                    )
                }
            }
        }
    }
}

@Composable
private fun Centered(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) { content() }
}

@Composable
private fun ArticleWebView(
    html: String? = null,
    url: String? = null,
    baseUrl: String? = null,
    jsEnabled: Boolean,
    allowInternalNavigation: Boolean,
    onCreated: (WebView) -> Unit = {},
) {
    val context = LocalContext.current
    var loadedKey by remember { mutableStateOf<String?>(null) }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = jsEnabled
                settings.domStorageEnabled = false
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        request: WebResourceRequest,
                    ): Boolean {
                        val target = request.url.toString()
                        if (!target.startsWith("http")) return false
                        return if (allowInternalNavigation) {
                            // Navegador embutido: continua no próprio WebView.
                            false
                        } else {
                            // Navegador externo.
                            openInBrowser(context, target)
                            true
                        }
                    }
                }
                onCreated(this)
            }
        },
        update = { webView ->
            webView.settings.javaScriptEnabled = jsEnabled
            val key = url ?: html
            if (key != null && key != loadedKey) {
                loadedKey = key
                if (url != null) {
                    webView.loadUrl(url)
                } else {
                    webView.loadDataWithBaseURL(baseUrl, html.orEmpty(), "text/html", "utf-8", null)
                }
            }
        },
    )
}

private fun shareArticle(context: Context, article: ArticleEntity) {
    val intent = Intent(Intent.ACTION_SEND)
        .setType("text/plain")
        .putExtra(Intent.EXTRA_SUBJECT, article.title)
        .putExtra(Intent.EXTRA_TEXT, article.link ?: article.title)
    context.startActivity(Intent.createChooser(intent, null))
}

private fun openInBrowser(context: Context, url: String) {
    runCatching {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}
