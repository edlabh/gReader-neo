# Relatório 04 — UI/UX: telas, fluxos e catálogos de interação

> Mapeamento das telas e interações do gReader 5.2.2 (estático, REA/JADX + aapt2) para orientar o redesign moderno (Android 16+, Compose/Material 3). Complementa os Relatórios 01–03.

## 1. Inventário de telas

| Tela (original) | Papel | Redesign moderno sugerido |
| --- | --- | --- |
| `LoginActivity` | Escolha de backend (Feedly, Old Reader, Inoreader, RSS local) + "getting started" | Onboarding Compose: bottom sheet "Adicionar conta" + Custom Tabs para OAuth (sem WebView) |
| `WelcomeActivity` | Guia/novidades (assets `welcome_listings.json`) + feedback | Onboarding/carrossel opcional; mover guia para web |
| `HomeActivity` | Lista principal (drawer + lista de artigos) | Single-activity + `NavigationSuiteScaffold` (drawer/rail adaptativo) + `LazyColumn` |
| `HomeTabletActivity` | 2 painéis (lista de feeds + artigos) | `ListDetailPaneScaffold` (adaptativo/foldable) |
| `ItemActivity` + `ItemFragment` | Leitor de artigo (3 modos) | Tela de leitura Compose + interop WebView + barra de ações |
| `ItemListFragment` | Lista de artigos (swipe, ações rápidas, seleção) | `LazyColumn` + `SwipeToDismissBox` + barra contextual + `SearchBar` |
| `TtsActivity` + `TtsService` | Fila de leitura por voz | `MediaSessionService` + bottom sheet de fila; controles na notificação |
| `MusicActivity` + `MusicService` | Player de podcast/áudio | `Media3` (ExoPlayer + MediaSession) + player Compose |
| `DownloadActivity` (2 tabs) / `DownloadFeedActivity` | Histórico e downloads em andamento | Tabs Compose + progresso via `WorkManager` |
| `SubscribeActivity` (4 tabs: search/podcast/browse/news) | Descobrir e assinar feeds | `SearchBar` + `SegmentedButton`; share-to-subscribe via intent filter |
| `ImportOpmlActivity` | Importar OPML | SAF (`OpenDocument`) + parser em coroutine |
| `ManageSourcesActivity` | Gerenciar assinaturas (editar, mover, excluir) | Tela Compose com multi-seleção + ações em lote |
| `SettingsActivity` (+ 4 fragments) | 112 prefs (Relatório 03) | Preferências Compose agrupadas com busca |
| `PremiumActivity` | Assinatura/restauração | Paywall Compose (Play Billing 7) |
| 4× `Widget*ConfigureActivity` | Configuração de widgets | Config em `Glance`/activity Compose |
| `LocaleEditActivity` | Plugin Tasker | Manter protocolo Locale (opcional) |
| `SendLogActivity` | Envio de log de suporte | Substituir por issue/crashlytics opt-in |
| `YoutubePlayerActivity`, `ExtendedWebView`, `ItemWebView` | Player in-app e WebViews | `WebView` interop + intent para app do YouTube |

## 2. Navegação (original → moderno)

- **Original**: múltiplas Activities + `MenuDrawer` (árvore de feeds com contadores de não lidos), back stack manual (`onBackPressed` + `Slidr` swipe-back), deep link `greader://feedly`, share (`ACTION_SEND`) → `SubscribeActivity`, atalhos (INSTALL_SHORTCUT), multiwindow Samsung/LG.
- **Moderno**: single-activity, **Navigation Compose** com rotas tipadas; **predictive back**; `NavigationSuiteScaffold` para drawer/rail; deep links (`greader://` → `https://` + App Links); `ShortcutManagerCompat`; edge-to-edge com insets; adaptativo por `WindowSizeClass`.

## 3. Home / lista de artigos

### 3.1 Toolbar (observado em `HomeActivity.onCreateOptionsMenu`)
- **Switch "somente não lidos"** (`switch_unread`) inline na toolbar
- **Botão sync** (clique = sincronizar; clique longo = sync offline/seleção) + **botão cancelar sync** visível durante o sync
- **Botão marcar como lido** (visível conforme modo de leitura de lista; long-click = "marcar até…")
- **Busca** (`MaterialSearchView` com histórico/filtros salvos: `search_filter`, `search_save`)
- Overflow: gerenciar fontes, marcar lidas até/scroll, sync otimizado, salvar páginas até, layout da lista (lista/cartão/grade — `menu_list_view`/`menu_card_view`/`menu_grid_view`), agrupar por feed, downloads, exportar OPML, preferências, rich list, premium, ajuda, sobre, privacidade, restaurar compra, PiP

### 3.2 Lista de itens (`ItemListFragment`)
- Linhas com **swipe esquerda/direita** configuráveis (LTR/RTL) e **botões de ação laterais** (`left_action`/`right_action`)
- **Rodapé** = carregar mais (`footer`)
- **Seleção múltipla** (long-press; `m(long[],long[],long[])`, `j(long[],long[])` — ações em lote)
- **"Mark scroll"**: marcar como lido ao rolar
- Contexto do item: info, copiar link, compartilhar link, abrir no navegador, marcar lido/não lido, estrela, keep unread, salvar página

