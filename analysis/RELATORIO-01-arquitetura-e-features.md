# Relatório 01 — gReader 5.2.2: Arquitetura, Recursos e Stack Original

> Análise estática do APK `com.noinnion.android.greader.reader` 5.2.2-424 (SHA-256 `20f587379f303da151d2394868d9e8678f8d26f59c285383c8eb0214a3a6e2a9`) com REA + JADX headless 0.7.1. O APK nunca foi executado. Data: 2026-10-08.

## 1. Sumário executivo

O gReader é um leitor de RSS/feeds com 4 modos de conta (Feedly, The Old Reader, Inoreader, RSS local), sincronização bidirecional, leitura offline com extração de conteúdo (Readability), podcasts, leitura em voz alta (TTS), 4 widgets, 5 temas e integrações de automação (Tasker, DashClock).

Arquitetura interna: uma **camada Java legada** (~190 classes com nomes preservados: activities, services, ContentProvider, diálogos) sobre um **núcleo Kotlin moderno ofuscado pelo R8** (~7.7 mil classes em `defpackage.*`: módulos `readerCore`, `htmldownloader`, `htmlextractor`, DI via Koin, coroutines/RxJava). O app tem `minSdk 21`, `targetSdk 29`, usa armazenamento externo legado (`/gReader/...`), ButterKnife, ContentProvider+SQLite cru, serviços `IntentService` com notificações `startForeground`, AdMob + Firebase + Play Billing 3.0.3.

Para o rebuild (Android 16+, leve, moderno) há paridade funcional clara a atingir e um mapeamento direto de modernização (seção 19).

**Limites**: código ofuscado limita a leitura do núcleo de sync; conclusões marcadas como **observado** (bytecode/recursos) ou **inferido**. Sem execução/runtime. Nomes de método de app foram renomeados (a, b, c...) mas overrides de framework são legíveis.

## 2. Artefatos e evidências

| Arquivo | Conteúdo |
| --- | --- |
| `analysis/01-package-inspection.json` | Evidência REA `ev_d8b86e653...` da inspeção do pacote (manifesto decodificado, permissões) |
| `analysis/02-manifest-summary.json` | Componentes do manifesto extraídos |
| `analysis/03-all-classes.json` | Inventário completo: 10.160 classes |
| `analysis/04-greader-classes.txt` / `05-all-classes.txt` | Listas de classes (próprias / todas) |
| `analysis/06-strings-all.txt` | 66.916 strings dos 2 DEX (endpoints, prefs, mensagens) |
| `analysis/07-evidence-bundle.json` | Bundle REA da sessão MCP: 47 registros de evidência |
| `analysis/assets/` | `feed.html`, `feed.css`, `welcome_listings.json`, `TZDB.dat` extraídos |

## 3. Identidade

| Campo | Valor |
| --- | --- |
| Package | `com.noinnion.android.greader.reader` |
| Versão | 5.2.2-424 (versionCode 2100522) |
| minSdk / targetSdk / compileSdk | 21 / **29** / 29 |
| Classes / recursos | 10.160 / 1.782 |
| DEX | classes.dex (8,2 MB) + classes2.dex (4,4 MB) |
| APK | ~11,5 MB (x86/ARM + mips; inclui `liblanguage_id_jni.so`, `libpl_droidsonroids_gif*.so`) |
| Application | `com.noinnion.android.greader.ReaderApp` (`allowBackup=true`, `usesCleartextTraffic=true`, `requestLegacyExternalStorage=true`) |
| Assinatura | não verificada (limite do provider) |

## 4. Stack original (observado)

