package com.greader.neo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greader.neo.data.prefs.AppSettings
import com.greader.neo.ui.navigation.NeoNavHost
import com.greader.neo.ui.theme.GReaderNeoTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleAuthRedirect(intent?.data)
        handleShareIntent(intent)
        setContent {
            val settings by app.settings.settings
                .collectAsStateWithLifecycle(initialValue = AppSettings())
            GReaderNeoTheme(
                amoled = settings.amoled,
                dynamicColor = settings.dynamicColor,
            ) {
                NeoNavHost()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleAuthRedirect(intent.data)
        handleShareIntent(intent)
    }

    /** Recebe o redirect do OAuth2 do Feedly (greader://feedly?code=…&state=…). */
    private fun handleAuthRedirect(uri: Uri?) {
        if (uri != null) {
            app.authManager.handleRedirect(uri)
        }
    }

    /** Recebe compartilhamentos de texto (share-to-subscribe). */
    private fun handleShareIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type?.startsWith("text/") == true) {
            val text = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (!text.isNullOrBlank()) {
                app.pendingShare.value = text
            }
        }
    }
}
