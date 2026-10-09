# Relatório 03 — Dados e Preferências: paridade completa

> Fechamento do item 3 do plano: schema final do banco + inventário definitivo de preferências (telas XML decodificadas com aapt2 e telas construídas em código). Base: APK 5.2.2-424, análise estática REA/JADX. Artefatos gerados em `analysis/prefs/`.

## 1. Banco de dados final

- **Nome/arquivo**: `reader.db` (mensagem do app: "Couldn't open reader.db for reading: Check your storage.")
- **Local**: `EXTERNAL_STORAGE/gReader/db/reader.db` (legado; no rebuild: `Room` em armazenamento interno/`noBackupFilesDir`)
- Acesso somente via `ReaderProvider` (ContentProvider) com URIs transacionais e operações em massa (§2)

### 1.1 `item` (artigos) — schema final

```sql
CREATE TABLE IF NOT EXISTS item (
  _id INTEGER PRIMARY KEY,
  sub_id INTEGER NOT NULL,
  uid TEXT NOT NULL UNIQUE,          -- id estável do servidor (Feedly entryId / GData hex id / URL local)
  title TEXT NOT NULL,
  content TEXT,                      -- HTML do artigo (feed ou extraído)
  author TEXT,
  link TEXT,                         -- link canônico (Google redirects desembrulhados)
  image TEXT,                        -- capa/thumbnail
  video TEXT,
  audio TEXT,                        -- mídia de podcast
  sharer TEXT,
  published_time INTEGER NOT NULL DEFAULT 0,   -- segundos
  updated_time INTEGER NOT NULL DEFAULT 0,
  starred INTEGER NOT NULL DEFAULT 0,
  cached INTEGER NOT NULL DEFAULT 0,           -- conteúdo offline baixado?
  read INTEGER NOT NULL DEFAULT 0,
  read_time INTEGER NOT NULL DEFAULT 0,        -- quando foi lido (local)
  keep_unread INTEGER NOT NULL DEFAULT 0,      -- "manter não lido" (ignora mark-all-read)
  sync_time INTEGER NOT NULL DEFAULT 0         -- último sync em que foi visto/enviado
);
```
Migrações observadas (histórico de upgrades): `ALTER TABLE item ADD COLUMN` para `image`, `video`, `audio`, `sharer`, `cached`.

### 1.2 Demais tabelas

| Tabela | Colunas (final) | Notas |
| --- | --- | --- |
| `subscription` | _id, uid, title, feed_url, html_url, icon_url, sortid, sort, sort2, unread_count, newest_item_time, sync_time, sync_excluded, image_fit, javascript, offline_content, display_content, link_format, auto_readability, user_agent, notification, charset, hidden | Overrides por feed: `-1` = herdar global (offline_content/display_content/link_format/auto_readability/user_agent); `notification=1` entra nas notificações por feed; `hidden`/`sync_excluded` controlam visibilidade/sync |
| `tag` | _id, uid, type, label, sortid, sort, unread_count, sync_time, sync_excluded, hidden | Tipos: **1** = estado estrelado (global.saved), **9** = estados fixos (não removidos no pull), **10** = pasta vazia, **11** = pasta com feeds (contagem = soma dos feeds); outros = labels de item |
| `tag2sub` | _id, tag_uid, sub_id, sync_time | Pasta ↔ feed |
| `tag2item` | _id, tag_uid, item_uid, action, sync_time | Labels/estados por item; `action=1` add, `2` remove, `0` sincronizado; `-1` = ignorado nas contagens |
| `history` | _id, type, params, sync_time, done | **Outbox** de sync (tipos 1–8; ver Relatório 02) |
| `item_sync` | _id, uid | Staging de uids para operações de sync |

## 2. Operações em massa do provider (URIs 70–76)

SQL exato (observado em `ReaderProvider.d`):