| Área | Tecnologia | Evidência |
| --- | --- | --- |
| Linguagem | Java legado + Kotlin (módulos `readerCore`, `gReader_prodRelease`, `htmldownloader`, `htmlextractor`) | `.kotlin_module` no APK; classes nomeadas vs `defpackage.*` |
| DI | Koin (`koin-android`) | `ReaderApp.onCreate` → `o08`/`l08` (Koin) |
| Async | Coroutines + RxJava/RxKotlin (ofuscados) + AsyncTask pools | `kotlinx.coroutines`; `Completable.fromCallable` em `FeedlyLoginActivity`; `FeedService` usa `AsyncTask.THREAD_POOL_EXECUTOR` |
| Rede | OkHttp 134 classes + Retrofit2 (+Gson converter) | namespaces `okhttp3.*`, `retrofit2.*` |
| Parsing HTML/feeds | Jsoup 57 + Readability4J (Kotlin) + parser AntennaPod (`client.rss.syndication.*`) | RssReaderClient; módulo `htmlextractor`; `Readability4J.kotlin_module` |
| Banco | SQLite via `ContentProvider` próprio (`ReaderProvider`) + SQLiteQueryBuilder | `ReaderProvider.query` |
| UI | AppCompat + Material Components + ConstraintLayout + ViewPager2 + ButterKnife (38 refs `_ViewBinding`) + EventBus + Fragments | inventário de classes |
| Imagens | Picasso + ImageLoaderProvider custom (cache próprio) + GIF (pl.droidsonroids) | providers no manifesto |
| Texto | Calligraphy (fontes) + threetenbp (`TZDB.dat`) + Spectrum (color picker de temas) | assets e namespaces |
| Player | `MediaPlayer` próprio em `MusicService` (sem ExoPlayer/media3) | callbacks `onPrepared/onCompletion/onError` |
| Background | `IntentService` + receivers próprios + WorkManager (presente; uso direto não confirmado) | `SyncService`, `CleanService`, `ScheduleReceiver`, `androidx.work.*` |
| TTS | Android TTS + MLKit language-id (`langid_model.smfb.jpg` + `liblanguage_id_jni.so`) | prefs `preference_tts_auto_detect_language` |
| Monetização | AdMob (248 classes) + Play Billing 3.0.3 + rewarded ads | manifesto, strings `premium_*` |
| Analytics | Firebase Analytics/Crashlytics/Config/In-App Messaging/GDPR dialog | `ReaderApp.onCreate` (Remote Config defaults, GDPR AdMob) |
| Automação | Tasker/Locale (`com.twofortyfouram.locale`), DashClock extension | manifesto |
| Implantação | ButterKnife, MenyDrawer, BottomSheet, MaterialSearchView, GlowPadView, Slidr (swipe-back), RecyclerViewSlideItem | namespaces |

## 5. Componentes do app (do manifesto)

**Activities (28)** — principais:
- `ui.login.LoginActivity` (LAUNCHER) + 4 logins por backend: `FeedlyLoginActivity` (deep link `greader://feedly`), `InoreaderLoginActivity`, `OldReaderLoginActivity`, `RssReaderLoginActivity` ("RSS Reader" local, sem conta)
- `ui.home.HomeActivity` / `HomeTabletActivity` (2 painéis) / `WelcomeActivity`
- `ui.item.ItemActivity` + `ui.itemlist.ItemListFragment` (leitor + lista)
- `ui.setting.SettingsActivity` (+ fragments: ArticleControls, MarkRead, TextToSpeech), `SendLogActivity`
- `ui.subscription.*`: ManageSources, Subscribe (recebe **ACTION_SEND** de qualquer app), ImportOpml, diálogos
- `ui.download.DownloadActivity` / `DownloadFeedActivity` (offline/podcast)
- `ui.tts.TtsActivity` (com `premiumUpgrade`, `showLanguageList`)
- 4 configs de widget, `ui.media.MusicActivity`, `ui.locale.LocaleEditActivity`, `PremiumActivity`

**Services (9 próprios)**: `SyncService` (sync c/ notificação), `FeedService` (salva artigos offline, thread pool 1–3), `DownloadService` (mídia, notificação `download_media_v2`), `CleanService` (limpeza de cache), `TtsService`, `WidgetService`, `WidgetLargeV11Service`, `MusicService` (ações PLAY/PAUSE/FORWARD/REWIND + URL/PATH), `dashclock.UnreadExtension`

