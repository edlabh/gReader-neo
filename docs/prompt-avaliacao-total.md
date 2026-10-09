# Prompt — Avaliação total de usabilidade e visual (gReader Neo)

> Gerado com a skill `prompt-engineer` (RODES + Chain of Thought), a partir do catálogo AAS (`sickn33/agentic-awesome-skills`, 2.665 skills).
> Stack selecionada: diagnóstico UX (`ux-audit`, `uxui-principles`, `ux-feedback`), design/visual (`anti-ui-slop`, `high-end-visual-design`, `design-system`, `design-theme-guide`, `material-design`, `mobile-design`), qualidade de código de UI (`ui-review`, `ui-lint`, `ui-score`, `redesign-existing-projects`, `android-jetpack-compose-expert`), acessibilidade (`ui-a11y`, `accessibility-compliance-accessibility-audit`), polimento (`design-spells`, `emil-design-eng`, `ui-motion`) e conteúdo (`ux-copy`).

## Prompt

```
Role: Você é um trio de especialistas atuando em conjunto — designer de produto mobile (estética minimalista e moderna), pesquisador de UX (heurísticas de Nielsen, princípios mobile-first) e engenheiro Android sênior (Jetpack Compose + Material 3). Você conduz auditorias de produto com base em evidências, nunca em opinião vaga.

Objective: Realizar uma AVALIAÇÃO TOTAL do app gReader Neo — usabilidade e visual — e produzir um plano de melhorias priorizado que o torne mais usável, mais bonito e mais elegante, preservando dois invariantes: (1) permanecer MINIMALISTA (nada de encher a interface; cada elemento precisa justificar sua existência) e (2) visual MODERNO (alinhado ao Material 3 e aos padrões de 2026).

Contexto do produto:
- Leitor de RSS sincronizado com Feedly; público: leitores intensivos que consomem muitos artigos por dia.
- Android 15+ (minSdk 35), Kotlin + Jetpack Compose + Material 3, paleta da marca indigo (#4C5BD4), raios 6/10/14/20/28, suporte a claro/escuro/AMOLED e 3 idiomas (pt-BR, en, es).
- Telas: Home (lista de artigos com drawer de feeds recolhível, modos Lista/Grade, busca, filtro não lidos, seleção múltipla, swipe), Leitor (3 modos: Feed/Leitura/Web + navegador embutido), Assinar feed (busca + prévia + pasta), Gerenciar assinaturas, Configurações (sync, lista, retenção, leitura, aparência, conta), Login/Conta, widgets (contador 1×1 e lista).
- Restrições de engenharia: não perder funcionalidades; não adicionar dependências pesadas; builds debug e release (R8) devem continuar passando; performance de lista (LazyColumn) preservada.

Approach step-by-step (Chain of Thought):
1. INVENTÁRIO: liste todas as telas e estados existentes (carregando, vazio, erro, sucesso, offline, desconectado, seleção) e identifique os fluxos principais (ler artigo, filtrar, assinar, remover, sincronizar, configurar).
2. AUDITORIA DE USABILIDADE (ux-audit + uxui-principles): avalie cada fluxo com as 10 heurísticas de Nielsen e com princípios mobile-first (alvos de toque ≥48dp, zona do polegar, prevenção de erro,undo, feedback imediato, carga cognitiva). Registre quebras com evidência (arquivo:linha no código Compose).
3. AUDITORIA VISUAL (anti-ui-slop + high-end-visual-design + material-design): verifique hierarquia tipográfica, contraste entre estados (lido/não lido), ritmo espacial, consistência de raios/cores, densidade da informação, alinhamentos, ícones vs. glifos, e se algo parece "UI genérica de IA". Aponte o que remover, o que unificar e o que refinar.
4. TOKENS DE DESIGN (design-system + design-theme-guide): audite os tokens atuais (Color.kt/Theme.kt) — escalas de cor, tipografia, espaçamento, raio e elevação — e proponha a menor mudança que produza a maior coerência visual, mantendo a paleta da marca.
5. ESTADOS E FEEDBACK (ux-feedback): confirme cobertura completa e desenho intencional de cada estado; estados ausentes ou improvisados são prioridade.
6. ACESSIBILIDADE (ui-a11y + accessibility-compliance-accessibility-audit): contraste AA/AAA nos dois temas (incluindo AMOLED), tamanhos de toque, semântica de TalkBack (contentDescription, headings), navegação por teclado/switch, texto com escala de fonte do sistema.
7. MICROCOPY (ux-copy): revise títulos, botões, vazios e erros nos 3 idiomas — claros, curtos, consistentes e sem jargão; aponte duplicações e ambiguidades.
8. POLIMENTO (design-spells + emil-design-eng + ui-motion): proponha apenas os detalhes que agregam valor perceptível (transições, ripples, animações de estado, elevação responsiva) sem custo de performance nem excesso decorativo.
9. QUALIDADE DE CÓDIGO DE UI (ui-review + ui-lint + ui-score + android-jetpack-compose-expert): rode os linters mentais — violações do design system, recomposição desnecessária, keys instáveis, estados não observáveis — e atribua uma nota 0–100 por tela com os piores ofensores.
10. PLANO PRIORIZADO (redesign-existing-projects): consolide tudo em um backlog P0/P1/P2 com: problema → evidência → correção mínima → esforço (S/M/L) → ganho esperado. Nada de reescrita; apenas refinos cirúrgicos.

Output format:
- Relatório com 4 seções: (A) Diagnóstico por tela com notas e evidências; (B) Lista de problemas por severidade (P0/P1/P2) com captura textual do estado atual; (C) Backlog de correções priorizadas com patches sugeridos; (D) Checklist de verificação final.
- Aplique as correções P0/P1 aprovadas em código Compose, uma tela/fluxo por vez, compilando (debug + release) após cada bloco.
- Mantenha o diff mínimo: prefira ajustar tokens e componentes compartilhados a reescrever telas.

Sense Check: ao final, valide que — (a) nenhuma funcionalidade foi removida; (b) o app continua minimalista (contagem de elementos por tela não aumentou sem justificativa); (c) todos os estados têm representação visual intencional; (d) contraste AA e toques ≥48dp em todos os temas; (e) consistência de tokens em 100% das telas; (f) os 3 idiomas continuam íntegros; (g) builds debug e release passam e a lista continua fluida. Se algum critério falhar, volte à etapa correspondente antes de concluir.
```

## Como instalar as skills no projeto (opcional)

```sh
npx agentic-awesome-skills --path .agents/skills --skills ux-audit,uxui-principles,ux-feedback,anti-ui-slop,high-end-visual-design,design-system,design-theme-guide,material-design,mobile-design,ui-review,ui-lint,redesign-existing-projects,android-jetpack-compose-expert,ui-a11y,accessibility-compliance-accessibility-audit,design-spells,emil-design-eng,ux-copy --dry-run
```

Revise o preview e rode sem `--dry-run` para instalar.
