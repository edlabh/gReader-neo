# gReader Neo

Leitor de RSS/feeds moderno para **Android 13+**, reconstruído a partir da engenharia reversa do gReader 5.2.2 (pasta `analysis/`). Kotlin + Jetpack Compose + Material 3, offline-first, **sincronização somente com Feedly**.

## Sobre o app

| Campo | Valor |
| --- | --- |
| Package | `com.greader.neo` |
| minSdk / targetSdk / compileSdk | 33 (Android 13) / 36 (Android 16) / 37.0 |
| Stack | Kotlin 2.4.21 · Compose BOM 2026.09 · Material 3 · Room · DataStore · WorkManager · OkHttp/Retrofit + kotlinx.serialization · Coil · Readability4J |
| Escopo completo | [`docs/ESCOPO-novo-app.md`](docs/ESCOPO-novo-app.md) |

### Status — Milestone 1 (esqueleto executável)

- [x] Projeto Gradle (AGP 9.4.1, Gradle 9.6, Kotlin 2.4.21, KSP, Compose)
- [x] Tema Material 3 com **paleta própria** (indigo), dynamic color opcional, AMOLED, edge-to-edge, single-activity
- [x] Navegação: **Home (todos os artigos + drawer de feeds recolhível)** · Leitor · Configurações · Login · Assinaturas
- [x] UI redesenhada (prompt em `docs/prompt-redesign-visual.md`; skills AAS: anti-ui-slop, android-jetpack-compose-expert, mobile-design)
- [x] Camada de dados: Room (`feeds`, `tags`, `tag2sub`, `articles`, `tag2article`, `sync_operations`/outbox)
- [x] Preferências DataStore (chaves espelhando o app original p/ migração 1:1)
- [x] Cliente Feedly v3 (subscriptions, tags, streams, ids, markers, busca, OAuth2)
- [x] **OAuth2 Feedly** (Authorization Code + Custom Tabs + deep link `greader://feedly`, tokens no Keystore); conta acessível pelo drawer (email, sair/entrar)
- [x] **Motor de sync**: replay da outbox → markers · pull de assinaturas/pastas · pull de artigos (continuation/newerThan) · reconciliação de leitura (`streams/ids`) · contadores
- [x] **Agendamento flexível**: seletor único com **Atualização Manual** (sem background) e frequências de 5 min a 24 h (1 h padrão)
- [x] **Idiomas**: Português (BR) · Inglês (padrão) · Espanhol — segue o idioma do sistema (inclui mensagens de sync/erros/acessibilidade)
- [x] **Lista de artigos**: por feed/pasta/todos, busca local, seleção múltipla, swipe (lido), marcar tudo lido, **"Mostrar artigos lidos (N)"** no rodapé/estado vazio quando o filtro de não lidos esconde os artigos já lidos
- [x] **Personalização da lista**: densidade, tamanho da fonte, linha entre artigos, **modos Lista/Grade**, **ordenação (recentes/antigos)**, **não lidos em negrito / lidos discretos** — as escolhas ficam **salvas automaticamente** e valem para as próximas aberturas (aplicadas em tempo real)
- [x] **Armazenamento**: retenção configurável (1/3/7/14/30 dias ou nunca; **padrão 7 dias**; estrelados preservados) com limpeza automática pós-sync
- [x] **Leitor 3 modos**: Feed · Leitura (Readability4J) · Web, com estrela, compartilhar, **manter não lido** e **navegador embutido/externo configurável (padrão: embutido, com histórico de navegação no botão voltar)** — barra de modos **compacta e retrátil** (esconde ao rolar para baixo; volta ao rolar para cima ou tocar na alça)
- [x] **Assinaturas**: busca de feeds no Feedly, **prévia dos últimos artigos antes de assinar (com escolha de pasta; sem pasta por padrão)**, OPML import/export (SAF), share-to-subscribe, **gerenciar assinaturas** (renomear, mover de pasta, remover — sincronizado com a conta Feedly)
- [x] **Notificações** de novos artigos (canal + permissão + pós-sync)
- [x] **Widgets** (Glance): lista de feeds com não lidos + **contador pequeno (1×1) estilo ícone com badge**, atualizados pós-sync e em tempo real nas leituras
- [x] **Avaliação total de usabilidade + visual** (prompt em `docs/prompt-avaliacao-total.md`, relatório em `docs/AVALIACAO-total.md`): rodadas 1 (P1), 2 (P2) e 3 (P3)
- [x] **Desfazer (undo)**: swipe de leitura, remoção de feed (reassina no Feedly) e "marcar tudo como lido" (desfaz exatamente os artigos afetados)
- [x] **Manter não lido** no leitor (flag local que sobrevive a sincronizações e "marcar tudo lido")
- [x] **Animações de transição** entre telas (fade + slide sutil)
- [x] **Modo Lista com miniaturas opcionais** (Aparência; padrão desligado)
- [x] **Cache offline**: conteúdo do modo Leitura guardado no banco (migração v1→v2) + pré-cache de imagens em Wi-Fi (Armazenamento; 60 imagens/sync)
- [x] **Tablet (2 painéis)**: telas ≥ 840dp com drawer fixo e leitor em painel lateral
- [x] **Testes**: JVM (DTOs Feedly, codec dos filtros) + smoke test de UI Compose da Home
- [ ] Futuro: widgets adicionais, opção "sincronizar somente em Wi-Fi", backup/restauração de preferências

### Login no Feedly

O app **já funciona de imediato**: usa como padrão as credenciais OAuth legadas do gReader
(extraídas do APK original na análise — client `greader`, redirect `greader://feedly`).

