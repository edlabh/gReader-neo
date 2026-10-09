package com.greader.neo.data.sync

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.greader.neo.app
import java.util.concurrent.TimeUnit

/** Executa o motor de sync do Feedly em background. */
class SyncWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val outcome = applicationContext.app.runSync()

        val result = when (outcome) {
            is SyncOutcome.Success -> {
                if (outcome.newArticles > 0) {
                    applicationContext.app.notifier.notifyNewArticles(outcome.newArticles)
                }
                runCatching {
                    com.greader.neo.widget.UnreadWidget().updateAll(applicationContext)
                    com.greader.neo.widget.UnreadCountWidgetProvider.refresh(applicationContext)
                }
                Result.success()
            }

            is SyncOutcome.NotLoggedIn -> Result.success()
            is SyncOutcome.AuthError -> Result.failure()
            is SyncOutcome.Error -> if (outcome.retryable) Result.retry() else Result.failure()
        }

        // Encadeia o próximo sync de background (se houver intervalo configurado).
        if (result !is Result.Retry) {
            runCatching {
                val minutes = applicationContext.app.settings.current().syncIntervalMinutes
                SyncScheduler.schedulePeriodic(applicationContext, minutes)
            }
        }
        return result
    }
}

object SyncScheduler {

    private const val CHAIN_WORK = "feedly_sync_chain"
    private const val ONE_TIME_WORK = "feedly_sync_now"
    private const val LEGACY_PERIODIC = "feedly_periodic_sync"

    /** Sync manual imediato (pull-to-refresh, botão da barra). */
    fun enqueueNow(context: Context) {
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
            .build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork(ONE_TIME_WORK, ExistingWorkPolicy.KEEP, request)
    }

    /**
     * (Re)agenda o próximo sync em background.
     *
     * Usa uma cadeia de OneTimeWorkRequest (o periódico do WorkManager tem mínimo
     * de 15 min, o que impede 5m). `intervalMinutes <= 0` = somente manual:
     * nenhum trabalho é agendado.
     */
    fun schedulePeriodic(context: Context, intervalMinutes: Int) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(LEGACY_PERIODIC)
        workManager.cancelUniqueWork(CHAIN_WORK)
        if (intervalMinutes <= 0) return

        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
            .setInitialDelay(intervalMinutes.toLong(), TimeUnit.MINUTES)
            .build()
        workManager.enqueueUniqueWork(CHAIN_WORK, ExistingWorkPolicy.REPLACE, request)
    }

    fun cancel(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(CHAIN_WORK)
        workManager.cancelUniqueWork(ONE_TIME_WORK)
    }
}