**Receivers (9 próprios)**: `WidgetUpdateReceiver` (exported=true, ação `...action.WIDGET_UPDATE`), 4 AppWidgetProviders, `MusicIntentReceiver` (MEDIA_BUTTON), `ScheduleReceiver` (aplica `ACTION_SYNC`/`ACTION_CLEAN`), `OnBootCompletedReceiver`, `OnUpgradeReceiver`, `LocaleFireReceiver` (Tasker)

**Providers (6 próprios)**: `ReaderProvider` (autoridade `...reader.provider`), `LocalFileProvider`, `ImageLoaderProvider`, `FileProvider`, `WidgetProvider` (dados p/ widget list), PicassoProvider

**Permissões**: INTERNET, ACCESS_NETWORK_STATE, VIBRATE, READ/WRITE_EXTERNAL_STORAGE, FOREGROUND_SERVICE, RECEIVE_BOOT_COMPLETED, WAKE_LOCK, INSTALL_SHORTCUT, CHECK_LICENSE, BILLING

## 6. Modelo de dados (observado em `ReaderProvider.query`)

Tabelas SQLite: `subscription` (feeds), `tag` (pastas/labels), `tag2sub`, `tag2item`, `history`, `item` (artigos; colunas citadas: `audio` — migração `ALTER TABLE item ADD COLUMN audio TEXT;`, `video`, `cached`, `sync_time`).

URIs/operações (matcher `u`):
- `1/2/3` = beginTransaction / setTransactionSuccessful / endTransaction (API de transação via ContentResolver!)
- `4` = recriar/limpar banco; `5` = rawQuery; `10/11` = subscription item/dir
- `20/21` = tag; `30/31/32` = tag2sub (+ UNION tag×subscription com `unread_count`, `sort`, `newest_item_time`, `sync_excluded`...)
- `40/41` = item item/dir (MIME `vnd.android.cursor.item/dir...provider.item`)
- `50/51` = tag2item; `60/61` = history
- Consultas paginam com sufixo `" limit "` parseado manualmente; `setNotificationUri` em todas as queries (UI reativa via Cursor)

## 7. Backends e sincronização

**4 backends** (LoginActivity.onClick — observado):
| Backend | Login | Sinais de API |
| --- | --- | --- |
| Feedly | `greader://feedly` OAuth (code → token) | v3 completo: `/auth/auth`, `/auth/token`, `/profile`, `/categories`, `/subscriptions`, `/tags`, `/markers`, `/streams/-/ids`, `/streams/-/contents` |
| Inoreader | OAuth próprio | `https://inoreader.com` / `innoreader.com` (`base_url_innoreader`) |
| The Old Reader | OAuth próprio | `https://theoldreader.com` |
| RSS local ("RSS Reader") | sem conta; `RssReaderClient` | `clientService=7`; persiste DataSet/DataStarred; parse RSS/Atom via `syndication.*` (base AntennaPod) |

Feedly OAuth: `response_type=code&client_id=…` para `/v3/auth/auth`; troca em `/v3/auth/token` (strings `client_id`, `client_secret`, `oauth2_token_refresh`).

**Sync**: `SyncService` (IntentService) → notificação "Synchronization" → núcleo ofuscado (`yo6`/`rn6` = interfaces de cliente por serviço). Agendamento via `ScheduleReceiver` (`ACTION_SYNC`/`ACTION_CLEAN`) + boot. Interface de cliente (`rn6`) define: subscribe/unsubscribe, markAsRead/Unread (individual, listas, all), tags (criar/renomear/mover), itens (labels), listas (`handleItemList`, `handleReaderList`).

