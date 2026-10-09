# Relatório 02 — Sincronização do gReader: fluxo completo

> Continuação do Relatório 01. Mesma base: análise estática do APK 5.2.2-424 com REA/JADX, sem execução. Métodos salvos em `analysis/sync/` (`.java`) e evidências no bundle `analysis/07-evidence-bundle.json`.

## 1. Resumo

O sync do gReader é **offline-first com outbox**: mutações locais (ler, estrelar, labels, assinaturas) entram numa fila SQLite (`history`), são reaplicadas ao servidor no início de cada sync e apagadas após sucesso. Em seguida vem o pull: assinaturas/pastas, novos artigos (streams com continuation), estado de leitura do servidor e estrelados. Tudo é orquestrado pelo engine `ep6` (ofuscado), através de um contrato abstrato de cliente (`rn6`) com 4 implementações: **Feedly** (`sn6`, serviço 5), **The Old Reader** (`xn6`→base `un6`, serviço 6), **RSS local** (`RssReaderClient`, serviço 7), **Inoreader** (`wn6`→base `un6`, serviço 8). O agendamento usa `AlarmManager` alinhado ao último sync; o serviço manual é `SyncService` (foreground).

## 2. Componentes e papéis

| Componente | Papel |
| --- | --- |
| `service.SyncService` | Entrada do sync (IntentService + foreground). Decide escopo: sub específica (`subId`/`subUid`), tag (`tagUid`) ou **full sync** (`manual_sync` ou `sync_auto_update`). Emite broadcasts e dispara offline automático |
| `ep6` (engine) | Orquestrador: fila, pull, upserts, contadores, limpeza, notificações |
| `rn6` (contrato) | Interface de cliente: `handleReaderList`, `handleItemList`, `handleItemIdList`, `markAsRead/Unread`, `editSubscription`, `editItemTag`, `renameTag`, `disableTag`, `markAllAsRead`, `setRequestStop`; constantes `LOGIN_*`, `STATE_*`, `ACTION_*`; factory `getClient(Context)` |
| `sn6` = Feedly | OAuth token refresh; endpoints v3; parsers JSON próprios |
| `un6` (base) + `xn6`="the old reader", `wn6`="inoreader" | Cliente GData comum; auth por **e-mail/senha** (`cn6.b0` + pref `google_passwd`); OkHttp 30s (`n56.M(30000)`) |
| `RssReaderClient` | Serviço local: `loadDataSet/saveDataSet`, parse RSS/Atom com `syndication.*` (base AntennaPod) |
| `yo6` + `ScheduleReceiver` | Agendador: PendingIntent (req 10001) → `ACTION_SYNC`; `AlarmManager` cancel/reschedule; `f()` = sync manual imediato |
| `gx6` | Wrapper de `AlarmManager` (provável `setAndAllowWhileIdle`/`setExact` por API — inferido; corpo não lido) |
| `iw6` | Preferências (chaves literais), ex.: intervalo, limite, toggles |
| `cn6` | Sessão/login: `c0` = `login_method` (5/6/7/8), `b0` = usuário, `f0/g0/h0/i0` = gravar credenciais/token |
| `dp6`/`fp6` | Callbacks de `handleItemIdList` (ids) e `handleItemList` (itens) |
| Modelos: `ap6` item, `ip6` subscription, `lp6` tag/pasta, `kp6` tag2sub, `jp6` tag2item, `zo6` history | ContentValues/Cursor + URIs e projeções de coluna |
| `vd` | Cursor wrapper (query tipada) usado no lugar de CursorLoader direto |
| `yd` | LocalBroadcast helper (ações internas) |

## 3. Outbox: a tabela `history`

```sql
CREATE TABLE IF NOT EXISTS history (
  _id INTEGER PRIMARY KEY, type INTEGER NOT NULL, params TEXT NOT NULL,
  sync_time INTEGER NOT NULL DEFAULT 0, done INTEGER NOT NULL DEFAULT 0)
```

