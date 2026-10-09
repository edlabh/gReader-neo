package com.greader.neo.ui.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greader.neo.R
import com.greader.neo.app
import com.greader.neo.data.auth.AuthState
import com.greader.neo.ui.components.RssGlyph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val auth = context.app.authManager
    val state by auth.state.collectAsStateWithLifecycle()

    var showAdvanced by remember { mutableStateOf(false) }
    var clientId by remember { mutableStateOf("") }
    var clientSecret by remember { mutableStateOf("") }
    var credentialsMessage by remember { mutableStateOf<String?>(null) }
    var urlKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(showAdvanced) {
        if (showAdvanced) {
            clientId = auth.customClientId.orEmpty()
            clientSecret = auth.customClientSecret.orEmpty()
        }
    }

    val authorizeUrl = remember(state, urlKey) { auth.buildAuthorizeUrl() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (state is AuthState.Connected) R.string.account_title
                            else R.string.connect_feedly,
                        ),
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
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(24.dp))

            // Hero da marca
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                RssGlyph(
                    modifier = Modifier.size(56.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.login_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(28.dp))

            when (val current = state) {
                is AuthState.Connected -> {
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(R.string.login_connected_as, current.email ?: "Feedly"),
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(Modifier.height(16.dp))
                            TextButton(onClick = { auth.logout() }) {
                                Text(stringResource(R.string.logout))
                            }
                        }
                    }
                }

                AuthState.Connecting -> {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(16.dp))
                    Text(stringResource(R.string.login_connecting))
                }

                is AuthState.Error -> {
                    Text(
                        text = current.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(20.dp))
                    ConnectButton(context, authorizeUrl)
                }

                AuthState.Disconnected -> {
                    ConnectButton(context, authorizeUrl)
                }
            }

            Spacer(Modifier.height(32.dp))

            // Credenciais próprias (avançado)
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.login_advanced),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(onClick = { showAdvanced = !showAdvanced }) {
                            Icon(
                                imageVector = if (showAdvanced) {
                                    Icons.Filled.ExpandLess
                                } else {
                                    Icons.Filled.ExpandMore
                                },
                                contentDescription = null,
                            )
                        }
                    }
                    Text(
                        text = stringResource(
                            if (auth.hasCustomCredentials) R.string.login_using_custom
                            else R.string.login_using_default,
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    if (showAdvanced) {
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = clientId,
                            onValueChange = { clientId = it },
                            label = { Text(stringResource(R.string.login_client_id)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = clientSecret,
                            onValueChange = { clientSecret = it },
                            label = { Text(stringResource(R.string.login_client_secret)) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    auth.saveCredentials(clientId, clientSecret)
                                    credentialsMessage = context.getString(R.string.login_credentials_saved)
                                    urlKey++
                                },
                                enabled = clientId.isNotBlank() && clientSecret.isNotBlank(),
                            ) {
                                Text(stringResource(R.string.login_save_credentials))
                            }
                            TextButton(onClick = {
                                auth.useDefaultCredentials()
                                clientId = ""
                                clientSecret = ""
                                credentialsMessage = context.getString(R.string.login_credentials_reset)
                                urlKey++
                            }) {
                                Text(stringResource(R.string.login_reset_credentials))
                            }
                        }
                        credentialsMessage?.let {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun ConnectButton(context: Context, url: String?) {
    var launchFailed by remember { mutableStateOf(false) }

    Button(
        onClick = {
            val target = url ?: return@Button
            val opened = runCatching {
                CustomTabsIntent.Builder().build().launchUrl(context, Uri.parse(target))
                true
            }.getOrElse {
                runCatching {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(target)))
                    true
                }.getOrElse { false }
            }
            launchFailed = !opened
        },
        enabled = url != null,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
    ) {
        Text(
            text = stringResource(R.string.connect_feedly),
            style = MaterialTheme.typography.titleSmall,
        )
    }
    if (launchFailed) {
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.login_browser_unavailable),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
        )
    }
    if (url == null) {
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.login_not_configured),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
        )
    }
}