**Limpeza/cache** — `CleanService.a()` (observado):
- Só roda com tela apagada (`!isInteractive()`)
- Image cache em `EXTERNAL/gReader/.image-cache/`; mantém N imagens (pref `cache_auto_cleanup_images`, default 1000), apaga as mais antigas
- Artigos em `EXTERNAL/gReader/cache/`; percorre itens com `cached > 0` em lotes de 1000 por `sync_time`, apaga arquivos órfãos
- Prefs: `cache_auto_clean_read/unread`, `cache_keep_starred`, `cache_duration`, `cache_offline_location`, `cache_clear_articles/duplicates/images`, `offline_save_data_traffic`

## 8. Leitor de artigos (renderização)

Três modos de conteúdo (menu do `ItemActivity.onOptionsItemSelected` → `cache_view` / `web_view` / `feed_view`):
1. **Feed content** — HTML do próprio feed, renderizado no `feed.html` template (`assets/feed.html` + `css/feed.css`) dentro de WebView custom (`extendedWebView`, `WebViewPager`, `ItemWebView`)
2. **Reader cache** — conteúdo completo extraído: Readability4J (Kotlin, `htmlextractor`) e/ou readability.js injetado no WebView (scheme `readability://` desliga o modo leitura)
3. **Web view** — página original

Recursos observados:
- **Mobilizers** (feature oculta): Readability, **Instapaper** (`http://mobilizer.instapaper.com/m?u=`), **Google WebLight** (`http://googleweblight.com/i?u=`)
- **Tradução**: `http://translate.google.com/translate?sl=auto&tl=<lang>` (pref `preference_service_translate_summary`)
- Esquemas internos: `youtube://` (YoutubePlayerActivity), `readaloud://` (TTS), `readability://`
- Controles: fontes (xsmall…xxxlarge, `font_size_up/down`), 5 temas (`item_checked_default/sepia/green/dark/black`), `item_autoload_link`, `item_autoload_reading_mode`, copiar link/imagem, pular página (`WebViewPager` paging), orientação de mídia nativa
- Layouts phone/tablet (`item_fragment` / `item_tablet_fragment`)

## 9. Offline

- `FeedService`: baixa artigo completo + imagens (`item_ids`, `save_page`), prefs `offline_full_content_format`, `item_user_agent`, `offline_save_data_traffic`; até 3 workers AsyncTask
- `DownloadService`/`DownloadActivity`/`DownloadFeedActivity`: download de mídia (podcasts/vídeos) com notificação de progresso
- Cache em armazenamento externo raiz: `EXTERNAL/gReader/cache/` e `.image-cache/` (**storage legado**)
- Backup/restore próprio (`PrefRestoreDialog`, `allowBackup=true`)

## 10. Podcasts / áudio

- Modelo de feed estendido: `FeedMedia`, `FeedImage`, `Chapter`, `SimpleChapter`, `ID3Chapter`, `VorbisCommentChapter`; leitor OGG/Vorbis (`OggInputStream`, `VorbisCommentReader`), `podlove.org/simple-chapters`
- `MusicService`: playback com ações (TOGGLE/PLAY/PAUSE/FORWARD/REWIND/STOP, URL http, PATH file), `MusicIntentReceiver` (botões de headset), `MusicActivity`
- Descoberta: `SubscribePodcastFragment` (busca de podcasts)
- Offline: pref `preference_offline_load_audio` ("carregar áudio para offline")
- Capa/mídia renderizadas no template (`media_overlay.png`, `gif_overlay.png`)

## 11. TTS / Voice Reading

- `TtsActivity` + `TtsService` + `TtsActionReceiver` (`action.INIT_TTS`), notificação com controles (`notification_tts`), playlist (`array_tts_play_next/play_now/current_page/full_content/append`)
- Prefs: `preference_tts_auto_detect_language` (MLKit), `preference_tts_voices`, `service_tts_mark_read`, `show_tts_button`
- Premium: `limit_tts` / `premium_limit_tts`, `premium_voice_reading_description` (limite grátis), `PremiumActivity`
- Controle pela WebView via `readaloud://`

## 12. Widgets