Replay em `ep6.I` (query `history.done = 0 AND history.type != 4`, ordem `sync_time DESC`) → chamadas ao cliente → `ep6.f(_id)` **deleta a linha** (em transação via URIs do provider). Formato `params` (string compacta com prefixo de 2 chars, pares `chave=valor` separados por `&`):

| type | Ação | Formato / chamada |
| --- | --- | --- |
| 1, 2, 3 | Marcar tudo como lido (escopo sub/tag/global) | `..<subUid>&t=<tagUid>` → `markAllAsRead(subUid, tagUid, syncTime)`; type 1 interrompe o loop após processar |
| 4 | **Nunca reaplicado** pelo outbox (tratado no fluxo de reads; ver §5.4) | — |
| 5 | Editar assinatura | `..<subUid>&t=<tag>&a=<labels…>` → `editSubscription(..., 4)`; `&r=<labels…>` → ação 5; sem labels → ação 1 (cancelar/desassinar) |
| 6 | Renomear tag | `..<uid>&t=<uid>&dest=<novo>` → `renameTag` |
| 7 | Desabilitar/remover tag | `..<uid>&t=<uid>` → `disableTag` |
| 8 | Labels de item | `s=<subUid>&i=<itemUid>&a=<add…>&r=<remove…>` → `editItemTag([item],[sub],labels, 1=add / 2=remove)` |

Além do outbox textual, pendências de label por item na `tag2item` (`action <> 0`) são aplicadas item a item via `ep6.V()` → `editItemTag`; sucesso em add → `action=0 + sync_time`; remove → apaga a linha. O engine usa as URIs transacionais do ContentProvider (`h`=begin, `i`=commit, `j`=rollback).

## 4. Fluxo do full sync (`ep6.I`)

1. **Replay do outbox** (§3)
2. **`S(j)` — pull de assinaturas/pastas**: `handleReaderList` → grava subs/tags com `sync_time=j`; depois apaga o que não foi tocado (`sync_time <> j`), exceto tags `type=9` (fixas). Pastas `type=10` que contêm subs viram `type=11`. Broadcast `REFRESH_SUB_LIST`
3. **Pendências de label de item**: query `item.uid = tag2item.item_uid AND tag2item.action <> 0` → `V()` por item
4. **`M()` — fixup de leituras**: itens `sync_time < read_time AND read=1` em lotes de 250 (corrige estados de leitura locais não propagados)
5. **Preparação de notificações**: se `sync_notifiable_custom`, monta `SparseIntArray` de feeds com `notification=1` (`cw6.b`)
6. **Pull de artigos**: `handleItemList(new fp6(...), j)` → nos clientes, `GET …/streams/<stream>/contents` com `newerThan`, `count ≤ 1000`, paginando por `continuation`; itens chegam em lotes de 200 → `ep6.L()` (upsert; para RSS (`n()==7`) usa modo 2 com `sync_time=j`; desembrulha links `news.google.com`/`google.com/url?…url=`) → `update(ReaderProvider.n)` recalcula
7. **Leitura do servidor** (`sync_server_reads`, default **true**): `handleItemIdList(dp6)` → `GET …/streams/<stream>/ids` (`unreadOnly`) → callback recebe IDs e o provider aplica em massa (`ReaderProvider.p` com `ids='…'`), depois `Y()` (recontagem) e broadcast de dados
8. **Estrelados** (`sync_include_starred`): sync do stream `STATE_STARRED` (`W(...)`) somando ao contador
9. Retorna `cw6 {a: nº de itens novos, b: contagem por feed}`

Pós-processamento no `SyncService`: `c()` (finalização), `iw6.U("last_sync_time")` se houve novidade, `d(cw6)` (notificações de novos artigos), `yo6.c()` (reagenda alarme), broadcast `SYNC_FINISHED`, e **offline automático** via `n56.s0` quando `sync_offline`/`offline_usage` (usando `offline_feed_items_limit`, default 50).

Syncs parciais: `T(sub, j, false)` (uma assinatura) e `W(tagUid, j, false, itemLimit)` (uma pasta), disparados por intents com `subId`/`subUid`/`tagUid`.

