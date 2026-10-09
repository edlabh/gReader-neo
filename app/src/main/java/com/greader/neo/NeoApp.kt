package com.greader.neo

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.greader.neo.data.ArticleActions
import com.greader.neo.data.SubscriptionActions
import com.greader.neo.data.auth.AuthManager
import com.greader.neo.data.auth.SecureTokenStore
import com.greader.neo.data.db.NeoDatabase
import com.greader.neo.data.feedly.FeedlyApi
import com.greader.neo.data.feedly.createFeedlyApi
import com.greader.neo.data.notify.NewArticlesNotifier
import com.greader.neo.data.opml.OpmlManager
import com.greader.neo.data.prefs.SettingsRepository
import com.greader.neo.data.reader.ArticleExtractor
import com.greader.neo.data.sync.FeedlySyncEngine
import com.greader.neo.data.sync.SyncOutcome
import com.greader.neo.data.sync.SyncScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NeoApp : Application() {

    /** Escopo de longa duração para trabalho em background (sync, agendamento). */
    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    lateinit var database: NeoDatabase
        private set

    lateinit var settings: SettingsRepository
        private set

    lateinit var feedlyApi: FeedlyApi
        private set

    lateinit var tokenStore: SecureTokenStore
        private set

    lateinit var authManager: AuthManager
        private set

    lateinit var syncEngine: FeedlySyncEngine
        private set

    lateinit var articleActions: ArticleActions
        private set

    lateinit var subscriptionActions: SubscriptionActions
        private set

    lateinit var articleExtractor: ArticleExtractor
        private set

    lateinit var opmlManager: OpmlManager
        private set

    lateinit var notifier: NewArticlesNotifier
        private set

    /** Texto compartilhado de outro app (share-to-subscribe). */
    val pendingShare = MutableStateFlow<String?>(null)

    /** Estado do sync exposto para a UI. */
    val syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)

    /** Executa o sync e atualiza o status (usado pelo worker e pelo app). */
    suspend fun runSync(): SyncOutcome {
        syncStatus.value = SyncStatus.Running
        val outcome = syncEngine.sync()
        syncStatus.value = when (outcome) {
            is SyncOutcome.Success -> SyncStatus.Success(outcome.newArticles)
            is SyncOutcome.NotLoggedIn -> SyncStatus.Error(getString(R.string.login_required))
            is SyncOutcome.AuthError -> SyncStatus.Error(outcome.message)
            is SyncOutcome.Error -> SyncStatus.Error(outcome.message)
        }
        // O aviso de sucesso some sozinho após alguns segundos.
        if (outcome is SyncOutcome.Success) {
            appScope.launch {
                delay(4_000)
                if (syncStatus.value is SyncStatus.Success) {
                    syncStatus.value = SyncStatus.Idle
                }
            }
        }
        return outcome
    }

    /** Sync manual (FAB / sync ao abrir), com feedback na tela. */
    fun syncNow() {
        if (syncStatus.value is SyncStatus.Running) return
        appScope.launch { runSync() }
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, NeoDatabase::class.java, "reader.db").build()
        settings = SettingsRepository(this)
        feedlyApi = createFeedlyApi()
        tokenStore = SecureTokenStore(this)
        authManager = AuthManager(feedlyApi, tokenStore, appScope, this)
        syncEngine = FeedlySyncEngine(database, feedlyApi, authManager, settings, this)
        articleActions = ArticleActions(database)
        subscriptionActions = SubscriptionActions(database, feedlyApi, authManager, this)
        articleExtractor = ArticleExtractor()
        opmlManager = OpmlManager(this, feedlyApi, authManager)
        notifier = NewArticlesNotifier(this)
        notifier.ensureChannel()

        // Mantém o agendamento periódico alinhado com a preferência do usuário.
        appScope.launch {
            settings.settings.collect { prefs ->
                SyncScheduler.schedulePeriodic(this@NeoApp, prefs.syncIntervalMinutes)
            }
        }
    }
}

/** Atalho para o container do app (DI manual). */
val Context.app: NeoApp
    get() = applicationContext as NeoApp

/** Estado de sincronização exibido na UI. */
sealed interface SyncStatus {
    data object Idle : SyncStatus
    data object Running : SyncStatus
    data class Success(val newArticles: Int) : SyncStatus
    data class Error(val message: String) : SyncStatus
}