4 tamanhos (small/medium/large/icon) + `WidgetLargeV11Service` (lista), `WidgetUpdateReceiver` (atualização custom), dados via `WidgetProvider` (ContentProvider). Opções: cores de fundo (dark/gradient/label/transparent), limite de itens, gerenciar fontes (`widget_manage_source_dialog`), refresh manual.

## 13. Assinaturas, busca e organização

- **Share-to-subscribe**: `SubscribeActivity` recebe `ACTION_SEND` de outros apps
- **OPML**: `ImportOpmlActivity`
- **Busca de feeds**: Feedly search (`FeedlyEndpoint.findFeeds` → `FeedSearchResult`) em `SubscribeSearchFragment`
- **Pastas/tags**: criar/renomear/editar subscrição (`SubEditDialog`, `ChangeFolderDialog`, `TagEditDialog`)
- **Filtros de busca salvos**: `SearchFilterDialog` (`filter_query`, `filter_location`)
- **Histórico**, limpeza de duplicatas, marcar tudo lido
- gerenciamento de fontes (`ManageSourcesActivity`), bundles de fontes (`SubscribeBundleDialog`, `BundleItem`)

## 14. Personalização e dispositivo

- Temas: default, sepia, green, dark, black (cores `background_*`, `item_checked_*`)
- Fontes configuráveis; fonte custom (Lato/OpenSans embarcadas)
- Layout tablet (2 painéis) e phone; **multiwindow** Samsung/LG (metadados obsoletos)
- Locale/Tasker (automação), DashClock, atalhos na launcher (`INSTALL_SHORTCUT`)
- `WelcomeActivity` + `welcome_listings.json` (guia in-app: temas, offline, filtros, podcasts, voz, widgets, features ocultas)

## 15. Monetização e privacidade

- **AdMob** (app id `ca-app-pub-6875289151086704~1486326566`, 1 App ID real no manifesto) + banner/interstitial/rewarded; **rewarded ad → dias de premium** (`premium_rewarded_ad_granted_days`)
- **Play Billing 3.0.3**: assinatura mensal/anual (`premium_monthly_subscription_product_id`, `premium_yearly_subscription_product_id`), `rxbilling`
- GDPR dialog (michaelflisar) para consentimento de anúncios; analytics padrão desliga personalização de anúncios
- Firebase Analytics/Crashlytics/Remote Config/In-App Messaging; `SendLogActivity` para suporte

## 16. Implicações para o rebuild (Android 16+, leve, moderno)

### 16.1 Stack alvo sugerido

| Original | Alvo moderno |
| --- | --- |
| Java + ButterKnife + Fragments/Views | **Kotlin 2.x + Jetpack Compose + Material 3** (predictive back, edge-to-edge, dynamic color) |
| Activities múltiplas + menudrawer | Navigation Compose / Navigation 3, uma Activity |
| ContentProvider + SQLiteQueryBuilder + Cursor | **Room** + Paging 3 + Flow (sem ContentProvider; usar `SavedStateHandle`) |
| RxJava + EventBus + AsyncTask | **Coroutines + Flow/StateFlow** |
| `IntentService` + receivers + AlarmManager-ish | **WorkManager** (sync periódico com constraints; expedited p/ manual) |
| Foreground services sem tipo | FGS **com tipo** (`dataSync`, `mediaPlayback`, `shortService`), notificação obrigatória |
| Picasso + ImageLoaderProvider + cache externo | **Coil 3** (disk cache interno, OkHttp) |
| WebView + readability.js + mobilizers | Manter WebView + modo leitura; Readability4J (compatível Kotlin) para extração; opcional motor nativo |
| MediaPlayer próprio | **Media3 ExoPlayer + MediaSessionService** (capítulos, velocidade, filas) |
| TTS manual + MLKit language-id | Android TextToSpeech moderno (vozes); language-id on-device (MLKit continua ok) |
| AppWidgetProvider + RemoteViews | **Glance** (widgets Compose) mantendo 4 tamanhos |
| Billing 3.0.3 | **Play Billing 7.x** (subscriptions + rewarded ads opcionais) |
| AdMob/Firebase/Analytics embutidos | **Remover por padrão** (app leve, sem tracking); manter hooks opcionais |
| targetSdk 29 + storage legado | **targetSdk 36**; scoped storage, MediaStore/SAF para exportar/importar; sem `/gReader` na raiz externa |
| minSdk 21 | Recomendo **26–28+** (corta fallbacks legados) |
| Multiwindow Samsung/LG, INSTALL_SHORTCUT | `windowSizeClass`, ShortcutManagerCompat |
| Tasker/Locale, DashClock | Manter Locale protocol (opcional); modernizar DashClock→widgets próprios |

