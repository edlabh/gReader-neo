package com.greader.neo.ui.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.RemoveDone
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberSwipeToDismissBoxState
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.greader.neo.R
import com.greader.neo.SyncStatus
import com.greader.neo.app
import com.greader.neo.data.auth.AuthState
import com.greader.neo.data.db.ArticleListItem
import com.greader.neo.data.db.FeedEntity
import com.greader.neo.data.db.Tag2SubEntity
import com.greader.neo.data.db.TagEntity
import com.greader.neo.data.prefs.AppSettings
import com.greader.neo.ui.components.RssGlyph
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/** Filtro ativo da lista de artigos (painel de feeds recolhível). */
sealed interface FeedFilter {
    data object All : FeedFilter
    data class Folder(val tag: TagEntity) : FeedFilter
    data class Feed(val feed: FeedEntity) : FeedFilter
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenSettings: () -> Unit,
    onOpenArticle: (Long) -> Unit,
    onConnectFeedly: () -> Unit,
    onOpenAccount: () -> Unit,
    onOpenSubscriptions: () -> Unit,
) {
    val context = LocalContext.current
    val app = context.app
    val scope = rememberCoroutineScope()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var filter by remember { mutableStateOf<FeedFilter>(FeedFilter.All) }
    var query by remember { mutableStateOf("") }
    var searchVisible by remember { mutableStateOf(false) }
    var selection by remember { mutableStateOf(emptySet<Long>()) }
    val searchFocusRequester = remember { FocusRequester() }
    val snackbarHostState = remember { SnackbarHostState() }
    var deletingFeed by remember { mutableStateOf<FeedEntity?>(null) }
    var confirmMarkAll by remember { mutableStateOf(false) }
    var swipeUndoJob by remember { mutableStateOf<Job?>(null) }

    val feeds by app.database.feedDao().observeAll()
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val totalUnread by app.database.feedDao().observeTotalUnread()
        .collectAsStateWithLifecycle(initialValue = 0)
    val tags by app.database.tagDao().observeAll()
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val links by app.database.tag2SubDao().observeAll()
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val authState by app.authManager.state.collectAsStateWithLifecycle()
    val syncStatus by app.syncStatus.collectAsStateWithLifecycle()
    val connected = authState is AuthState.Connected

    // Preferências de densidade/fonte/modo da lista (aplicadas em tempo real).
    val settings by app.settings.settings
        .collectAsStateWithLifecycle(initialValue = AppSettings())
    val listStyle = remember(
        settings.listDensity,
        settings.listFontSizeSp,
        settings.listViewMode,
        settings.listThumbnails,
    ) {
        listStyleFor(
            settings.listDensity,
            settings.listFontSizeSp,
            compact = settings.listViewMode == 0,
            thumbnails = settings.listThumbnails,
        )
    }

    val feedId = when (val current = filter) {
        is FeedFilter.Feed -> current.feed.id
        else -> -1L
    }
    val folderUid = (filter as? FeedFilter.Folder)?.tag?.uid
    val items by app.database.articleDao()
        .observeList(feedId, folderUid, query, settings.listSortOldest, settings.listUnreadOnly)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val filterTitle = when (val current = filter) {
        FeedFilter.All -> stringResource(R.string.all_articles)
        is FeedFilter.Folder -> current.tag.label
        is FeedFilter.Feed -> current.feed.title
    }

    val selectedItems = items.filter { it.article.id in selection }

    // Mantém o widget pequeno (contador) sincronizado com leituras locais.
    LaunchedEffect(totalUnread) {
        runCatching { com.greader.neo.widget.UnreadCountWidgetProvider.refresh(context) }
    }

    fun markAllRead() {
        scope.launch {
            val streamUid = when (val current = filter) {
                FeedFilter.All -> "global.all"
                is FeedFilter.Folder -> current.tag.uid
                is FeedFilter.Feed -> current.feed.uid
            }
            app.articleActions.markAllRead(feedId, streamUid)
        }
    }

    /** Confirma antes de marcar TUDO como lido (evita toque acidental em massa). */
    fun requestMarkAllRead() {
        if (filter is FeedFilter.All) {
            confirmMarkAll = true
        } else {
            markAllRead()
        }
    }

    // Sync ao abrir (configurável), apenas quando há sessão.
    LaunchedEffect(Unit) {
        if (app.authManager.state.value is AuthState.Connected) {
            if (app.settings.current().syncOnStart) {
                app.syncNow()
            }
        }
    }

    // Permissão de notificações (Android 13+), pedida quando há sessão.
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }
    LaunchedEffect(authState) {
        if (connected) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    email = (authState as? AuthState.Connected)?.email,
                    connected = connected,
                    totalUnread = totalUnread,
                    folders = tags.filter { it.type == 11 },
                    feeds = feeds,
                    links = links,
                    filter = filter,
                    onSelect = { selected ->
                        filter = selected
                        selection = emptySet()
                        scope.launch { drawerState.close() }
                    },
                    onMarkAllRead = {
                        scope.launch { drawerState.close() }
                        requestMarkAllRead()
                    },
                    onOpenSettings = {
                        scope.launch { drawerState.close() }
                        onOpenSettings()
                    },
                    onOpenAccount = {
                        scope.launch { drawerState.close() }
                        onOpenAccount()
                    },
                    onOpenSubscriptions = {
                        scope.launch { drawerState.close() }
                        onOpenSubscriptions()
                    },
                    onDeleteFeed = { feed ->
                        scope.launch { drawerState.close() }
                        deletingFeed = feed
                    },
                    onConnect = {
                        scope.launch { drawerState.close() }
                        onConnectFeedly()
                    },
                )
            }
        },
    ) {
        Scaffold(
            topBar = {
                if (selection.isEmpty()) {
                    TopAppBar(
                        title = {
                            TopicSelector(
                                title = filterTitle,
                                viewMode = settings.listViewMode,
                                oldestFirst = settings.listSortOldest,
                                onViewMode = { mode ->
                                    scope.launch { app.settings.setListViewMode(mode) }
                                },
                                onSortOldest = { oldest ->
                                    scope.launch { app.settings.setListSortOldest(oldest) }
                                },
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = stringResource(R.string.cd_menu),
                                )
                            }
                        },
                        actions = {
                            // Sincronizar
                            IconButton(onClick = { app.syncNow() }) {
                                Icon(
                                    imageVector = Icons.Filled.Sync,
                                    contentDescription = stringResource(R.string.cd_sync),
                                    tint = if (syncStatus is SyncStatus.Running) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                )
                            }
                            // Mostrar todos / somente não lidos
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        app.settings.setListUnreadOnly(!settings.listUnreadOnly)
                                    }
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = if (settings.listUnreadOnly) {
                                        MaterialTheme.colorScheme.secondaryContainer
                                    } else {
                                        Color.Transparent
                                    },
                                    contentColor = if (settings.listUnreadOnly) {
                                        MaterialTheme.colorScheme.onSecondaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                ),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.MarkEmailUnread,
                                    contentDescription = stringResource(
                                        if (settings.listUnreadOnly) {
                                            R.string.cd_show_all
                                        } else {
                                            R.string.cd_unread_only
                                        },
                                    ),
                                )
                            }
                            // Pesquisa (abre/fecha)
                            IconButton(onClick = {
                                if (searchVisible) {
                                    searchVisible = false
                                    query = ""
                                } else {
                                    searchVisible = true
                                }
                            }) {
                                Icon(
                                    imageVector = if (searchVisible) {
                                        Icons.Filled.Close
                                    } else {
                                        Icons.Filled.Search
                                    },
                                    contentDescription = stringResource(
                                        if (searchVisible) R.string.cd_close_search
                                        else R.string.cd_search,
                                    ),
                                )
                            }
                        },
                    )
                } else {
                    TopAppBar(
                        title = { Text(stringResource(R.string.selected_count, selection.size)) },
                        navigationIcon = {
                            IconButton(onClick = { selection = emptySet() }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = stringResource(R.string.cd_clear_selection),
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                scope.launch {
                                    selectedItems.forEach {
                                        app.articleActions.setRead(it.article.id, it.article.uid, true)
                                    }
                                    selection = emptySet()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = stringResource(R.string.cd_mark_read),
                                )
                            }
                            IconButton(onClick = {
                                scope.launch {
                                    selectedItems.forEach {
                                        app.articleActions.setRead(it.article.id, it.article.uid, false)
                                    }
                                    selection = emptySet()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.RemoveDone,
                                    contentDescription = stringResource(R.string.cd_mark_unread),
                                )
                            }
                        },
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                if (!connected && feeds.isNotEmpty()) {
                    ConnectBanner(onConnectFeedly)
                }
                when (val status = syncStatus) {
                    is SyncStatus.Running -> LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                    )

                    is SyncStatus.Error -> StatusBanner(
                        message = status.message,
                        isError = true,
                        onRetry = { app.syncNow() },
                    )

                    is SyncStatus.Success -> if (status.newArticles > 0) {
                        StatusBanner(
                            message = stringResource(R.string.sync_success, status.newArticles),
                            isError = false,
                            onRetry = null,
                        )
                    }

                    SyncStatus.Idle -> Unit
                }

                if (connected && feeds.isNotEmpty() && searchVisible && selection.isEmpty()) {
                    // Foca automaticamente ao abrir a busca (teclado aparece sozinho).
                    LaunchedEffect(searchVisible) {
                        if (searchVisible) {
                            runCatching { searchFocusRequester.requestFocus() }
                        }
                    }
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text(stringResource(R.string.search_hint)) },
                        singleLine = true,
                        shape = RoundedCornerShape(28.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                        },
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = { query = "" }) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = stringResource(R.string.cd_clear_search),
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .focusRequester(searchFocusRequester),
                    )
                }

                // Puxar para baixo sincroniza (com efeito de esticamento da lista).
                PullToRefreshBox(
                    isRefreshing = syncStatus is SyncStatus.Running,
                    onRefresh = { app.syncNow() },
                    modifier = Modifier.fillMaxSize(),
                ) {
                    if (items.isEmpty()) {
                        EmptyState(
                            connected = connected,
                            hasFeeds = feeds.isNotEmpty(),
                            hasQuery = query.isNotBlank(),
                            unreadOnly = settings.listUnreadOnly,
                            onConnectFeedly = onConnectFeedly,
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(items, key = { it.article.id }) { item ->
                                Column {
                                    SwipeableArticleRow(
                                        item = item,
                                        style = listStyle,
                                        selected = item.article.id in selection,
                                        onClick = {
                                            if (selection.isEmpty()) {
                                                onOpenArticle(item.article.id)
                                            } else {
                                                selection = selection.toggle(item.article.id)
                                            }
                                        },
                                        onLongClick = { selection = selection.toggle(item.article.id) },
                                        onSwipe = {
                                            val wasRead = item.article.read
                                            val target = !wasRead
                                            // Gravação independente do snackbar (não é cancelada por ele).
                                            scope.launch {
                                                app.articleActions.setRead(
                                                    item.article.id,
                                                    item.article.uid,
                                                    target,
                                                )
                                            }
                                            swipeUndoJob?.cancel()
                                            swipeUndoJob = scope.launch {
                                                val result = snackbarHostState.showSnackbar(
                                                    message = context.getString(
                                                        if (target) {
                                                            R.string.article_marked_read
                                                        } else {
                                                            R.string.article_marked_unread
                                                        },
                                                    ),
                                                    actionLabel = context.getString(R.string.undo),
                                                    duration = SnackbarDuration.Short,
                                                )
                                                if (result == SnackbarResult.ActionPerformed) {
                                                    scope.launch {
                                                        app.articleActions.setRead(
                                                            item.article.id,
                                                            item.article.uid,
                                                            wasRead,
                                                        )
                                                    }
                                                }
                                            }
                                        },
                                    )
                                    if (settings.listDivider) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    // Confirmação de remoção de feed (toque longo no drawer), sincronizada com o Feedly.
    deletingFeed?.let { feed ->
        AlertDialog(
            onDismissRequest = { deletingFeed = null },
            title = { Text(stringResource(R.string.delete_subscription_title)) },
            text = { Text(stringResource(R.string.delete_subscription_message, feed.title)) },
            confirmButton = {
                TextButton(onClick = {
                    deletingFeed = null
                    val wasCurrentFilter = (filter as? FeedFilter.Feed)?.feed?.id == feed.id
                    // Pastas do feed capturadas antes da remoção, para o "Desfazer".
                    val tagUids = links.filter { it.feedId == feed.id }.map { it.tagUid }
                    scope.launch {
                        app.subscriptionActions.delete(feed)
                            .onSuccess {
                                if (wasCurrentFilter) filter = FeedFilter.All
                                val result = snackbarHostState.showSnackbar(
                                    message = context.getString(R.string.subscription_deleted),
                                    actionLabel = context.getString(R.string.undo),
                                    duration = SnackbarDuration.Long,
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    app.subscriptionActions.restore(feed, tagUids)
                                        .onSuccess {
                                            if (wasCurrentFilter) filter = FeedFilter.Feed(feed)
                                            // Reabre a janela de sync para trazer os artigos de volta.
                                            app.settings.setLastSyncTime(0)
                                            app.syncNow()
                                            snackbarHostState.showSnackbar(
                                                context.getString(R.string.subscription_restored),
                                            )
                                        }
                                        .onFailure {
                                            snackbarHostState.showSnackbar(
                                                it.message
                                                    ?: context.getString(R.string.subscription_restore_failed),
                                            )
                                        }
                                }
                            }
                            .onFailure {
                                snackbarHostState.showSnackbar(
                                    it.message ?: context.getString(R.string.subscription_delete_failed),
                                )
                            }
                    }
                }) {
                    Text(stringResource(R.string.cd_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingFeed = null }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
    // Confirmação antes de marcar tudo como lido (todos os feeds).
    if (confirmMarkAll) {
        AlertDialog(
            onDismissRequest = { confirmMarkAll = false },
            title = { Text(stringResource(R.string.mark_all_read_confirm_title)) },
            text = { Text(stringResource(R.string.mark_all_read_confirm_message)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmMarkAll = false
                    markAllRead()
                }) {
                    Text(stringResource(R.string.mark_all_read))
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmMarkAll = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

// ---------- Drawer compacto ----------

@Composable
private fun DrawerContent(
    email: String?,
    connected: Boolean,
    totalUnread: Int,
    folders: List<TagEntity>,
    feeds: List<FeedEntity>,
    links: List<Tag2SubEntity>,
    filter: FeedFilter,
    onSelect: (FeedFilter) -> Unit,
    onMarkAllRead: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAccount: () -> Unit,
    onOpenSubscriptions: () -> Unit,
    onDeleteFeed: (FeedEntity) -> Unit,
    onConnect: () -> Unit,
) {
    var expandedFolders by remember { mutableStateOf(emptySet<String>()) }

    // Mantém a pasta do filtro ativo sempre expandida.
    LaunchedEffect(filter) {
        (filter as? FeedFilter.Folder)?.let { folder ->
            expandedFolders = expandedFolders + folder.tag.uid
        }
    }

    val linkedFeedIds = links.map { it.feedId }.toSet()
    val standaloneFeeds = feeds.filter { it.id !in linkedFeedIds }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        // Cabeçalho compacto — toque abre a conta (ver informações, sair/entrar)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenAccount)
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                RssGlyph(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = email ?: stringResource(R.string.account_disconnected),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
        }

        CompactDrawerRow(
            label = stringResource(R.string.all_articles),
            icon = Icons.Filled.Inbox,
            badge = totalUnread,
            selected = filter is FeedFilter.All,
            onClick = { onSelect(FeedFilter.All) },
        )

        if (folders.isNotEmpty()) {
            DrawerSectionTitle(stringResource(R.string.drawer_folders))
            folders.forEach { folder ->
                val expanded = folder.uid in expandedFolders
                CompactDrawerRow(
                    label = folder.label,
                    icon = Icons.Filled.Folder,
                    badge = folder.unreadCount,
                    selected = (filter as? FeedFilter.Folder)?.tag?.uid == folder.uid,
                    onClick = {
                        expandedFolders = expandedFolders + folder.uid
                        onSelect(FeedFilter.Folder(folder))
                    },
                    trailing = {
                        IconButton(
                            onClick = {
                                expandedFolders = if (expanded) {
                                    expandedFolders - folder.uid
                                } else {
                                    expandedFolders + folder.uid
                                }
                            },
                            modifier = Modifier.size(28.dp),
                        ) {
                            Icon(
                                imageVector = if (expanded) {
                                    Icons.Filled.ExpandLess
                                } else {
                                    Icons.Filled.ExpandMore
                                },
                                contentDescription = stringResource(
                                    if (expanded) R.string.cd_collapse_folder
                                    else R.string.cd_expand_folder,
                                ),
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    },
                )
                if (expanded) {
                    links
                        .filter { it.tagUid == folder.uid }
                        .mapNotNull { link -> feeds.firstOrNull { it.id == link.feedId } }
                        .forEach { feed ->
                            CompactDrawerRow(
                                label = feed.title,
                                badge = feed.unreadCount,
                                indent = 24.dp,
                                selected = (filter as? FeedFilter.Feed)?.feed?.id == feed.id,
                                onClick = { onSelect(FeedFilter.Feed(feed)) },
                                onLongClick = { onDeleteFeed(feed) },
                                onLongClickLabel = stringResource(R.string.action_remove_feed),
                            )
                        }
                }
            }
        }

        if (standaloneFeeds.isNotEmpty()) {
            DrawerSectionTitle(stringResource(R.string.drawer_feeds))
            standaloneFeeds.forEach { feed ->
                CompactDrawerRow(
                    label = feed.title,
                    icon = Icons.Filled.RssFeed,
                    badge = feed.unreadCount,
                    selected = (filter as? FeedFilter.Feed)?.feed?.id == feed.id,
                    onClick = { onSelect(FeedFilter.Feed(feed)) },
                    onLongClick = { onDeleteFeed(feed) },
                    onLongClickLabel = stringResource(R.string.action_remove_feed),
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        )
        Spacer(Modifier.height(6.dp))

        CompactDrawerRow(
            label = stringResource(R.string.manage_subscriptions),
            icon = Icons.Filled.Edit,
            onClick = onOpenSubscriptions,
        )
        CompactDrawerRow(
            label = stringResource(R.string.mark_all_read),
            icon = Icons.Filled.DoneAll,
            onClick = onMarkAllRead,
        )
        CompactDrawerRow(
            label = stringResource(R.string.settings),
            icon = Icons.Filled.Settings,
            onClick = onOpenSettings,
        )
        if (!connected) {
            CompactDrawerRow(
                label = stringResource(R.string.connect_feedly),
                icon = Icons.Outlined.CloudOff,
                onClick = onConnect,
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun CompactDrawerRow(
    label: String,
    selected: Boolean = false,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    onLongClickLabel: String? = null,
    icon: ImageVector? = null,
    badge: Int = 0,
    indent: Dp = 0.dp,
    trailing: (@Composable () -> Unit)? = null,
) {
    val background = if (selected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        Color.Transparent
    }
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 1.dp)
            .height(48.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick ?: {},
                onLongClickLabel = onLongClickLabel,
            )
            .padding(start = 12.dp + indent, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) contentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(10.dp))
        }
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (badge > 0) {
            CountBadge(badge)
        }
        trailing?.invoke()
    }
}

@Composable
private fun DrawerSectionTitle(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 4.dp),
    )
}

@Composable
private fun CountBadge(count: Int) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = CircleShape,
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 1.dp),
        )
    }
}

// ---------- Estados ----------

@Composable
private fun ConnectBanner(onConnectFeedly: () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.padding(start = 14.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.CloudOff,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.connect_banner),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall,
            )
            TextButton(
                onClick = onConnectFeedly,
                colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                ),
            ) {
                Text(stringResource(R.string.connect_feedly))
            }
        }
    }
}

@Composable
private fun StatusBanner(message: String, isError: Boolean, onRetry: (() -> Unit)?) {
    val container = if (isError) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }
    val onContainer = if (isError) {
        MaterialTheme.colorScheme.onErrorContainer
    } else {
        MaterialTheme.colorScheme.onPrimaryContainer
    }

    Surface(
        shape = MaterialTheme.shapes.large,
        color = container,
        contentColor = onContainer,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        Row(
            modifier = Modifier.padding(start = 14.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if (isError) Icons.Outlined.ErrorOutline else Icons.Filled.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = message,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall,
            )
            if (onRetry != null) {
                TextButton(
                    onClick = onRetry,
                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                        contentColor = onContainer,
                    ),
                ) {
                    Text(stringResource(R.string.sync_retry))
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    connected: Boolean,
    hasFeeds: Boolean,
    hasQuery: Boolean,
    unreadOnly: Boolean,
    onConnectFeedly: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            RssGlyph(
                modifier = Modifier.size(52.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        Spacer(Modifier.height(24.dp))
        val title = when {
            !connected || !hasFeeds -> stringResource(R.string.empty_feeds_title)
            hasQuery -> stringResource(R.string.empty_search_title)
            unreadOnly -> stringResource(R.string.empty_unread_title)
            else -> stringResource(R.string.empty_articles)
        }
        val message = when {
            !connected -> stringResource(R.string.empty_feeds_message)
            !hasFeeds -> stringResource(R.string.empty_feeds_connected_message)
            hasQuery -> stringResource(R.string.empty_search_message)
            unreadOnly -> stringResource(R.string.empty_unread_message)
            else -> stringResource(R.string.empty_articles_message)
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (!connected) {
            Spacer(Modifier.height(24.dp))
            TextButton(onClick = onConnectFeedly) {
                Text(stringResource(R.string.connect_feedly))
            }
        }
    }
}

// ---------- Linha de artigo ----------

@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableArticleRow(
    item: ArticleListItem,
    style: ListStyle,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onSwipe: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value != SwipeToDismissBoxValue.Settled) {
                onSwipe()
            }
            false
        },
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(20.dp),
                )
            }
        },
    ) {
        ArticleRow(
            item = item,
            style = style,
            selected = selected,
            onClick = onClick,
            onLongClick = onLongClick,
        )
    }
}

@Composable
private fun ArticleRow(
    item: ArticleListItem,
    style: ListStyle,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val article = item.article
    val background = when {
        selected -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    val selectLabel = stringResource(R.string.action_select_article)

    if (style.compact) {
        // Modo Lista: nome do feed (menor, colorido) + título completo, sem imagem.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(background)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick,
                    onLongClickLabel = selectLabel,
                )
                .padding(horizontal = 16.dp, vertical = style.verticalPadding),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 5.dp, end = 12.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (article.read) {
                            MaterialTheme.colorScheme.surface
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                    ),
            )
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.feedTitle,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = (style.titleSize - 3).coerceAtLeast(10).sp,
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (article.starred) {
                        Text(
                            text = "★",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = style.metaSize.sp),
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(start = 6.dp),
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = articleTime(article.publishedTime),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = style.metaSize.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = style.titleSize.sp),
                    // Não lidos em negrito; lidos mais claros e discretos.
                    fontWeight = if (article.read) FontWeight.Normal else FontWeight.Bold,
                    color = if (article.read) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    // Sem corte: o título completo é exibido, quebrando em quantas linhas precisar.
                )
            }
            if (style.showThumbnails && !article.image.isNullOrBlank()) {
                Spacer(Modifier.width(12.dp))
                AsyncImage(
                    model = article.image,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(style.thumbnailSize)
                        .clip(RoundedCornerShape(14.dp)),
                )
            }
        }
        return
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                onLongClickLabel = selectLabel,
            )
            .padding(horizontal = 16.dp, vertical = style.verticalPadding),
    ) {
        Box(
            modifier = Modifier
                .padding(top = style.dotTopPadding, end = 12.dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(
                    if (article.read) {
                        MaterialTheme.colorScheme.surface
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                ),
        )

        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.feedTitle,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = (style.titleSize - 3).coerceAtLeast(10).sp,
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (article.starred) {
                    Text(
                        text = "★",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = style.metaSize.sp),
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(start = 6.dp),
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = articleTime(article.publishedTime),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = style.metaSize.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = article.title,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = style.titleSize.sp),
                // Não lidos em negrito; lidos mais claros e discretos.
                fontWeight = if (article.read) FontWeight.Normal else FontWeight.Bold,
                color = if (article.read) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        if (!article.image.isNullOrBlank()) {
            Spacer(Modifier.width(12.dp))
            AsyncImage(
                model = article.image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(style.thumbnailSize)
                    .clip(RoundedCornerShape(14.dp)),
            )
        }
    }
}

/** Estilo da lista derivado das preferências de densidade, fonte e modo. */
private data class ListStyle(
    val verticalPadding: Dp,
    val thumbnailSize: Dp,
    val titleSize: Int,
    val metaSize: Int,
    val gap: Dp,
    val dotTopPadding: Dp,
    val compact: Boolean,
    val showThumbnails: Boolean = false,
)

private fun listStyleFor(
    density: Int,
    fontSp: Int,
    compact: Boolean,
    thumbnails: Boolean,
): ListStyle = when (density) {
    0 -> ListStyle(
        verticalPadding = if (compact) 6.dp else 8.dp,
        thumbnailSize = 48.dp,
        titleSize = fontSp,
        metaSize = (fontSp - 4).coerceAtLeast(11),
        gap = 2.dp,
        dotTopPadding = 6.dp,
        compact = compact,
    )

    2 -> ListStyle(
        verticalPadding = if (compact) 14.dp else 20.dp,
        thumbnailSize = 80.dp,
        titleSize = fontSp,
        metaSize = (fontSp - 2).coerceAtLeast(12),
        gap = 6.dp,
        dotTopPadding = 9.dp,
        compact = compact,
    )

    else -> ListStyle(
        verticalPadding = if (compact) 10.dp else 14.dp,
        thumbnailSize = 64.dp,
        titleSize = fontSp,
        metaSize = (fontSp - 3).coerceAtLeast(11),
        gap = 4.dp,
        dotTopPadding = 7.dp,
        compact = compact,
    )
}.copy(showThumbnails = thumbnails)

private fun Set<Long>.toggle(id: Long): Set<Long> =
    if (contains(id)) this - id else this + id

/** Seletor do tópico atual + preferências de exibição (modo e ordenação). */
@Composable
private fun TopicSelector(
    title: String,
    viewMode: Int,
    oldestFirst: Boolean,
    onViewMode: (Int) -> Unit,
    onSortOldest: (Boolean) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = stringResource(R.string.cd_topic_selector),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ModeOption(
                    icon = Icons.AutoMirrored.Filled.ViewList,
                    label = stringResource(R.string.view_list),
                    contentDescription = stringResource(R.string.cd_view_list),
                    selected = viewMode == 0,
                    onClick = {
                        onViewMode(0)
                        expanded = false
                    },
                )
                ModeOption(
                    icon = Icons.Filled.GridView,
                    label = stringResource(R.string.view_grid),
                    contentDescription = stringResource(R.string.cd_view_grid),
                    selected = viewMode == 1,
                    onClick = {
                        onViewMode(1)
                        expanded = false
                    },
                )
            }
            HorizontalDivider(Modifier.padding(horizontal = 8.dp))
            DropdownMenuItem(
                text = { Text(stringResource(R.string.sort_recent)) },
                trailingIcon = {
                    if (!oldestFirst) {
                        Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                    }
                },
                onClick = {
                    onSortOldest(false)
                    expanded = false
                },
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.sort_oldest)) },
                trailingIcon = {
                    if (oldestFirst) {
                        Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                    }
                },
                onClick = {
                    onSortOldest(true)
                    expanded = false
                },
            )
        }
    }
}

@Composable
private fun ModeOption(
    icon: ImageVector,
    label: String,
    contentDescription: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (selected) {
                    MaterialTheme.colorScheme.secondaryContainer
                } else {
                    Color.Transparent
                },
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 8.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = contentColor,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
        )
    }
}

/**
 * Horário do artigo, à direita do nome do feed:
 * hoje → HH:mm · ontem → "Ontem" · este ano → dd/MM · mais antigo → dd/MM/yy
 */
private fun articleTime(epochSeconds: Long): String {
    if (epochSeconds <= 0) return ""
    return runCatching {
        val zone = ZoneId.systemDefault()
        val dateTime = Instant.ofEpochSecond(epochSeconds).atZone(zone)
        val now = ZonedDateTime.now(zone)
        when {
            dateTime.toLocalDate() == now.toLocalDate() ->
                dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))

            dateTime.toLocalDate() == now.toLocalDate().minusDays(1) -> "Ontem"
            dateTime.year == now.year ->
                dateTime.format(DateTimeFormatter.ofPattern("dd/MM"))

            else -> dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yy"))
        }
    }.getOrDefault("")
}