## 5. Agendamento e disparadores

- **Manual**: `yo6.f(ctx, syncOffline)` → `startForegroundService(SyncService)` com `manual_sync=true` (ignora `sync_auto_update`)
- **Periódico**: `yo6.d(ctx)` (re)agenda PendingIntent `ScheduleReceiver` (requestCode 10001):
  - `iy6.H(ctx)`: se sync base desligado → 0; senão `sync_interval_hours` (×60 min) ou `sync_custom_interval` (default 30 min)
  - Alinhamento à grade do `last_sync_time` (`iw6.z`): próximo disparo = `((now − lastSync)/intervalo + 1) × intervalo + lastSync`
  - `yo6.b` cancela; `yo6.c` = `d` (chamado ao fim de cada sync)
- **Boot/upgrade**: `OnBootCompletedReceiver`, `OnUpgradeReceiver` reagendam
- `ScheduleReceiver.onReceive`: `ACTION_SYNC` → `yo6.f(ctx,false)`; `ACTION_CLEAN` → `CleanService`

## 6. Erros, cancelamento e eventos

- `setRequestStop(false)` no início; durante o parse/pull, `mRequestStop → gw6.a` (cancelado)
- `gw6.b` = falha de login → broadcast `…action.STOP_SYNC_ERROR_LOGIN` + toast
- `IOException` → `SyncService.b(e)`; outros → `STOP_SYNC_ERROR`
- Eventos internos: `SYNC_FINISHED`, `REFRESH_SUB_LIST`, `WIDGET_UPDATE`, `INIT_TTS` (separado)
- `requestStop(true)` é chamado, por ex., ao deslogar/trocar de conta (inferido)

## 7. Feedly (cliente `sn6`, serviço 5)

### 7.1 Autenticação (observado)
- Refresh: `POST https://feedly.com/v3/auth/token` (form) com `refresh_token`, `client_id=greader`, `client_secret=…`, `grant_type=refresh_token`; resposta `access_token`
- Access token cacheado na pref `google_auth` por ~899 s (`google_auth_time`); refresh token em `oauth2_token_refresh`
- Header: `Authorization: OAuth <access_token>` (não "Bearer")
- **Atenção**: client id/secret embutidos são do desenvolvedor original — **não reutilizar** no rebuild; registrar app OAuth próprio junto ao Feedly

### 7.2 Endpoints e payloads
| Operação | Chamada |
| --- | --- |
| Perfil (userId) | `GET /v3/profile` |
| Assinaturas | `GET /v3/subscriptions` (id, title, sortid, website, visualUrl, updated, categories[{id,label}]) |
| Tags/pastas | `GET /v3/tags` |
| Artigos | `GET /v3/streams/<streamId>/contents?ranked=&unreadOnly=&continuation=&newerThan=<ms>&count≤1000` |
| IDs (reads) | `GET /v3/streams/<streamId>/ids?ranked=&unreadOnly=&count` → `{ids:[…]}` |
| Markers | `POST /v3/markers` |
| Marcar lido | `{"action":"markAsRead","type":"entries","entryIds":[…]}` |
| Manter não lido | `{"action":"keepUnread","type":"entries","entryIds":[…]}` |
| Marcar tudo lido (pasta/stream) | `{"action":"markAsRead","type":"categories","asOf":<ms+999>,"categoryIds":[…]}` |
| Desmarcar lido | `markAsUnread` (variante; método `l/d/e` no cliente) |

### 7.3 Stream IDs
`f(String)`: `user/-/category/global.all` → `user/<userId>/category/global.all`; `STATE_STARRED` → `user/<userId>/tag/global.saved`; `global.read` idem; senão passthrough (`feed/http://…`, `user/<id>/category/<x>`, `user/<id>/tag/<label>`).

