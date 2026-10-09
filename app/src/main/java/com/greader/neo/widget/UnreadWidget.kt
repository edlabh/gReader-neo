package com.greader.neo.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.greader.neo.MainActivity
import com.greader.neo.app
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Widget de não lidos: lista os feeds com mais artigos não lidos.
 * Toque abre o app; atualiza a cada 30 min e após cada sync.
 */
class UnreadWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val feeds = withContext(Dispatchers.IO) {
            runCatching {
                context.app.database.feedDao().all()
                    .filter { !it.hidden }
                    .sortedByDescending { it.unreadCount }
                    .take(6)
            }.getOrDefault(emptyList())
        }
        provideContent {
            GlanceTheme(colors = WidgetColors) {
                WidgetContent(feeds.map { it.title to it.unreadCount })
            }
        }
    }
}

@Composable
private fun WidgetContent(feeds: List<Pair<String, Int>>) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(16.dp)
            .background(GlanceTheme.colors.widgetBackground)
            .padding(12.dp)
            .clickable(actionStartActivity<MainActivity>()),
    ) {
        Text(
            text = "gReader Neo",
            style = TextStyle(
                color = GlanceTheme.colors.primary,
                fontWeight = FontWeight.Bold,
            ),
        )
        Spacer(modifier = GlanceModifier.padding(top = 4.dp))
        if (feeds.isEmpty()) {
            Text(
                text = "Nenhum feed — abra o app para sincronizar",
                style = TextStyle(color = GlanceTheme.colors.onSurfaceVariant),
            )
        } else {
            feeds.forEach { (title, unread) ->
                Row(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                ) {
                    Text(
                        text = title,
                        maxLines = 1,
                        style = TextStyle(color = GlanceTheme.colors.onSurface),
                        modifier = GlanceModifier.defaultWeight(),
                    )
                    Spacer(modifier = GlanceModifier.width(8.dp))
                    Text(
                        text = unread.toString(),
                        style = TextStyle(
                            color = if (unread > 0) {
                                GlanceTheme.colors.primary
                            } else {
                                GlanceTheme.colors.onSurfaceVariant
                            },
                            fontWeight = FontWeight.Medium,
                        ),
                    )
                }
            }
        }
    }
}

class UnreadWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = UnreadWidget()
}