### 3.3 FAB
- Posição: actionbar / esquerda / direita (`fab_position` 0/1/2); estilo simples/material (`fab_style`); opacidade 0–100; ação = marcar tudo lido (com confirmações `confirm_mark_as_read_all/sub`)

### 3.4 Catálogo de ações (values → significado)

**Swipe / botões de ação** (`item_list_action_*`, `swipe_action_*`):
`0` nenhum · `1` alternar lido · `2` alternar keep-unread · `3` alternar estrela · `4` alternar salvar (offline) · `5` abrir no navegador · `14` compartilhar · `6` Facebook · `7` Twitter · `10` Pocket · `11` Instapaper · `12` marcar lido
(swipe aceita subconjunto: 0–4)

**Duplo toque no leitor** (`item_double_tab_action_*`): `0` nenhum · `1` lido · `3` estrela · `4` salvar · `5` navegador · `14` compartilhar · `9` modo leitura (Readability) · `13` voltar à lista

**Teclas de volume** (lista): `0` nenhum · `1` rolagem · `2` rolagem + marcar lido; (leitor): `0` nenhum · `1` navegação · `2` rolagem
**Gestos de navegação (leitor)**: `0` desativado · `1` volume = navegação; **barra de navegação**: `0` off · `1` esquerda · `2` direita

**Enviar/compartilhar conteúdo** (`item_send_to_*`): `1` `{link}` · `2` `{title}+{link}` · `3` `{link}+{summary}` · `4` `{title}+{link}+{summary}` · `5` `{title}+{summary}+{link}`

## 4. Leitor de artigo (`ItemActivity`/`ItemFragment`)

- **3 modos**: conteúdo do feed / **reader cache** (conteúdo extraído, Readability) / página web — alternância no menu do item (`cache_view`/`web_view`/`feed_view`)
- **Barra de ações do artigo** (`R.id` observados): estrela, keep-unread, **voice reading (TTS)**, compartilhar, navegador, **modo leitura (readability)**, anterior/próximo, **zoom** (+/−/controles), **mídia** (podcast/vídeo), tela cheia, voltar
- **Paginação horizontal** (`WebViewPager`; desabilitável), **pinch zoom**, **hardware acceleration** configurável
- **Autoload**: link e modo leitura (`item_autoload_link`, `item_autoload_reading_mode`)
- **Navegador in-app** e **YouTube in-app** (WebView/Intent)
- **Tradução** via Google Translate (63 idiomas) e **mobilizers** (Google WebLight, Readability, Instapaper)
- PiP (`menu_pip`), controle de imagens (mostrar/ocultar/ajustar — `menu_image_fit`/`image_hide`)
- **Tipografia/leitura**: 5 temas, fonte (tamanhos 12–20 na lista; tamanhos no artigo), família de fonte (sans/serif/mono), itálico, densidade, `font_labels` etc.

## 5. TTS / Voice Reading

- `TtsActivity`: lista (fila) + estado vazio + banner premium; `TtsService` em foreground; broadcasts `INIT_LANGUAGE`, `START_INIT`, `INIT_ITEM`, `STATUS_CHANGED`, `STOP_SERVICE`
- Ações da fila: **acrescentar / tocar agora / tocar em seguida** (`array_tts_append/play_now/play_next`)
- Escopo: **página atual** ou **conteúdo completo** (`service_tts` 1/2)
- Notificação com controles (`notification_tts`), **marcar como lido ao terminar** (`service_tts_mark_read`), auto-detecção de idioma (MLKit), seleção de voz/idioma, **limite premium** (`limit_tts`)
- Entrada pelo artigo via `readaloud://` e botão na barra

## 6. Podcasts / áudio

- `MusicActivity`: título, tempo atual/duração, **seekbar**, play, avançar/retroceder, parar, fechar; serviço com notificação e **botões de headset** (`MEDIA_BUTTON`)
- Modelo: enclosures (áudio/vídeo), **capítulos** (ID3/Vorbis/Podlove), capa, overlay de mídia no artigo
- Offline: `offline_load_audio`/`offline_load_video` (**premium**), downloads com notificação de progresso
- Redesign: Media3 + **velocidade de reprodução, sleep timer, skip silence** (novos), fila e capítulos em Compose

## 7. Downloads

- `DownloadActivity`: 2 tabs — **histórico** e **em andamento**; aberta com `startDownloading`
- `DownloadFeedActivity`: seleção de feeds/itens para baixar
- Notificações de progresso (`download_media_v2`) + limpeza automática (Relatório 02)

## 8. Assinaturas / descoberta

- `SubscribeActivity`: 4 tabs — **Search** (com texto vindo de share), **Podcast**, **Browse** (1) e **News** (2) via `SubscribeBrowseFragment`; busca de feeds (Feedly search)
- **Share-to-subscribe**: recebe `ACTION_SEND` de qualquer app (o texto vira busca)
- OPML import/export, bundles de feeds, edição de assinatura (título, pasta, labels)

