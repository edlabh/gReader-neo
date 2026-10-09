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