- **70 `update_newest_time`**: `UPDATE subscription SET newest_item_time = (SELECT MAX(updated_time) …)` — mantém a ordenação da lista de feeds
- **71 `update_unreads`** (3 statements): contadores por feed (soma de itens não lidos), pastas tipo 11 (soma dos feeds) e demais tags (COUNT via `tag2item`, excluindo `action=-1`)
- **72 `update_reads`** (reconciliação servidor↔local, usada pelo callback de IDs):
  - `UPDATE item SET read=1 WHERE read=0 AND keep_unread=0 AND uid NOT IN (ids)`
  - `UPDATE item SET read=0 WHERE read=1 AND read_time < sync_time AND uid IN (ids)`
- **73 `update_mark_tag`**: marca tudo lido por pasta/tag (respeita `keep_unread`; se `all=false`, só itens com `updated_time < ts/1000`)
- **74 `update_starred`**: espelha estrelados (`uid IN ids` → 1; fora → 0) e limpa `tag2item` do tag de estrela
- **75 `update_mark_read`**: `UPDATE item SET sync_time = read_time WHERE _id IN (ids)` — confirma leituras locais já enviadas
- **76 `update_tag2item`**: remove labels órfãos (item não existe mais)

**Regras de ouro para o rebuild**: `keep_unread` protege o item do mark-all-read; `read_time < sync_time` distingue "lido localmente e já visto pelo servidor"; a reconciliação do 72 é exatamente o que evita loops de "ressuscitar" não lidos.

## 3. Preferências internas (não-UI)

| Chave | Uso | Segurança |
| --- | --- | --- |
| `google_auth` / `google_auth_time` | Access token + timestamp (Feedly 15 min; GData 3 dias) | 🔴 texto puro |
| `oauth2_token_refresh` | Refresh token Feedly | 🔴 texto puro |
| `google_passwd` | Senha GData (Old Reader/Inoreader) | 🔴 texto puro |
| `login_method` | Serviço atual (5 Feedly / 6 Old Reader / 7 RSS / 8 Inoreader) | — |
| `last_sync_time`, `sync_last_sync_count`, `unread_count` | Estado de sync/badge | — |
| `service_ril_username` / `service_ril_password`, `service_instapaper_password` | Credenciais Pocket/Instapaper | 🔴 texto puro |
| `inoreader_base_url` | Base URL configurável do Inoreader | — |
| `sync_service_v2`, `clean_service_v2`, `download_media_v2` | IDs de canais de notificação | — |
| `offline_usage` | Política de offline (0/1/2) | — |

No rebuild: **EncryptedSharedPreferences/Keystore** obrigatório; senhas de terceiros devem virar tokens OAuth (nunca guardar senha).

## 4. Inventário completo de preferências

Abaixo, as duas partes geradas automaticamente:

1. **Telas XML** (decodificadas com aapt2 — chaves, tipo de widget, default e lista de valores)
2. **Telas construídas em código** (`SettingsActivity.b`: sync, offline, services, other + navegação)

# Inventário de preferências (gerado)

### pref_article_controls.xml

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |
| `auto_hide_item_controls` | CheckBox | true |  | Auto hide controls |
| `show_navigation_bar` | CheckBox | true |  | Show navigation bar |
| `show_item_bar` | CheckBox | true |  | Show item bar |
| `show_zoom_button` | CheckBox | true |  | Zoom buttons |
| `item_volume_key_action` | List | 1 | 0, 1, 2 | Volume key (article view) |
| `item_navigation_gesture` | List | 1 | 0, 1 | Navigation gestures |
| `item_double_tap_action` | List | 9 | 0, 1, 3, 4, 5, 14, 9, 13 | Double tap |
| `item_inapp_browser` | CheckBox | true |  | In-app Browser |
| `item_inapp_youtube` | CheckBox | true |  | In-app Youtube player |

### pref_backup.xml

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |

