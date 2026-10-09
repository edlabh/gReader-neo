package com.greader.neo

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
import coil3.SingletonImageLoader
import coil3.request.ImageRequest
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
            // Pré-cache de imagens para leitura offline (respeita Wi-Fi + preferência).
            prefetchRecentImages()
        }
        return outcome
    }

    /** Sync manual (FAB / sync ao abrir), com feedback na tela. */
    fun syncNow() {
        if (syncStatus.value is SyncStatus.Running) return
        appScope.launch { runSync() }
    }

    /**
     * Pré-baixa imagens dos artigos recentes para leitura offline.
     * Só roda quando a preferência está ligada e a rede ativa é Wi-Fi.
     */
    fun prefetchRecentImages() {
        appScope.launch {
            if (!settings.current().prefetchImages || !isOnWifi()) return@launch
            val urls = runCatching { database.articleDao().recentImageUrls(60) }
                .getOrDefault(emptyList())
            if (urls.isEmpty()) return@launch
            val loader = SingletonImageLoader.get(this@NeoApp)
            urls.forEach { url ->
                loader.enqueue(
                    ImageRequest.Builder(this@NeoApp)
                        .data(url)
                        .size(512)
                        .build(),
                )
            }
        }
    }

    private fun isOnWifi(): Boolean {
        val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, NeoDatabase::class.java, "reader.db")
            .addMigrations(MIGRATION_1_2)
            .build()
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

/** v1 → v2: cache do conteúdo extraído do modo Leitura (leitura offline). */
private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE articles ADD COLUMN extractedContent TEXT")
        db.execSQL("ALTER TABLE articles ADD COLUMN extractedTitle TEXT")
    }
}