### 7.4 Mapeamento de entry → item (`sn6.n` → `ap6`)
| JSON Feedly | Campo local |
| --- | --- |
| `id` | `uid` |
| `title` | `title` (entities decodificadas) |
| `unread` (bool) | `read = !unread` |
| `categories/tags[].id` | `/global.saved` → `starred`; `user/…/tag/<x>` → lista de labels |
| `published`, `crawled` | `published_time`, `updated_time` (ms→s) |
| `alternate[].href` | `link` |
| `enclosure[].href/type` | mídia (áudio/vídeo/imagem; `image/*`→`image/thumbnail`) |
| `summary` / `content.content` | `content` |
| `visual.url` | `image` (via `cp6`) |
| `author` | `author` |
| `origin` | stream de origem → associação à subscription |

## 8. The Old Reader e Inoreader (`un6`)

- Base comum `un6` (34 métodos): API estilo GData (`/reader/api/0/...` — inferido pelo modelo), auth por **e-mail + senha** (`google_passwd`) com `cn6.b0` (usuário) — fluxo legado tipo ClientLogin/app-password
- `xn6` = "the old reader" (svc 6); `wn6` = "inoreader" (svc 8, com contadores extras `d()/e()` e helpers `k()`)
- Ids de estado: `rn6.STATE_READ`, `STATE_STARRED`; hunks de `un6` não foram decompilados nesta rodada

## 9. RSS local (serviço 7)

- Sem servidor: `RssReaderClient` mantém `DataSet`/`DataStarred` (persistidos via `saveDataSet/saveStarred`) e parseia feeds com o parser `syndication.*` (RSS 2.0, Atom, Media RSS, iTunes, capítulos)
- `handleReaderList/handleItemList/handleItemIdList` são supridos pelo próprio cliente (fetch HTTP + parse)
- `sync_server_reads` usa os ids locais como “servidor”; `ep6.L` modo 2 garante `sync_time=j` para todos os itens baixados; limpeza (`G`) não apaga itens mais novos que o sync

## 10. Pós-sync local

- **`Y()`**: recalcula contadores de não lidos (provider `o`), grava pref `unread_count` e atualiza **badge do ícone** (`n56.N1` com o componente da LoginActivity)
- **`G(j)` (auto-clean)**: apaga itens antigos conforme `cache_auto_clean_read` (default 1) e `cache_auto_clean_unread` (default -1 = nunca), respeitando `cache_keep_starred` (default true) e `cache_keep_cached` (default false); para RSS, preserva itens com `sync_time >= j`
- **Notificações de novos artigos**: `d(cw6)` no SyncService usa `cw6.b` (contagem por feed; feeds com `notification=1` e pref `sync_notifiable_custom`) + opções LED/som/vibração (`sync_notify_*`)
- **Offline automático**: `n56.s0(ctx, null, ts)` — baixa artigos para offline se `sync_offline`/`offline_usage` (limite `offline_feed_items_limit`, default 50)
- `ep6.d()` esvazia o que sobrar na `history` após sync completo

## 11. Preferências de sync (observado nas strings/decompilação)

| Pref | Uso | Default |
| --- | --- | --- |
| `login_method` | serviço (5/6/7/8) | 0 |
| `sync_on_start` | sync ao abrir (LoginActivity) | — |
| `sync_auto_update` | permite sync periódico | true |
| `sync_interval_hours` / `sync_custom_interval` | intervalo | 2 h (`*60`) / 30 min |
| `sync_item_limit` | artigos por pull | 1000 |
| `sync_include_starred` | sincronizar estrelados | false |
| `sync_server_reads` | puxar estado de leitura do servidor | true |
| `sync_server_reads`/`sync_all`/`sync_all_offline`/`sync_offline*` | políticas de escopo/offline | — |
| `sync_notifiable_custom` + `sync_notify_*` | notificações por feed (som/LED/vibra) | false |
| `cache_auto_clean_read/unread`, `cache_keep_starred/cached` | limpeza pós-sync | 1 / -1 / true / false |
| `offline_feed_items_limit` | limite do offline automático | 50 |
| `last_sync_time`, `unread_count` | estado | 0 |

## 12. Implicações para o rebuild