## 9. Widgets

- 4 tamanhos (**small/medium/large/icon**) + serviço de atualização + provider de dados
- Configuração por widget: fonte (feed/pasta), **limite de itens** (10/20/50/100), **cor de fundo**, **opacidade** (0/25/50/75/100), **cor do texto**, **tamanho do texto**, **auto-scroll** (0–300 s), gerenciar fontes
- Extras: badge **Tesla Unread**, canal de notificação, tema escuro/claro do widget
- Redesign: **Glance** com as mesmas opções + dynamic color + `RemoteViews` só se necessário

## 10. Temas e aparência

- Temas: **Default(0), Dark(1), Black/AMOLED(2), Sepia(3), Green(5)** + **auto dark** (`application_theme_auto_dark`, escolha dark vs black)
- Idioma do app (33) e da tradução (63); fullscreen; dual-pane landscape; ocultar lista de artigos; "usar UI de telefone" em tablet
- Fontes embarcadas (Lato/OpenSans), listas de fonte e tamanho
- Redesign: Material 3 **dynamic color** + presets equivalentes (sepia/green como esquemas custom), **AMOLED toggle**, tipografia de leitura separada da UI

## 11. Mapeamento de componentes (Compose/M3)

| Original | Moderno |
| --- | --- |
| MenuDrawer + feed tree | `NavigationDrawer`/`NavigationRail` + `NavigationSuiteScaffold`; árvore com contadores (`Badge`) |
| MaterialSearchView + SearchFilterDialog | `SearchBar`/`DockedSearchBar` + chips de filtros salvos (`FilterChip`) |
| Swipe + botões laterais | `SwipeToDismissBox` + `ListItem` com ações; `ContextualFlowRow` de ações |
| Seleção múltipla + toolbar contextual | `SelectionContainer` de app: `TopAppBar` contextual + `MultiChoiceSegmentedButtonRow` |
| FAB (posição/estilo/opacidade) | `FloatingActionButton`/`LargeFloatingActionButton` (posição fixa M3; opacidade não recomendada) |
| Reader WebViewPager | `HorizontalPager` + `WebView` interop; barra superior com `SegmentedButton` (Feed/Reader/Web) |
| Item bar (ações) | `BottomAppBar` + `ModalBottomSheet` para ações secundárias |
| TTS Activity | `MediaSessionService` + fila em `ModalBottomSheet`; botão "ouvir" no leitor |
| MusicActivity | Tela "Now Playing" Compose + mini-player persistente |
| Download tabs | `TabRow` + `LinearProgressIndicator` + WorkManager `Progress` |
| Subscribe tabs | `SegmentedButton` (Search/Podcasts/Browse/News) + `SearchBar` |
| Widgets | **Glance** (4 tamanhos + ícone) com config persistida em DataStore |
| Preferences (112) | Telas Compose agrupadas + busca; `DataStore` (migração 1:1 das chaves) |
| Temas | `MaterialTheme` + dynamic color + presets sepia/green/AMOLED; dark automático por sistema |
| Gestos/volume | Manter catálogo (Relatório 04 §3.4); mapear para `key events` e `pointerInput` |
| Swipe-back (`Slidr`) | Predictive back nativo |
| Multiwindow Samsung/LG | `WindowSizeClass` + `ListDetailPaneScaffold` |

## 12. Oportunidades de melhoria (além da paridade)

- **Leitura**: barra de progresso de leitura, marcações/destaques, fontes variáveis, modo foco
- **Podcasts**: velocidade (1x–3x), sleep timer, skip silence, fila arrastável, capítulos clicáveis
- **TTS**: MediaSession com velocidade, destaque da frase sendo lida, sleep timer
- **Lista**: agrupamento por data/feed, regras de filtro (além dos filtros salvos), undo de ações (Snackbar)
- **Sync**: indicador de estado por feed, sync em segundo plano com notificação opcional (WorkManager)
- **Widgets**: Glance com tema dinâmico, múltiplas fontes por widget, rolagem suave nativa
- **Acessibilidade**: content descriptions (o original tem parciais), contraste AA/AAA, TalkBack, tamanhos de toque
- **Adaptive**: foldables/tablets com `ListDetailPaneScaffold`, desktop ChromeOS

## 13. Evidências

- `analysis/prefs/resources.txt` (arrays de ações/temas/tamanhos decodificados)
- `analysis/sync/ItemFragment.onCreateView.java` (barra do leitor; R.id.* observados)
- Métodos salvos nesta rodada: `HomeActivity.onCreateOptionsMenu`, `HomeBaseActivity.onOptionsItemSelected`, `TtsActivity.onCreate/onOptionsItemSelected`, `ItemListFragment.onClick`, `MusicActivity.onCreate`, `SubscribeActivity.onCreate`, `DownloadActivity.onCreate`
- Bundle REA: `analysis/07-evidence-bundle.json`
