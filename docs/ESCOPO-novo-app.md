# Escopo do novo app — gReader Neo

> Baseado nos Relatórios 01–04. Alvo: **Android 16+ (API 36)**, leve, Kotlin + Compose + Material 3.
> **Decisões aplicadas**: ❌ TTS/leitura por voz · ❌ Podcasts/áudio · ✅ Sync somente com Feedly.

## 1. Conta e autenticação

- **Login apenas com Feedly** via **OAuth2 próprio** (Authorization Code + PKCE, Custom Tabs) — app registrado pelo dono do novo app; nunca reutilizar `client_id`/`secret` do original
- Armazenamento seguro de tokens (**EncryptedSharedPreferences/Keystore**), refresh automático
- Tela de conta: status, sincronizar agora, sair (com confirmação), trocar de conta
- Sem Inoreader, The Old Reader ou modo RSS local

## 2. Sincronização (Feedly v3)

- **Motor offline-first com outbox** (Room `sync_operations`): lido/não lido, keep-unread, estrelado (saved), labels (tags), assinaturas, pastas (categories), marcar tudo lido (markers com `asOf`)
- **Pull**: subscriptions, categories, tags, streams de conteúdo (`continuation`, `newerThan`, `count ≤ 1000`), ids do servidor para reconciliar leitura, stream de estrelados
- **Agendamento**: WorkManager periódico com intervalos configuráveis (custom, 1/2/4/6/12/24 h) + restrição opcional "somente Wi-Fi"; sync manual; sync ao abrir (configurável)
- **Sync parcial**: por feed (uma assinatura) e por pasta
- **Reconciliação de leitura** com as regras do original: `keep_unread` protege do mark-all; `read_time < sync_time` evita loop de ressurreição de não lidos
- **Contadores de não lidos** desnormalizados por feed/pasta (queries em massa no Room) + **badge do ícone**
- **Notificações de novos artigos por feed**: liga/desliga por feed, som/LED/vibração configuráveis; sem notificação = sem ruído

## 3. Organização e assinaturas

- Árvore **pastas (categories) + feeds** com contadores de não lidos, ocultar/mostrar feeds
- **Gerenciar fontes**: editar título, mover de pasta, desassinar, excluir do sync (por feed)
- **Assinar**: busca de feeds via Feedly (search API) + **share-to-subscribe** (ACTION_SEND de qualquer app)
- **OPML**: importar (assina no Feedly) e exportar
- **Labels/tags por artigo**: adicionar/remover, navegar por label
- **Marcar tudo como lido**: feed, pasta ou tudo (com confirmações configuráveis)
- **Estrelados** (reading list do Feedly) e **keep unread**
- **Busca local** com filtros salvos + busca em título/conteúdo
- (Extra) agrupamento por feed e opção de lista densa

## 4. Leitor de artigos

- **3 modos**: conteúdo do feed · **modo leitura** (extração Readability4J) · página web original — alternância por segmented control
- **Barra de ações**: estrela, keep-unread, compartilhar, abrir no navegador, modo leitura, anterior/próximo, zoom, tela cheia
- **Compartilhar** com formatos configuráveis (`{title}`, `{link}`, `{summary}` — 5 combinações) via Sharesheet do Android
- **Copiar link**, abrir links externos, player do YouTube via intent (sem player interno legado)
- **Desembrulhar redirects** (news.google.com / google.com/url?url=…)
- **Tipografia de leitura**: tamanho de fonte, família (sans/serif/mono), itálico, margens/coluna
- **Imagens**: mostrar/ocultar/ajustar à largura; GIF animado suportado
- **Autoload**: link e modo leitura (configuráveis)
- **Tradução** (Google Translate, 63 idiomas) — opcional nas configurações *(confirmar)*
- **Gestos**: duplo toque configurável (lido/estrela/salvar/navegador/share/readability/voltar), teclas de volume (navegação/rolagem), pinch zoom
- **Paginação**: rolagem vertical padrão; horizontal opcional *(recomendado cortar)*
- ❌ Sem TTS/readaloud · ❌ Sem player de podcast/overlay de mídia

## 5. Offline e cache

- **Salvar artigo para offline** (conteúdo completo + imagens) com fila em WorkManager
- **Auto-offline**: baixar novos artigos conforme política (limite por feed: 10/20/50/100/200, todos sincronizados, todos não lidos)
- **Salvar ao estrelar** (opcional)
- **Limpeza automática**: lidos após N dias (0–28 ou nunca), não lidos após N dias (1–28 ou nunca), imagens em cache (N itens), preservar estrelados/salvos
- **Cache em armazenamento interno** (scoped storage) — sem `/gReader` na raiz externa
- Exportar/importar dados via SAF (backup)

