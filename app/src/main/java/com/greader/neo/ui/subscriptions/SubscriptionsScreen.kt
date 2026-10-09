package com.greader.neo.ui.subscriptions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greader.neo.R
import com.greader.neo.app
import com.greader.neo.data.auth.AuthState
import com.greader.neo.data.db.FeedEntity
import com.greader.neo.data.db.TagEntity
import kotlinx.coroutines.launch

/**
 * Gerenciamento de assinaturas: renomear, mover de pasta e remover feeds,
 * sincronizando as mudanças com a conta Feedly.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val app = context.app
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val feeds by app.database.feedDao().observeAll()
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val tags by app.database.tagDao().observeAll()
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val links by app.database.tag2SubDao().observeAll()
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val authState by app.authManager.state.collectAsStateWithLifecycle()
    val connected = authState is AuthState.Connected

    var editing by remember { mutableStateOf<FeedEntity?>(null) }
    var deleting by remember { mutableStateOf<FeedEntity?>(null) }

    val folders = tags.filter { it.type == 11 }
    val linkedFeedIds = links.map { it.feedId }.toSet()

    fun folderOf(feed: FeedEntity): TagEntity? {
        val link = links.firstOrNull { it.feedId == feed.id } ?: return null
        return folders.firstOrNull { it.uid == link.tagUid }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.manage_subscriptions)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        when {
            !connected -> CenteredBox(Modifier.padding(padding)) {
                Text(
                    text = stringResource(R.string.subscriptions_requires_login),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            feeds.isEmpty() -> CenteredBox(Modifier.padding(padding)) {
                Text(
                    text = stringResource(R.string.empty_feeds_title),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                folders.forEach { folder ->
                    val folderFeeds = links
                        .filter { it.tagUid == folder.uid }
                        .mapNotNull { link -> feeds.firstOrNull { it.id == link.feedId } }
                    if (folderFeeds.isNotEmpty()) {
                        item(key = "header-${folder.uid}") {
                            SectionHeader(folder.label)
                        }
                        items(folderFeeds, key = { it.id }) { feed ->
                            SubscriptionRow(
                                feed = feed,
                                folderLabel = folder.label,
                                onEdit = { editing = feed },
                                onDelete = { deleting = feed },
                            )
                        }
                    }
                }
                val standalone = feeds.filter { it.id !in linkedFeedIds }
                if (standalone.isNotEmpty()) {
                    item(key = "header-none") {
                        SectionHeader(stringResource(R.string.folder_none))
                    }
                    items(standalone, key = { it.id }) { feed ->
                        SubscriptionRow(
                            feed = feed,
                            folderLabel = null,
                            onEdit = { editing = feed },
                            onDelete = { deleting = feed },
                        )
                    }
                }
                item(key = "spacer") { Spacer(Modifier.height(24.dp)) }
            }
        }
    }

    // Diálogo de edição (renomear + mover de pasta)
    editing?.let { feed ->
        EditSubscriptionDialog(
            feed = feed,
            folders = folders,
            currentFolder = folderOf(feed),
            onDismiss = { editing = null },
            onSave = { newTitle, folder ->
                editing = null
                scope.launch {
                    app.subscriptionActions.update(feed, newTitle, folder)
                        .onSuccess {
                            snackbarHostState.showSnackbar(
                                context.getString(R.string.subscription_updated),
                            )
                        }
                        .onFailure {
                            snackbarHostState.showSnackbar(
                                it.message ?: context.getString(R.string.subscription_update_failed),
                            )
                        }
                }
            },
        )
    }

    // Confirmação de remoção
    deleting?.let { feed ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text(stringResource(R.string.delete_subscription_title)) },
            text = { Text(stringResource(R.string.delete_subscription_message, feed.title)) },
            confirmButton = {
                TextButton(onClick = {
                    deleting = null
                    scope.launch {
                        app.subscriptionActions.delete(feed)
                            .onSuccess {
                                snackbarHostState.showSnackbar(
                                    context.getString(R.string.subscription_deleted),
                                )
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
                TextButton(onClick = { deleting = null }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun CenteredBox(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) { content() }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 24.dp, top = 20.dp, bottom = 6.dp),
    )
}

@Composable
private fun SubscriptionRow(
    feed: FeedEntity,
    folderLabel: String?,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = feed.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            folderLabel?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        IconButton(onClick = onEdit) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = stringResource(R.string.cd_edit),
                modifier = Modifier.width(20.dp),
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = stringResource(R.string.cd_delete),
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.width(20.dp),
            )
        }
    }
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 24.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
    )
}

@Composable
private fun EditSubscriptionDialog(
    feed: FeedEntity,
    folders: List<TagEntity>,
    currentFolder: TagEntity?,
    onDismiss: () -> Unit,
    onSave: (String, TagEntity?) -> Unit,
) {
    var title by remember { mutableStateOf(feed.title) }
    var selectedFolderUid by remember { mutableStateOf(currentFolder?.uid) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_subscription)) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.subscription_title_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.subscription_folder_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                ) {
                    FilterChip(
                        selected = selectedFolderUid == null,
                        onClick = { selectedFolderUid = null },
                        label = { Text(stringResource(R.string.folder_none)) },
                    )
                    folders.forEach { folder ->
                        FilterChip(
                            selected = selectedFolderUid == folder.uid,
                            onClick = { selectedFolderUid = folder.uid },
                            label = { Text(folder.label) },
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        title.trim().ifBlank { feed.title },
                        folders.firstOrNull { it.uid == selectedFolderUid },
                    )
                },
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}