**Credenciais próprias pelo app**: na tela de login, toque em
**"Usar credenciais próprias (avançado)"** e informe o `Client ID` e o `Client Secret` do
seu app OAuth registrado em developers.feedly.com (redirect URI `greader://feedly`).
As credenciais ficam criptografadas no Keystore, valem imediatamente e podem ser
restauradas para o padrão a qualquer momento.

Para builds automatizados, também é possível fixar credenciais em `local.properties`
(sobrescreve o padrão embutido):

```properties
feedly.clientId=SEU_CLIENT_ID
feedly.clientSecret=SEU_CLIENT_SECRET
```

O fluxo é Authorization Code via Custom Tabs; o retorno chega pelo deep link
`greader://feedly` e os tokens ficam criptografados no Keystore.

### Compilar

```sh
./gradlew :app:assembleDebug     # APK debug (app/build/outputs/apk/debug/)
./gradlew :app:assembleRelease   # APK release com R8 (app/build/outputs/apk/release/, ~4,3 MB)
./gradlew :app:installDebug      # instalar em dispositivo/emulador conectado
./gradlew :app:testDebugUnitTest # testes JVM (DTOs do Feedly)
./gradlew :app:connectedDebugAndroidTest  # smoke test de UI (requer device/emulador)
```

Requisitos: JDK 17+ e Android SDK com platform 37.0 (compileSdk 37; já configurado em `local.properties`).

A release é **assinada** com a keystore local configurada em `local.properties`
(`release.storeFile`, `release.storePassword`, `release.keyAlias`, `release.keyPassword`).
A keystore fica em `~/.local/share/greader-neo/release.jks` (**fora do repositório — faça backup!**).
Sem essas propriedades, o build gera um APK sem assinatura (`app-release-unsigned.apk`).

### Estrutura do app

```
app/src/main/java/com/greader/neo/
├── NeoApp.kt                     # Application + container (DI manual)
├── MainActivity.kt               # single-activity + edge-to-edge
├── ui/
│   ├── theme/                    # Material 3 (dynamic color + AMOLED)
│   ├── navigation/               # Navigation Compose (rotas tipadas)
│   ├── home/                     # lista de feeds (Room + Flow)
│   ├── settings/                 # preferências (DataStore)
│   ├── login/                    # placeholder OAuth2 Feedly
│   └── reader/                   # placeholder do leitor
└── data/
    ├── db/                       # Room: entidades, DAOs, banco (schema do Relatório 03)
    ├── prefs/                    # DataStore (chaves 1:1 com o app original)
    ├── feedly/                   # DTOs + Retrofit (Feedly v3, Relatório 02)
    └── sync/                     # SyncWorker + agendamento (outbox p/ fase 2)
```

## Engenharia reversa (base do projeto)

Análise estática do APK original com [REA](https://github.com/morluto/rea) + JADX (o APK nunca foi executado).

**Relatórios** (em `analysis/`):

| # | Relatório | Conteúdo |
| --- | --- | --- |
| 01 | `RELATORIO-01-arquitetura-e-features.md` | Arquitetura, features, stack original, mapeamento de modernização |
| 02 | `RELATORIO-02-sincronizacao.md` | Outbox (`history`), engine, Feedly v3 (endpoints/payloads), GData, agendamento |
| 03 | `RELATORIO-03-dados-e-preferencias.md` | Schema final do banco + 112 preferências inventariadas |
| 04 | `RELATORIO-04-ui-ux-e-fluxos.md` | Telas, gestos, catálogos de ações e mapeamento Compose/M3 |

**Alvo analisado**: `com.noinnion.android.greader.reader` 5.2.2-424 · SHA-256 `20f587379f303da151d2394868d9e8678f8d26f59c285383c8eb0214a3a6e2a9` · 10.160 classes.

### Ambiente REA (reanálise)

```sh
source tools/env.sh          # exporta REA_JADX_MCP_JAR e GREADER_APK
rea inspect-android-package "$GREADER_APK" --json
rea search-android-classes "$GREADER_APK" LoginActivity --json
```

O servidor MCP `rea` (~139 ferramentas) está registrado no OpenCode com `REA_JADX_MCP_JAR` apontando para `tools/jadx-headless-mcp-0.7.1-all.jar` (SHA-256 conferido). Limites do provider: análise estática, 120 s/operação, 90 s de decompilação, 1 MiB de texto.

## Estrutura do repositório

```
.
├── app/                          # módulo Android (gReader Neo)
├── gradle/libs.versions.toml     # catálogo de versões
├── docs/ESCOPO-novo-app.md       # especificação de recursos do app
├── analysis/                     # engenharia reversa (relatórios + evidências)
│   ├── RELATORIO-01..04*.md
│   ├── prefs/                    # inventário de preferências (aapt2 + scripts)
│   ├── sync/                     # métodos decompilados (engine, clientes Feedly/GData)
│   ├── ui/                       # métodos de UI (Home, TTS, player, subscribe)
│   └── assets/ · dex/ · strings/ # material bruto da análise
├── tools/
│   ├── env.sh                    # variáveis do REA
│   └── jadx-headless-mcp-*.jar   # engine JADX (não versionado)
└── com.noinnion....apk           # APK original analisado
```

## Notas

- Credenciais do app original (Feedly `client_id`/`secret`, Inoreader AppId/AppKey) **não são reutilizadas** — o novo app terá OAuth2 próprio.
- Fora do escopo: TTS, podcasts, Inoreader/Old Reader/RSS local, ads/premium/analytics (ver `docs/ESCOPO-novo-app.md`).
- Backup da config do OpenCode feito pelo setup do REA: `~/.config/opencode/opencode.jsonc.rea.backup`.