### pref_cache.xml

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |
| `db_cache_location` | List | local | local, sdcard | Database storage |
| `cache_auto_clean_read` | List | 1 | 0, 1, 2, 3, 5, 7, 14, 28, -1 | Auto cleanup read items |
| `cache_auto_clean_unread` | List | -1 | 1, 2, 3, 5, 7, 14, 28, -1 | Auto cleanup unread items |
| `cache_keep_starred` | CheckBox | true |  | Keep starred items |
| `cache_keep_cached` | CheckBox | false |  | Keep cached items |
| `cache_auto_cleanup_images` | List | 1000 | 0, 100, 200, 500, 1000, 2000, -1 | Auto cleanup image cache |

### pref_look_and_feel.xml

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |
| `application_language` | List |  | , az, da, de, en, es, it, nl, ru, sk, sl, fr, gl, iw, hu, ko, lt, pl, pt_PT, pt_BR, tr, uk, ug, sv, cs, bg, ca, vi, ar, zh_CN, zh_TW, zh_HK, ja | Languages |
| `application_theme` | List | 0 | 0, 5, 3, 1, 2 | Themes |
| `application_theme_auto_dark` | CheckBox | true |  | Auto Dark Mode |
| `application_theme_dark` | List | 1 | 1, 2 | Dark Theme |
| `fullscreen` | CheckBox | false |  | Fullscreen |
| `use_phone_ui` | CheckBox | false |  | Use phone UI |
| `home_landscape_dual_pane` | CheckBox | true |  | Landscape dual view |
| `home_hide_article_list` | CheckBox | false |  | Hide article list pane |
| `item_list_volume_key_action` | List | 1 | 0, 1, 2 | Volume key (article list) |
| `item_list_font_size` | List | 16 | 12, 14, 16, 18, 20 | List font size |
| `item_nav_bar` | List | 1 | 0, 1, 2 | Navigation bar |
| `item_pinch_zoom` | CheckBox | false |  | Pinch zoom |
| `item_hardware_accelerated` | CheckBox | true |  | Hardware acceleration |

### pref_mark_read.xml

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |
| `fab_position` | List | 2 | 0, 1, 2 | Position |
| `fab_style` | List | 1 | 1, 2 | Style |
| `fab_opacity` | List | 100 | 0, 25, 50, 75, 100 | Opacity |
| `show_feeds_after_mark_all` | CheckBox | false |  | Show feeds after Mark all |
| `confirm_mark_as_read_all` | CheckBox | true |  | All items |
| `confirm_mark_as_read_sub` | CheckBox | false |  | Feed or Tag |

### pref_menu.xml

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |

### pref_notification.xml

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |
| `sync_notifiable` | CheckBox | true |  | Notification |
| `sync_notify_sound` | CheckBox |  |  | Sound |
| `sync_notify_sound_ringtone` | Ringtone | content://settings/system/notification_sound |  | Notification sound |
| `sync_notify_vibrate` | CheckBox |  |  | Vibration |
| `sync_notify_led` | CheckBox |  |  | LED |
| `sync_notifiable_custom` | CheckBox | false |  | Custom notification (PREMIUM) |
| `sync_notify_custom_sound` | CheckBox |  |  | Sound |
| `sync_notify_custom_sound_ringtone` | Ringtone | content://settings/system/notification_sound |  | Notification sound |
| `sync_notify_custom_vibrate` | CheckBox |  |  | Vibration |
| `sync_notify_custom_led` | CheckBox |  |  | LED |

### pref_other.xml

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |

### pref_quick_actions.xml

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |
| `item_list_left_action` | List | 3 | 0, 1, 2, 3, 4, 5, 14, 6, 7, 10, 11, 12 | Left action |
| `item_list_right_action` | List | 1 | 0, 1, 2, 3, 4, 5, 14, 6, 7, 10, 11, 12 | Right action |
| `item_list_swipe_action_ltr` | List | 0 | 0, 1, 2, 3, 4 | Swipe Left to Right |
| `item_list_swipe_action_rtl` | List | 0 | 0, 1, 2, 3, 4 | Swipe Right to Left |