### 16.2 Pontos de atenção técnicos

- **APIs de terceiros**: Feedly v3 exige OAuth + client próprio (o client_id do app original é de terceiros — não reutilizar); Inoreader (API oficial p/ devs, OAuth2), The Old Reader (API via `theoldreader.com`); RSS local não depende de nuvem
- **Sync bidirecional robusto**: modelo de operações pendentes (fila de mutações offline-first com resolução de conflitos), idempotência de markers (`/v3/markers`), paginação de streams (`continuation`), rate limiting
- **Obfuscação limita detalhes**: formato exato dos payloads Feedly/Inoreader e o agendador exato do sync precisam de análise adicional ou consulta às APIs oficiais
- **Extra de peso**: o APK original é ~11,5 MB, mas carrega Firebase+AdMob+MLKit+Apache Commons; o rebuild pode ficar **muito menor** sem ads/analytics (estimativa: <5 MB)
- **16 KB page size** (Android 15+): se houver qualquer `.so`, alinhar; no rebuild enxuto, evitar libs nativas
- **Cleartext**: o original tem `usesCleartextTraffic=true` (Instapaper mobilizer é http); no novo app, restringir a HTTPS
- **Notificações**: Android 13+ exige permissão runtime (`POST_NOTIFICATIONS`); Android 12+ restrições de exact alarms — agendar sync com WorkManager

### 16.3 Feature checklist de paridade (MVP → completo)

**MVP**: contas (Feedly OAuth + RSS local), árvore pastas/feeds, lista de artigos (unread/starred), leitor (feed/artigo/web + modo leitura), marcar lido/estrela, marcar tudo lido, sync manual+periódico, busca/filtros, ta manhos de tema claro/escuro, offline básico (cache de imagens).

**Completo**: Inoreader + Old Reader, podcasts (player Media3, capítulos, offline), TTS/voice reading, widgets, OPML, share-to-subscribe, downloads, cache automático avançado, tablet/desktop adaptativo, automação (Tasker/atalhos), premium (se quiser), backup/restore.

## 17. Próximos passos de análise

1. **Fluxo de sync em detalhe** — seguir referências de `SyncService`/`yo6` (ofuscado) para etapas, marcadores e conflitos
2. **Formato de cada backend** — extrair payloads de `/streams/-/contents`, `/markers` (Feedly) e APIs Inoreader/OldReader a partir dos modelos/strings restantes
3. **Migrations do banco** — minerar todas as strings `ALTER TABLE`/`CREATE TABLE` para o schema completo de `item`
4. **Preferências completas** — montar lista definitiva de settings (para paridade de opções)
5. **Fluxo OAuth exato** — parâmetros, scopes, refresh (strings + `oauth_activity`)
6. **TTS pipeline** — parágrafos, playlist, auto-detect (MLKit), integração com leitor

## 18. Proveniência

- Ferramenta: REA 6.0.0 + jadx-headless-mcp 0.7.1 (commit `5844800a…`, Apache-2.0) — análise somente estática, sem execução
- O APK foi analisado em `/tmp` pelo engine; cópias privadas por sessão; nenhum arquivo original modificado
- Este relatório mistura **observações** (bytecode/manifesto/assets estudados) e **inferências** (comportamento deduzido); itens desconhecidos estão marcados quando relevantes
