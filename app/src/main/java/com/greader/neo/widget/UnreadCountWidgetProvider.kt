package com.greader.neo.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.greader.neo.MainActivity
import com.greader.neo.R
import com.greader.neo.app
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Widget pequeno estilo "ícone com badge" do gReader original:
 * ícone do app + contador de não lidos no canto inferior direito.
 *
 * Implementado com RemoteViews clássico (AppWidgetProvider) para máxima
 * compatibilidade com qualquer launcher — o Glance não renderizava de forma
 * confiável em todos os dispositivos.
 */
class UnreadCountWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val count = currentCount(context)
                appWidgetManager.updateAppWidget(appWidgetIds, buildViews(context, count))
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {

        private suspend fun currentCount(context: Context): Int =
            runCatching { context.app.database.feedDao().totalUnread() }.getOrDefault(0)

        private fun buildViews(context: Context, count: Int): RemoteViews =
            RemoteViews(context.packageName, R.layout.widget_unread_count).apply {
                setImageViewResource(R.id.widget_icon, R.mipmap.ic_launcher)
                if (count > 0) {
                    setViewVisibility(R.id.widget_badge, View.VISIBLE)
                    setTextViewText(
                        R.id.widget_badge,
                        if (count > 999) "999+" else count.toString(),
                    )
                } else {
                    setViewVisibility(R.id.widget_badge, View.GONE)
                }
                val intent = Intent(context, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
                )
                setOnClickPendingIntent(R.id.widget_root, pendingIntent)
            }

        /** Atualiza todas as instâncias (chamado após sync e nas leituras locais). */
        suspend fun refresh(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                ComponentName(context, UnreadCountWidgetProvider::class.java),
            )
            if (ids.isEmpty()) return
            manager.updateAppWidget(ids, buildViews(context, currentCount(context)))
        }
    }
}
