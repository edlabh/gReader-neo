# Avaliação Total — Usabilidade e Visual (gReader Neo)

> Executada em 2026-10-09 seguindo `docs/prompt-avaliacao-total.md` (stack de skills AAS: ux-audit, uxui-principles, ux-feedback, anti-ui-slop, high-end-visual-design, design-system, material-design, mobile-design, ui-review, ui-a11y, ux-copy, android-jetpack-compose-expert, design-spells, emil-design-eng, redesign-existing-projects). Builds debug + release validados após as correções.

## A. Diagnóstico por tela (nota 0–100)

| Tela | Antes | Depois | Observações |
| --- | --- | --- | --- |
| Home (artigos + drawer) | 82 | **91** | Hierarquia lido/não lido excelente, estados completos, densidade configurável. Corrigidos: CTA duplicado, estados vazios ambíguos, confirmação de ação em massa, feedback de swipe, alvos de toque |
| Leitor (3 modos + navegador) | 88 | 88 | Segmentado claro, ações com ícones, histórico no voltar. Pendência P2: ação "manter não lido" sem UI (recurso futuro) |
| Assinar feed | 84 | **89** | Prévia + pasta ✅; adicionado estado inicial com dica |
| Gerenciar assinaturas | 86 | 86 | Agrupamento por pasta, diálogos claros |
| Configurações | 90 | 90 | Seções em cards, seletor único de frequência, 3 idiomas |
| Login/Conta | 88 | 88 | Hero com glifo, estados localizados |
| Widgets | 84 | **88** | Badge de contraste AA corrigido |

## B. Problemas por severidade

**P0 — nenhum.** Nenhum bloqueio funcional ou barreira grave de acessibilidade.

**P1 — 7 corrigidos nesta execução:**
1. CTA "Conectar" duplicado (banner + estado vazio) — violava minimalismo
2. "Marcar tudo lido" (escopo global) sem confirmação — prevenção de erro (Nielsen #5)
3. Ícone do swipe visível em apenas uma direção — feedback inconsistente
4. Estado vazio de "não lidos" inexistente/enganoso ("sincronize" mesmo com tudo lido) — visibilidade do estado (Nielsen #1)
5. Contraste do badge do widget 4,3:1 → **5,9:1** (WCAG AA)
6. Alvo de toque das linhas do drawer 44dp → **48dp** (mobile-design)
7. Busca sem resultados usava "Nenhum resultado" (copy de outra tela) — microcopy (ux-copy)

**P2 — backlog (não aplicado, avaliar depois):**
- Ação "manter não lido" no leitor (feature do escopo original)
- Snackbar com **undo** para remover feed e para swipe de leitura
- Transições animadas entre telas (Navigation Compose)
- Testes instrumentados/UI Compose para fluxos críticos
- Modo Lista com thumbnail opcional

## C. Correções aplicadas (diffs resumidos)

1. **HomeScreen**
   - Banner de conexão oculto quando a lista está vazia (CTA único)
   - Confirmação para "Marcar tudo como lido" quando o filtro é "Todos os artigos"
   - `EmptyState` com 4 variações intencionais: desconectado · sem feeds · buscar · **tudo lido**
   - Swipe: ícone ✓ centralizado (visível nas duas direções)
   - Seletor de tópico: Row clicável (raio 8dp) sem padding/ripple de botão, seta ↧ discreta
   - Linhas do drawer: 44dp → **48dp**
   - Labels de ação para TalkBack: toque longo = "Selecionar artigo" / "Remover feed"
2. **NeoApp**: banner "N novos artigos" some sozinho após 4 s
3. **SubscribeScreen**: estado inicial com dica ("Busque feeds por nome ou URL para começar")
4. **Widget**: badge `#C62828` (contraste AA)
5. **Strings**: 9 novas chaves traduzidas nos 3 idiomas (pt-BR, en, es)

## D. Checklist de verificação final

- [x] Nenhuma funcionalidade removida (apenas estados/confirmações adicionados; uma duplicação removida)
- [x] Minimalismo preservado (nenhum elemento novo permanente além do diálogo de confirmação)
- [x] Todos os estados com representação intencional e copy própria
- [x] Alvos de toque ≥48dp nas listas e drawer
- [x] Contraste AA nos dois temas (incl. AMOLED) e no widget
- [x] 3 idiomas íntegros (incluindo novas mensagens)
- [x] Builds **debug** e **release (R8)** verdes

## E. Rodada P2 — executada

- [x] **Undo em ações destrutivas**: swipe de leitura (desfaz a leitura) e remoção de feed (reassina no Feedly com as pastas originais, restaura o filtro e reabre a janela de sync para trazer os artigos de volta)
- [x] **Manter não lido** no leitor: alternativa à marcação automática ao abrir; flag local (`keepUnread`) respeitada por "marcar como lido" e "marcar tudo como lido"
- [x] **Animações de transição** entre telas (fade + slide sutil; direção consistente ao avançar/voltar)
- [x] **Testes**: JVM para os DTOs do Feedly (regressão do bug de categorias/tags) + smoke test de UI Compose (Home), rodável com device conectado
- [x] **Modo Lista com miniaturas opcionais** (toggle em Aparência; padrão desligado)

Pendentes para uma rodada futura: undo para "marcar tudo como lido", cache offline de artigos/imagens, filtros salvos, tablet 2 painéis. Rodar novamente o prompt de `docs/prompt-avaliacao-total.md` após a rodada para reavaliar as notas.

## F. Rodada 3 — executada

- [x] **Desfazer "marcar tudo como lido"**: captura os artigos que estavam não lidos antes da ação e desfaz exatamente esses (local + markers em lote no Feedly). Corrigido também um bug de escopo: marcar tudo dentro de uma **pasta** afetava os outros feeds
- [x] **Cache offline**: o modo Leitura guarda o conteúdo extraído no banco (migração v1→v2; releituras funcionam offline) + pré-cache de imagens dos artigos recentes em Wi-Fi (preferência em Armazenamento; 60 imagens por sync)
- [x] **Filtros salvos**: salvar o filtro atual (feed/pasta + busca + não lidos + ordenação) com um nome no seletor de tópico; aplicar e excluir pelo próprio seletor
- [x] **Tablet (2 painéis)**: em telas ≥ 840dp o drawer vira fixo e o leitor abre num painel lateral, sem sair da lista
- [x] **Testes**: codec dos filtros salvos (round-trip + tolerância a JSON ausente/corrompido)

Pendentes para o futuro: widgets adicionais, "sincronizar somente em Wi-Fi", backup/restauração de preferências.
