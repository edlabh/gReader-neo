package com.greader.neo.ui.subscribe

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.greader.neo.R
import com.greader.neo.app
import com.greader.neo.data.db.TagEntity
import com.greader.neo.data.feedly.FeedlyCategory
import com.greader.neo.data.feedly.FeedlyFeedSearchResult
import com.greader.neo.data.feedly.FeedlyItem
import com.greader.neo.data.feedly.FeedlySubscriptionRequest
import com.greader.neo.data.sync.SyncScheduler
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Assinatura de feeds com prévia: tocar num resultado abre os últimos artigos
 * para conferir o feed; o botão Assinar pergunta em qual pasta ele entra
 * (padrão: sem pasta).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscribeScreen(initialQuery: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val app = context.app
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var query by remember { mutableStateOf(initialQuery) }
    var results by remember { mutableStateOf<List<FeedlyFeedSearchResult>>(emptyList()) }
    var searching by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    // Prévia do feed selecionado
    var preview by remember { mutableStateOf<FeedlyFeedSearchResult?>(null) }
    var previewItems by remember { mutableStateOf<List<FeedlyItem>>(emptyList()) }
    var previewLoading by remember { mutableStateOf(false) }
    var previewError by remember { mutableStateOf<String?>(null) }

    // Diálogo de pasta (antes de confirmar a assinatura)
    var subscribing by remember { mutableStateOf<FeedlyFeedSearchResult?>(null) }

    val folders by app.database.tagDao().observeAll()
        .collectAsStateWithLifecycle(initialValue = emptyList())

    fun search() {
        if (query.isBlank()) return
        searching = true
        message = null
        scope.launch {
            runCatching {
                val token = app.authManager.validAccessToken()
                    ?: throw IllegalStateException(context.getString(R.string.login_required))
                app.feedlyApi.searchFeeds("OAuth $token", query)
            }
                .onSuccess { response ->
                    results = response.results
                    if (results.isEmpty()) message = context.getString(R.string.subscribe_none)
                }
                .onFailure { message = it.message }
            searching = false
        }
    }

    LaunchedEffect(Unit) {
        if (initialQuery.isNotBlank()) search()
    }

    // Carrega os últimos artigos do feed em prévia.
    LaunchedEffect(preview?.feedId) {
        val target = preview ?: return@LaunchedEffect
        previewLoading = true
        previewError = null
        previewItems = emptyList()
        runCatching {
            val token = app.authManager.validAccessToken()
                ?: throw IllegalStateException(context.getString(R.string.login_required))
            app.feedlyApi.streamContents(
                authorization = "OAuth $token",
                streamId = target.feedId,
                count = 15,
            )
        }
            .onSuccess { previewItems = it.items }
            .onFailure { previewError = it.message }
        previewLoading = false
    }

    fun subscribe(result: FeedlyFeedSearchResult, folder: TagEntity?) {
        subscribing = null
        scope.launch {
            runCatching {
                val token = app.authManager.validAccessToken()
                    ?: throw IllegalStateException(context.getString(R.string.login_required))
                val response = app.feedlyApi.addSubscription(
                    "OAuth $token",
                    FeedlySubscriptionRequest(
                        id = result.feedId,
                        title = result.title,
                        categories = folder?.let {
                            listOf(FeedlyCategory(id = it.uid, label = it.label))
                        } ?: emptyList(),
                    ),
                )
                if (!response.isSuccessful) throw IllegalStateException("HTTP ${response.code()}")
            }
                .onSuccess {
                    snackbarHostState.showSnackbar(context.getString(R.string.subscribe_added))
                    SyncScheduler.enqueueNow(context)
                    preview = null
                }
                .onFailure {
                    snackbarHostState.showSnackbar(
                        it.message ?: context.getString(R.string.subscribe_failed),
                    )
                }
        }
    }

    // Voltar: se está na prévia, volta para os resultados da pesquisa.
    BackHandler(enabled = preview != null) {
        preview = null
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val (ok, total) = app.opmlManager.importIntoFeedly(uri)
                snackbarHostState.showSnackbar(
                    context.getString(R.string.opml_imported, ok, total),
                )
                SyncScheduler.enqueueNow(context)
            }
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/xml"),
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val feeds = app.database.feedDao().all()
                app.opmlManager.export(uri, feeds)
                snackbarHostState.showSnackbar(
                    context.getString(R.string.opml_exported, feeds.size),
                )
            }
        }
    }

    val currentPreview = preview
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentPreview?.title
                            ?: stringResource(R.string.subscribe_title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (currentPreview != null) {
                                preview = null
                            } else {
                                onBack()
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                        )
                    }
                },
                actions = {
                    if (currentPreview == null) {
                        TextButton(onClick = {
                            importLauncher.launch(arrayOf("text/xml", "application/xml", "*/*"))
                        }) {
                            Text(stringResource(R.string.subscribe_import))
                        }
                        TextButton(onClick = { exportLauncher.launch("greader-neo.opml") }) {
                            Text(stringResource(R.string.subscribe_export))
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (currentPreview != null) {
                Surface(tonalElevation = 3.dp) {
                    Box(Modifier.fillMaxWidth().padding(16.dp)) {
                        Button(
                            onClick = { subscribing = currentPreview },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = MaterialTheme.shapes.large,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.subscribe_add))
                        }
                    }
                }
            }
        },
    ) { padding ->
        if (currentPreview == null) {
            SearchContent(
                padding = padding,
                query = query,
                onQueryChange = { query = it },
                searching = searching,
                message = message,
                results = results,
                onSearch = { search() },
                onPreview = { preview = it },
                onSubscribeClick = { subscribing = it },
            )
        } else {
            FeedPreview(
                padding = padding,
                result = currentPreview,
                items = previewItems,
                loading = previewLoading,
                error = previewError,
            )
        }
    }

    // Pergunta em qual pasta o feed entra (padrão: sem pasta).
    subscribing?.let { result ->
        FolderPickerDialog(
            feedTitle = result.title ?: result.feedId,
            folders = folders.filter { it.type == 11 },
            onDismiss = { subscribing = null },
            onConfirm = { folder -> subscribe(result, folder) },
        )
    }
}