### pref_reading.xml

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |
| `item_images_loading` | List | 1 | 0, 1, 2 | Load images |
| `item_load_link` | List | 0 | 0, 1 | Load article link |
| `item_autoload_link` | CheckBox | false |  | Autoload article link |
| `item_autoload_reading_mode` | CheckBox | false |  | Autoload reading mode |
| `item_user_agent` | List | 0 | 0, 1, 2, 3 | User Agent |

### pref_rich_list.xml

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |
| `rich_article_list` | CheckBox | true |  | Enable Rich Article List |
| `rich_show_snippet` | CheckBox | true |  | Show Snippet |
| `rich_show_thumb` | List | 2 | 0, 1, 2 | Show Preview Thumbnail |

### pref_text_to_speech.xml

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |
| `service_tts_auto_detect_language` | CheckBox | true |  | Auto detect language |
| `service_tts_read_title` | CheckBox | false |  | Read title |
| `service_tts_mark_read` | CheckBox | false |  | Mark read |


## Telas construídas em código (`SettingsActivity.b`)


### seção `sync`

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |
| `sync_auto_update` | List | 1 | 0, 1, 2 | Auto update |
| `sync_interval_hours` | List | 1 | 0, 1, 2, 4, 6, 12, 24 | Interval |
| `sync_custom_interval` | SeekBar |  |  | Custom Interval |
| `sync_item_limit` | List | 1000 | 100, 200, 500, 1000, 2000, 5000, 10000 | Item limit |
| `sync_include_starred` | CheckBox | false |  | Include starred items |
| `sync_on_start` | List | 0 | 0, 1, 2 | Sync on startup |
| `sync_server_reads` | CheckBox | true |  | Server reads |

### seção `offline`

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |
| `offline_usage` | List | 0 | 0, 1, 2 | Offline usage |
| `offline_feed_items_limit` | List | 50 | 10, 20, 50, 100, 200, 0, -1 | Item limit |
| `offline_autoload_cache` | CheckBox | true |  | Autoload cache |
| `offline_autoload_reading_mode` | List | 0 | 0, 1, 2 | Autoload reading mode |
| `offline_save_page_on_star` | CheckBox | false |  | Save page on star |
| `offline_load_full_content` | CheckBox | true |  | Load full content |
| `offline_full_content_format` | List | 3 | 1, 2, 3, 4 | Full content format |
| `offline_load_images` | CheckBox | false |  | Cache feed images |
| `offline_save_data_traffic` | CheckBox | true |  | Save data traffic |
| `offline_load_audio` | CheckBox | false |  | Load audio (PREMIUM) |
| `offline_load_video` | CheckBox | false |  | Load video (PREMIUM) |

### seção `notification`

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |

### seção `cache`

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |

### seção `reading`

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |

### seção `look_feel`

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |

### seção `rich_list`

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |

### seção `quick_actions`

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |

### seção `backup`

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |

### seção `other`

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |

### seção `services`

| key | widget | default | opções (values) | título |
| --- | --- | --- | --- | --- |
| `service_tts` | List | 2 | 0, 1, 2 | Voice-Reading |
| `service_translate` | CheckBox | true |  | Translate |
| `item_send_to` | List | 1 | 1, 2, 3, 4, 5 | Customize share content |
| `item_internal_share` | CheckBox | false |  | Internal share dialog |
| `service_everclip` | CheckBox | true |  | EverClip |
| `service_facebook` | CheckBox | true |  | Facebook |
| `service_twitter` | CheckBox | true |  | Twitter |
| `service_ril` | CheckBox | true |  | Pocket |
| `service_instapaper` | CheckBox | true |  | Instapaper |
| `service_google_mobilizer` | CheckBox | true |  | Google Mobilizer |
| `service_readability_mobilizer` | CheckBox | true |  | Readability Mobilizer |
| `service_tesla_unread` | CheckBox | false |  | Tesla Unread |


