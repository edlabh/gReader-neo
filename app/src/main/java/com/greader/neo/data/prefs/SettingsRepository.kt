package com.greader.neo.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/** Preferências do app (chaves espelham o app original para migração 1:1 — Relatório 03). */
data class AppSettings(
    val syncOnStart: Boolean = true,
    val syncServerReads: Boolean = true,
    val syncIncludeStarred: Boolean = false,
    /** Intervalo de sync em minutos; -1 = somente manual (sem background). Padrão: 60 (1h). */
    val syncIntervalMinutes: Int = 60,
    val syncItemLimit: Int = 1000,
    val amoled: Boolean = false,
    val dynamicColor: Boolean = false,
    val listDensity: Int = 1,
    val listFontSizeSp: Int = 16,
    val listDivider: Boolean = true,
    val listViewMode: Int = 1,
    val listSortOldest: Boolean = false,
    val listUnreadOnly: Boolean = false,
    val retentionDays: Int = 7,
    val webViewEmbedded: Boolean = true,
    val lastSyncTime: Long = 0,
)

class SettingsRepository(private val context: Context) {

    private object Keys {
        val SYNC_ON_START = booleanPreferencesKey("sync_on_start")
        val SYNC_SERVER_READS = booleanPreferencesKey("sync_server_reads")
        val SYNC_INCLUDE_STARRED = booleanPreferencesKey("sync_include_starred")
        val SYNC_INTERVAL_MINUTES = intPreferencesKey("sync_interval_minutes")
        val SYNC_ITEM_LIMIT = intPreferencesKey("sync_item_limit")
        val AMOLED = booleanPreferencesKey("theme_amoled")
        val DYNAMIC_COLOR = booleanPreferencesKey("theme_dynamic_color")
        val LIST_DENSITY = intPreferencesKey("list_density")
        val LIST_FONT_SIZE = intPreferencesKey("list_font_size")
        val LIST_DIVIDER = booleanPreferencesKey("list_divider")
        val LIST_VIEW_MODE = intPreferencesKey("list_view_mode")
        val LIST_SORT_OLDEST = booleanPreferencesKey("list_sort_oldest")
        val LIST_UNREAD_ONLY = booleanPreferencesKey("list_unread_only")
        val RETENTION_DAYS = intPreferencesKey("retention_days")
        val WEB_VIEW_EMBEDDED = booleanPreferencesKey("web_view_embedded")
        val LAST_SYNC_TIME = longPreferencesKey("last_sync_time")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            syncOnStart = prefs[Keys.SYNC_ON_START] ?: true,
            syncServerReads = prefs[Keys.SYNC_SERVER_READS] ?: true,
            syncIncludeStarred = prefs[Keys.SYNC_INCLUDE_STARRED] ?: false,
            syncIntervalMinutes = prefs[Keys.SYNC_INTERVAL_MINUTES] ?: 60,
            syncItemLimit = prefs[Keys.SYNC_ITEM_LIMIT] ?: 1000,
            amoled = prefs[Keys.AMOLED] ?: false,
            dynamicColor = prefs[Keys.DYNAMIC_COLOR] ?: false,
            listDensity = prefs[Keys.LIST_DENSITY] ?: 1,
            listFontSizeSp = prefs[Keys.LIST_FONT_SIZE] ?: 16,
            listDivider = prefs[Keys.LIST_DIVIDER] ?: true,
            listViewMode = prefs[Keys.LIST_VIEW_MODE] ?: 1,
            listSortOldest = prefs[Keys.LIST_SORT_OLDEST] ?: false,
            listUnreadOnly = prefs[Keys.LIST_UNREAD_ONLY] ?: false,
            retentionDays = prefs[Keys.RETENTION_DAYS] ?: 7,
            webViewEmbedded = prefs[Keys.WEB_VIEW_EMBEDDED] ?: true,
            lastSyncTime = prefs[Keys.LAST_SYNC_TIME] ?: 0L,
        )
    }

    suspend fun setSyncOnStart(value: Boolean) {
        context.dataStore.edit { it[Keys.SYNC_ON_START] = value }
    }

    suspend fun setSyncServerReads(value: Boolean) {
        context.dataStore.edit { it[Keys.SYNC_SERVER_READS] = value }
    }

    suspend fun setSyncIncludeStarred(value: Boolean) {
        context.dataStore.edit { it[Keys.SYNC_INCLUDE_STARRED] = value }
    }

    suspend fun setSyncIntervalMinutes(value: Int) {
        context.dataStore.edit { it[Keys.SYNC_INTERVAL_MINUTES] = value }
    }

    suspend fun setSyncItemLimit(value: Int) {
        context.dataStore.edit { it[Keys.SYNC_ITEM_LIMIT] = value }
    }

    suspend fun setAmoled(value: Boolean) {
        context.dataStore.edit { it[Keys.AMOLED] = value }
    }

    suspend fun setDynamicColor(value: Boolean) {
        context.dataStore.edit { it[Keys.DYNAMIC_COLOR] = value }
    }

    suspend fun setListDensity(value: Int) {
        context.dataStore.edit { it[Keys.LIST_DENSITY] = value }
    }

    suspend fun setListFontSize(value: Int) {
        context.dataStore.edit { it[Keys.LIST_FONT_SIZE] = value }
    }

    suspend fun setListDivider(value: Boolean) {
        context.dataStore.edit { it[Keys.LIST_DIVIDER] = value }
    }

    suspend fun setListViewMode(value: Int) {
        context.dataStore.edit { it[Keys.LIST_VIEW_MODE] = value }
    }

    suspend fun setListSortOldest(value: Boolean) {
        context.dataStore.edit { it[Keys.LIST_SORT_OLDEST] = value }
    }

    suspend fun setListUnreadOnly(value: Boolean) {
        context.dataStore.edit { it[Keys.LIST_UNREAD_ONLY] = value }
    }

    suspend fun setRetentionDays(value: Int) {
        context.dataStore.edit { it[Keys.RETENTION_DAYS] = value }
    }

    suspend fun setWebViewEmbedded(value: Boolean) {
        context.dataStore.edit { it[Keys.WEB_VIEW_EMBEDDED] = value }
    }

    suspend fun lastSyncTime(): Long = settings.first().lastSyncTime

    suspend fun setLastSyncTime(value: Long) {
        context.dataStore.edit { it[Keys.LAST_SYNC_TIME] = value }
    }

    suspend fun current(): AppSettings = settings.first()
}