1. **Outbox próprio é o coração**: manter fila de operações idempotente no Room (`history` → `sync_operations`), com replay no início do sync e remoção só após 2xx. Modelar os mesmos tipos (lido, estrelado, labels, assinaturas, pastas) com upsert por `uid` (UNIQUE).
2. **`WorkManager` substitui `AlarmManager`+receiver**: periodic work com constraints (rede), alinhamento opcional; `OneTimeWorkRequest` p/ manual; foreground info p/ sync longo (FGS `dataSync` no Android 14+).
3. **Feedly**: modernizar auth para OAuth2 próprio (Authorization Code + PKCE), mesmos endpoints v3; `newerThan` + `continuation` + `count ≤ 1000`; markers em lote; **nunca** reutilizar client id/secret do app original.
4. **Inoreader/OldReader**: implementar OAuth2 quando disponível (Inoreader exige OAuth2 hoje; Old Reader aceita OAuth/token modernos) — sem e-mail/senha em prefs; usar `EncryptedSharedPreferences`/Keystore para tokens.
5. **Estado de leitura consensual**: replicar o padrão "ids do servidor → reconciliar local" (`/streams/…/ids` + update em massa). Cuidado com race entre push do outbox e pull de ids — o original resolve com transações do provider; no novo, usar transação Room.
6. **Notificações e badge**: manter contagem por feed (`notification=1`), canais por prioridade e badge no ícone.
7. **Offline automático**: candidato a policy no WorkManager (`limit`, somente Wi-Fi, keep starred).
8. **Buscas/limpezas**: `Y/G` mostram que a UI depende de agregados (`unread_count`, `newest_item_time`) — manter colunas desnormalizadas no Room para listas rápidas (ou usar views).

## 13. Evidências e arquivos

- `analysis/sync/`: `SyncService.onHandleIntent.java`, `ep6.I.java` (full sync), `ep6.L/M/G/Y/d/e.java`, `defpackage.sn6` → `feedly/*.java` (markers, streams, parsers), `CleanService.a.java`, `ReaderProvider.query.java`
- Bundle de evidências: `analysis/07-evidence-bundle.json` (47 registros; IDs `ev_*`)
- Tipos: **observado** (decompilado), **inferido** (ex.: direção do update em `ReaderProvider.p`, corpo de `gx6`, API GData de `un6`)

## 14. Adendo — clientes GData (The Old Reader / Inoreader, base `un6`)

Base comum `un6` (34 métodos) com URLs via `f(endpoint)` = `<base><endpoint>?client=greader` (subclasses concatenam `&...`).

**Base URLs**: Old Reader `https://theoldreader.com` (fixo); Inoreader `https://inoreader.com` (pref `inoreader_base_url`). Headers extras do Inoreader: `AppId: 999999346`, `AppKey: mMJ988jRkf3FttaeEgNalb4n9W4In95x` (**credenciais do dev original — não reutilizar**).

**Autenticação (ClientLogin, não OAuth)**:
- `POST /accounts/ClientLogin` form `accountType=GOOGLE`, `Email`, `Passwd`, `service=reader` → parse `Auth=<token>`; cache em pref `google_auth` por **3 dias** (e-mail/senha em `google_passwd`)
- `GET /reader/api/0/token` → edit token de escrita, cache 15 min (mantido em memória)
- Escritas exigem `T=<edit token>` no form

**Endpoints**:
| Operação | Chamada |
| --- | --- |
| Tags/pastas | `GET /reader/api/0/tag/list?output=json&ck=<syncTime>` |
| Assinaturas | `GET /reader/api/0/subscription/list?output=json&ck=<syncTime>` |
| Conteúdo | `GET /reader/api/0/stream/contents?output=json&ck=…&s=<stream>&xt=user/-/state/com.google/read&c=<continuation>&ot=<ts>&r=n|o&n=<count>` |
| IDs | `GET /reader/api/0/stream/items/ids?output=json&s=…[&merge=true&includeAllDirectStreamIds=true][&xt=…][&c=…][&ot=…][&r=…][&n=…]` |
| Marcar tudo lido | `POST /reader/api/0/mark-all-as-read` form `T,s,t,ts=<ts+999>` → corpo `OK` |
| Assinatura | `POST /reader/api/0/subscription/edit` form `T,t,ac=edit|subscribe|unsubscribe,s,a=<categoria>,r=<categoria>` → `OK` |
| Edits de item (ler/estrela/label) | `POST /reader/api/0/edit-tag` (mesmo contrato GData) |

