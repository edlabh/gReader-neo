package com.greader.neo.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greader.neo.BuildConfig
import com.greader.neo.R
import com.greader.neo.app
import com.greader.neo.data.auth.AuthState
import com.greader.neo.data.prefs.AppSettings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenSubscribe: () -> Unit,
    onOpenLogin: () -> Unit,
    onOpenSubscriptions: () -> Unit,
) {
    val context = LocalContext.current
    val settingsRepository = context.app.settings
    val current by settingsRepository.settings
        .collectAsStateWithLifecycle(initialValue = AppSettings())
    val scope = rememberCoroutineScope()
    val auth = context.app.authManager
    val authState by auth.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            SectionTitle(stringResource(R.string.sync_section))
            SettingsCard {
                // Assinar feed
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenSubscribe)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.settings_subscribe),
                        modifier = Modifier.weight(1f),
                    )
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                }
                CardDivider()
                // Gerenciar assinaturas
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenSubscriptions)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.manage_subscriptions),
                        modifier = Modifier.weight(1f),
                    )
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                }
                CardDivider()
                SwitchRow(
                    title = stringResource(R.string.sync_on_start),
                    checked = current.syncOnStart,
                    onChange = { value -> scope.launch { settingsRepository.setSyncOnStart(value) } },
                )
                CardDivider()
                SwitchRow(
                    title = stringResource(R.string.sync_server_reads),
                    checked = current.syncServerReads,
                    onChange = { value -> scope.launch { settingsRepository.setSyncServerReads(value) } },
                )
            }

            SectionTitle(stringResource(R.string.sync_interval))
            SettingsCard {
                // Seletor único: Atualização Manual + frequências de 5 min até 24 h.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.sync_frequency),
                        modifier = Modifier.weight(1f),
                    )
                    Box {
                        var menuOpen by remember { mutableStateOf(false) }
                        OutlinedButton(onClick = { menuOpen = true }) {
                            Text(
                                if (current.syncIntervalMinutes <= 0) {
                                    stringResource(R.string.sync_update_manual)
                                } else {
                                    friendlyInterval(current.syncIntervalMinutes)
                                },
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                        DropdownMenu(
                            expanded = menuOpen,
                            onDismissRequest = { menuOpen = false },
                        ) {
                            intervalOptions.forEach { minutes ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            if (minutes <= 0) {
                                                stringResource(R.string.sync_update_manual)
                                            } else {
                                                friendlyInterval(minutes)
                                            },
                                        )
                                    },
                                    trailingIcon = {
                                        if (current.syncIntervalMinutes == minutes) {
                                            Icon(
                                                imageVector = Icons.Filled.Check,
                                                contentDescription = null,
                                            )
                                        }
                                    },
                                    onClick = {
                                        scope.launch {
                                            settingsRepository.setSyncIntervalMinutes(minutes)
                                        }
                                        menuOpen = false
                                    },
                                )
                            }
                        }
                    }
                }
                if (current.syncIntervalMinutes <= 0) {
                    Text(
                        text = stringResource(R.string.sync_interval_manual_summary),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 10.dp),
                    )
                }
            }

            SectionTitle(stringResource(R.string.appearance_section))
            SettingsCard {
                SwitchRow(
                    title = stringResource(R.string.theme_amoled),
                    checked = current.amoled,
                    onChange = { value -> scope.launch { settingsRepository.setAmoled(value) } },
                )
                CardDivider()
                SwitchRow(
                    title = stringResource(R.string.theme_dynamic),
                    checked = current.dynamicColor,
                    onChange = { value -> scope.launch { settingsRepository.setDynamicColor(value) } },
                )
            }

            SectionTitle(stringResource(R.string.list_section))
            SettingsCard {
                Text(
                    text = stringResource(R.string.list_density),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp),
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    listOf(
                        0 to R.string.density_compact,
                        1 to R.string.density_comfortable,
                        2 to R.string.density_spacious,
                    ).forEach { (value, label) ->
                        FilterChip(
                            selected = current.listDensity == value,
                            onClick = {
                                scope.launch { settingsRepository.setListDensity(value) }
                            },
                            label = { Text(stringResource(label)) },
                        )
                    }
                }
                CardDivider()
                Text(
                    text = stringResource(R.string.list_font_size),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp),
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    listOf(
                        14 to R.string.font_small,
                        16 to R.string.font_medium,
                        18 to R.string.font_large,
                    ).forEach { (value, label) ->
                        FilterChip(
                            selected = current.listFontSizeSp == value,
                            onClick = {
                                scope.launch { settingsRepository.setListFontSize(value) }
                            },
                            label = { Text(stringResource(label)) },
                        )
                    }
                }
                CardDivider()
                SwitchRow(
                    title = stringResource(R.string.list_divider),
                    checked = current.listDivider,
                    onChange = { value -> scope.launch { settingsRepository.setListDivider(value) } },
                )
                CardDivider()
                SwitchRow(
                    title = stringResource(R.string.list_thumbnails),
                    checked = current.listThumbnails,
                    onChange = { value -> scope.launch { settingsRepository.setListThumbnails(value) } },
                )
            }

            SectionTitle(stringResource(R.string.retention_section))
            SettingsCard {
                Text(
                    text = stringResource(R.string.retention_title),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp),
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    listOf(
                        1 to R.string.retention_1d,
                        3 to R.string.retention_3d,
                        7 to R.string.retention_7d,
                        14 to R.string.retention_14d,
                        30 to R.string.retention_30d,
                        -1 to R.string.retention_never,
                    ).forEach { (value, label) ->
                        FilterChip(
                            selected = current.retentionDays == value,
                            onClick = {
                                scope.launch { settingsRepository.setRetentionDays(value) }
                            },
                            label = { Text(stringResource(label)) },
                        )
                    }
                }
                CardDivider()
                SwitchRow(
                    title = stringResource(R.string.prefetch_images),
                    checked = current.prefetchImages,
                    onChange = { value -> scope.launch { settingsRepository.setPrefetchImages(value) } },
                )
            }

            SectionTitle(stringResource(R.string.reader_section))
            SettingsCard {
                SwitchRow(
                    title = stringResource(R.string.webview_embedded),
                    checked = current.webViewEmbedded,
                    onChange = { value -> scope.launch { settingsRepository.setWebViewEmbedded(value) } },
                )
                Text(
                    text = stringResource(R.string.webview_embedded_summary),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 10.dp),
                )
            }

            SectionTitle(stringResource(R.string.account_section))
            SettingsCard {
                (authState as? AuthState.Connected)?.let { connected ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = connected.email ?: "Feedly",
                            modifier = Modifier.weight(1f),
                        )
                        TextButton(onClick = { auth.logout() }) {
                            Text(stringResource(R.string.logout))
                        }
                    }
                } ?: Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.account_disconnected),
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = onOpenLogin) {
                        Text(stringResource(R.string.connect_feedly))
                    }
                }
            }

            SectionTitle(stringResource(R.string.about_section))
            SettingsCard {
                Text(
                    text = stringResource(R.string.version_format, BuildConfig.VERSION_NAME),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 28.dp, top = 20.dp, bottom = 6.dp),
    )
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Column(content = content)
    }
}

@Composable
private fun CardDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
    )
}

@Composable
private fun SwitchRow(title: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

/** Opções do seletor de frequência: manual (-1) + de 5 min até 24 h. */
private val intervalOptions = listOf(-1, 5, 10, 15, 30, 45, 60, 90, 120, 180, 240, 360, 480, 720, 1440)

/** Formata minutos de forma amigável (ex.: 45 min, 1 h, 1 h 30 min). */
private fun friendlyInterval(minutes: Int): String = when {
    minutes <= 0 -> "Manual"
    minutes < 60 -> "$minutes min"
    minutes % 60 == 0 -> "${minutes / 60} h"
    else -> "${minutes / 60} h ${minutes % 60} min"
}