## 5. Chaves referenciadas no código sem tela própria

Curadoria a partir das strings do DEX (não aparecem em nenhuma tela de settings):

| Chave | Provável uso | Situação |
| --- | --- | --- |
| `sync_excluded` | Coluna de `subscription`/`tag` (excluir do sync) + pref auxiliar | Ativo (por feed) |
| `sync_offline` | Extra de intent do `SyncService` (sync offline manual) | Ativo (interno) |
| `sync_mark_all_read` | Ação "marcar tudo lido" em sincronização | Ativo (menu/ação) |
| `sync_first_before_open_tag` | Estratégia de ordenação de itens por pasta | Ativo (lógica interna) |
| `sync_custom_filter` | Filtro de busca persistido? | Provável legado |
| `sync_all` / `sync_all_offline` / `sync_selection` / `sync_selection_offline` | Seleção de feeds para offline/sync (diálogo de fontes) | Ativo (fluxo offline) |
| `sync_local_opimized` (typo original) | Otimização de sync local | Provável legado |
| `sync_button` / `sync_cancel_button` / `sync_new_articles` | Textos/estado de UI de sync | UI (não é pref) |
| `item_user_agent` | User agent customizado (em `pref_reading.xml`) | Ativo (tela reading) |
| `item_internal_share` / `item_send_to` | Compartilhamento (tela services) | Ativo |
| `enable_log_sampling_rules`, `ga_screen_*` | Analytics (Firebase) | Não portar |

## 6. Notas de migração (Android 16)

| Original | Novo |
| --- | --- |
| `reader.db` em `/gReader/db` (externo, `requestLegacyExternalStorage`) | Room interno (`getDatabasePath`), export/import opcional via SAF |
| 23 colunas de `subscription` com overrides `-1` | Manter o modelo `-1 = herdar` (é ótimo) ou `null` + resolução no repositório |
| `tag.type` 1/9/10/11 | Enum `TagType { STARRED, SYSTEM, FOLDER, FOLDER_WITH_FEEDS, LABEL }` |
| Contadores desnormalizados + bulk ops 70–76 | Manter (Room suporta `@Query` com UPDATE ... SELECT) — performance de lista |
| Preferências `SharedPreferences` + XML de settings | **DataStore (Preferences)** + tela Compose; migração 1:1 das chaves para não perder configuração de usuários |
| `history` outbox | Tabela Room `sync_operations` (tipos 1–8 mapeados) |
| 82+ prefs de UI | Reagrupar em: Aparência, Leitura, Sincronização, Offline & Cache, Notificações, Serviços & Compartilhamento, TTS, Backup |
| Senhas/tokens em texto puro | EncryptedSharedPreferences / OAuth2 |
| Chaves `google_*` | Renomear para `auth_*` no novo app (migração explícita) |

## 7. Evidências e arquivos

- `analysis/prefs/resources.txt` (dump aapt2), `analysis/prefs/xmltree/*.txt` (12 telas)
- `analysis/prefs/settings-inventory.json` / `settings-tables.md` (XML)
- `analysis/prefs/code-settings-inventory.json` / `code-settings-tables.md` (código)
- `analysis/prefs/SettingsActivity.b.onCreate.java` (fonte completa das telas de código)
- `analysis/sync/ReaderProvider.clinit.java` e `ReaderProvider.d.java` (URIs e SQL em massa)
- Scripts: `build_inventory.py`, `extract_code_prefs.py` (reproduzíveis via aapt2)

### Cobertura

- **Observado**: chaves, tipos, defaults, listas de valores, SQL das operações em massa, schema.
- **Curadoria**: chaves sem tela (tabela §5) — presença confirmada no DEX; uso exato não confirmado em todos os casos.
- **Não coberto**: valores de `entries`/`labels` de cada idioma (apenas en), e telas que só existem em runtime (diálogos de seleção de feeds).