Streams: default `user/-/state/com.google/reading-list`; estrelados `user/-/state/com.google/starred`. `un6.d()/e()` = limites base 10000/1000 (Inoreader sobrescreve `d/e`). Inoreader sobrescreve `k()`: IDs vêm como Long em hex **padded a 16 chars**.

## 15. Adendo — upsert de itens (`ep6.L`) e URIs especiais do provider

**Upsert (`ep6.L(lista, syncTime, cw6, modo)`)**:
- Coleta uids; consulta existentes (`uid IN (...)`); transação via provider
- Para cada item: resolve subscription por `origin` (`on6.d`); desembrulha links Google (`news.google.com/news/url...url=`, `google.com/url?...url=`); se sem imagem, extrai a 1ª `<img>` do HTML (`gif`→`img/gif`, `jpg/png`→`image/thumbnail`, `//`→`http:`)
- **Novo**: `sync_time=j`; se já lido no servidor → `read_time=j`; conta em `cw6.a`; incrementa `cw6.b[sub]` se o feed tem `notification=1`
- **Existente estrelado**: atualiza sem `sync_time/read/read_time` (preserva estado local)
- **Labels**: upsert em `tag2item` (`update ... else insert`); ao final, apaga labels de itens tocados com `sync_time <> j` (remoção de labels obsoletas)
- `bulkInsert(ReaderProvider.k /*items_insert 44*/)`; se houve subs, recalcula e emite mudança de dados
- Modo 2 (RSS local): marca `sync_time=j` em todos os recebidos; modo 1: só `read=0`

**URIs do `ReaderProvider` (matcher completo)**:
| Código | URI | Uso |
| --- | --- | --- |
| 1/2/3 | begin_txn / success_txn / end_txn | transações via ContentResolver |
| 4 / 5 | reconnect_db / raw_query | reabrir banco / query crua |
| 10/11 | subscription/#, subscription | CRUD feeds |
| 20/21 | tag/#, tag | CRUD pastas/labels |
| 30/31 | tag2sub/#, tag2sub | vínculo pasta↔feed |
| 32 | tags_subs | UNION pastas+feeds (árvore da UI) |
| 40/41 | item/#, item | CRUD artigos |
| 42 / 44 | items_subs / items_insert | joins / bulk insert de itens |
| 45/46 | item_sync / items_sync_insert | staging de sync |
| 50/51 | tag2item/#, tag2item | labels por item |
| 60/61 | history/#, history | **outbox** |
| 70 | update_newest_time | recalcula `newest_item_time` (`MAX(updated_time)` por feed) |
| 71 | update_unreads | recalcula contadores de não lidos (usado por `Y()`) |
| 72 | update_reads | bulk de leitura (usado na limpeza `G()`) |
| 73/74/75/76 | update_mark_tag / starred / mark_read / tag2item | atualizações em massa (ex.: `p` = **update_mark_read** recebido por `dp6` com `ids='…'`) |

Mapeamento dos campos usados pelo engine: `n`=`update_newest_time`, `o`=`update_unreads`, `p`=`update_mark_read`, `r`=`update_reads`.

## 16. Estado das pendências

1. ~~Decompilar `un6`~~ — **feito** (§14)
2. ~~Ler `ep6.L` completo e fechar reconciliação~~ — **feito** (§15; `dp6`→`update_mark_read` confirmado por inferência direta do URI)
3. **Pendente (design)**: modelar a outbox no novo app com os tipos 1–8 e validar casos de conflito — próxima fase do rebuild

Arquivos novos: `analysis/sync/un6.*.java`, `analysis/sync/ReaderProvider.clinit.java`, `analysis/sync/ReaderProvider.d.java` (parcial salvo; completa em `analysis/sync/ep6.L.java`).