## 6. Widgets *(confirmar — recomendado manter 3 tamanhos)*

- Tamanhos: **small, medium, large** (Glance) — cortar o widget "ícone" legado
- Configuração: fonte (feed/pasta), limite de itens (10/20/50/100), cor de fundo, opacidade (0–100%), cor/tamanho do texto, auto-scroll (0–300 s)
- Atualização via WorkManager/`updatePeriodMillis` + tema claro/escuro automático

## 7. Compartilhamento e integrações

- **Share-to-subscribe** e **compartilhar artigo** (nativo)
- **Descoberta de feeds** (busca Feedly) + sugestões na tela de assinatura
- ❌ Pocket/Instapaper/Facebook/Twitter/EverClip (substituídos pelo Sharesheet) *(recomendado)*
- ❌ Tasker/Locale e DashClock *(recomendado cortar — legado)*
- ❌ Tesla Unread (usar badge padrão do Android)

## 8. Aparência, adaptação e acessibilidade

- **Material 3 + dynamic color**; tema claro/escuro/sistema + **AMOLED black**; presets Sepia/Green *(confirmar se mantém)*
- **Adaptativo**: telefone, tablet e dobráveis (`ListDetailPaneScaffold`, 2 painéis) — sem flags legadas Samsung/LG
- **Edge-to-edge**, predictive back, ícones temáticos
- Idioma do app por usuário (per-app language), pt-BR incluído
- **Acessibilidade**: TalkBack, contraste AA/AAA, áreas de toque ≥48 dp, fontes escaláveis
- ❌ Sem multiwindow Samsung/LG, GlowPad, Slidr (bibliotecas legadas)

## 9. Dados e backup

- **Room** com o schema do Relatório 03 (adaptado: sem colunas de áudio/vídeo; `tag.type` como enum; outbox `sync_operations`)
- **DataStore** para preferências (migração 1:1 das chaves relevantes do Relatório 03)
- Exportar/importar: OPML (feeds) + backup de preferências/banco via SAF
- ❌ Analytics/Firebase/Crashlytics/AdMob/GDPR-dialog *(recomendado cortar — app leve e privado)*
- ❌ Premium/billing (com TTS/podcasts fora, perde o objeto) *(confirmar)*

## 10. Arquitetura técnica

| Camada | Escolha |
| --- | --- |
| Linguagem/UI | Kotlin 2.x + **Jetpack Compose** + Material 3 |
| Navegação | Navigation Compose (single-activity), deep links `greader://` + App Links |
| Dados | **Room** (schema §9) + **Paging 3** + Flow |
| Preferências | **DataStore (Preferences)** |
| Rede | OkHttp + Retrofit + **kotlinx.serialization** (Feedly v3) |
| Imagens | **Coil 3** (cache em disco interno) |
| Extração de conteúdo | **Readability4J** (+ Jsoup) |
| Background | **WorkManager** (sync, offline, limpeza) |
| DI | Hilt (ou Koin) |
| Widgets | **Glance** |
| Build | AGP recente, `minSdk 26` *(confirmar)*, `targetSdk 36`, version catalogs, baseline profiles |

## 11. Fora do escopo (explícito)

1. TTS / Voice Reading / MLKit language-id / premium de voz
2. Podcasts: player, capítulos, downloads de áudio/vídeo, aba Podcast, overlay de mídia
3. Backends Inoreader, The Old Reader e modo RSS local
4. ClientLogin (e-mail/senha) — apenas OAuth2 Feedly
5. Ads, billing/premium, analytics, Remote Config, In-App Messaging
6. Mobilizers legados (Google WebLight, Instapaper) *(recomendado)*
7. Armazenamento externo legado (`/gReader`), multiwindow Samsung/LG, DashClock, Tasker, Tesla Unread

## 12. Decisões em aberto (confirmar)

| # | Decisão | Recomendação |
| --- | --- | --- |
| 1 | Widgets: manter 3 tamanhos? | Manter (small/medium/large) |
| 2 | Tradução de artigos: manter? | Manter como opção |
| 3 | Temas Sepia/Green (modo leitura): manter? | Manter como presets de leitura |
| 4 | Sem ads/premium/analytics? | Sim, remover tudo |
| 5 | Tasker/Locale: cortar? | Cortar |
| 6 | `minSdk`: 26 (Android 8) ok? | 26 |
