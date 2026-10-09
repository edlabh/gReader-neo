
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