@Composable
private fun SearchContent(
    padding: androidx.compose.foundation.layout.PaddingValues,
    query: String,
    onQueryChange: (String) -> Unit,
    searching: Boolean,
    message: String?,
    results: List<FeedlyFeedSearchResult>,
    onSearch: () -> Unit,
    onPreview: (FeedlyFeedSearchResult) -> Unit,
    onSubscribeClick: (FeedlyFeedSearchResult) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text(stringResource(R.string.subscribe_search_hint)) },
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )
        Button(
            onClick = onSearch,
            enabled = !searching && query.isNotBlank(),
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Text(stringResource(R.string.subscribe_search))
        }
        if (searching) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            )
        }
        message?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp),
            )
        }
        if (!searching && message == null && query.isBlank()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.subscribe_idle_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(32.dp),
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(results, key = { it.feedId }) { result ->
                    SearchResultRow(
                        result = result,
                        onPreview = { onPreview(result) },
                        onSubscribe = { onSubscribeClick(result) },
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedPreview(
    padding: androidx.compose.foundation.layout.PaddingValues,
    result: FeedlyFeedSearchResult,
    items: List<FeedlyItem>,
    loading: Boolean,
    error: String?,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
    ) {
        // Cabeçalho do feed
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!result.visualUrl.isNullOrBlank()) {
                AsyncImage(
                    model = result.visualUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp)),
                )
                Spacer(Modifier.width(12.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = result.title ?: result.feedId,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                result.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                result.subscribers?.let { count ->
                    Text(
                        text = stringResource(
                            R.string.subscribe_subscribers,
                            String.format("%,d", count),
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
        HorizontalDivider(Modifier.padding(horizontal = 16.dp))

        Text(
            text = stringResource(R.string.subscribe_preview_latest),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 4.dp),
        )

        when {
            loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            error != null -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.subscribe_preview_unavailable),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            items.isEmpty() -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.subscribe_preview_empty),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items, key = { it.id }) { item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                    ) {
                        Text(
                            text = item.title ?: "(sem título)",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        item.published?.let { published ->
                            Text(
                                text = previewDate(published),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                    )
                }
            }
        }
    }
}

@Composable
private fun FolderPickerDialog(
    feedTitle: String,
    folders: List<TagEntity>,
    onDismiss: () -> Unit,
    onConfirm: (TagEntity?) -> Unit,
) {
    var selectedUid by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.subscribe_folder_dialog_title)) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.subscribe_folder_dialog_message, feedTitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                ) {
                    FilterChip(
                        selected = selectedUid == null,
                        onClick = { selectedUid = null },
                        label = { Text(stringResource(R.string.folder_none)) },
                    )
                    folders.forEach { folder ->
                        FilterChip(
                            selected = selectedUid == folder.uid,
                            onClick = { selectedUid = folder.uid },
                            label = { Text(folder.label) },
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(folders.firstOrNull { it.uid == selectedUid }) },
            ) {
                Text(stringResource(R.string.subscribe_add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}

@Composable
private fun SearchResultRow(
    result: FeedlyFeedSearchResult,
    onPreview: () -> Unit,
    onSubscribe: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onPreview)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!result.visualUrl.isNullOrBlank()) {
            AsyncImage(
                model = result.visualUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(6.dp)),
            )
            Spacer(Modifier.width(12.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(
                text = result.title ?: result.feedId,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            result.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Button(onClick = onSubscribe) {
            Text(stringResource(R.string.subscribe_add))
        }
    }
    HorizontalDivider()
}

private fun previewDate(epochMillis: Long): String {
    if (epochMillis <= 0) return ""
    return runCatching {
        Instant.ofEpochMilli(epochMillis)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"))
    }.getOrDefault("")
}
