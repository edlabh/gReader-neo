# Prompt otimizado — Redesign visual do gReader Neo

> Gerado com a skill `prompt-engineer` (frameworks RODES + Chain of Thought), a partir do pedido original e das evidências do catálogo AAS (`anti-ui-slop`, `android-jetpack-compose-expert`, `mobile-design`).

```
Role: Você é um designer de produto mobile e engenheiro Android sênior (Jetpack Compose + Material 3),
especializado em apps de leitura com identidade visual própria (referências: Feedly, Pocket, Readwise).

Objective: Redesenhar a interface do app gReader Neo para que pareça um produto premium e "não genérico",
mantendo toda a funcionalidade existente (Feedly, leitura offline-first, 3 modos de leitor).

Contexto do produto:
- App de leitura de RSS sincronizado com Feedly; público: leitores que consomem muitos artigos por dia.
- Plataforma: Android 15+ (minSdk 35), Compose + Material 3, tema claro/escuro + AMOLED.
- Estrutura atual: Home lista de feeds → tela separada de artigos. Requisito novo: Home passa a mostrar
  TODOS os artigos por padrão; o painel de feeds/pastas vira um drawer recolhível que filtra a lista.

Details:
- Tokens: paleta da marca (indigo #4C5BD4 + superfícies frias), raios 6/10/14/20/28, espaçamento 4/8/12/16/24.
- Identidade: glifo RSS desenhado em Canvas (usado no drawer, estados vazios e login).
- Estados obrigatórios: carregando, vazio (sem feeds / sem artigos), erro de sync com retry, offline,
  desconectado, seleção múltipla, busca sem resultados.
- Regras de toque: alvos ≥48dp; ação primária (sincronizar) na zona do polegar (FAB inferior).
- Performance: listas com LazyColumn e keys estáveis; sem divisores pesados; recomposição mínima.

Approach step-by-step (Chain of Thought):
1. Definir contrato de design (hierarquia, estados, componentes permitidos, critérios de aceite observáveis).
2. Aplicar tokens e tema (paleta, tipografia, formas) mantendo dynamic color como opção.
3. Redesenhar a Home: drawer recolhível + lista de artigos como tela principal, com busca, seleção e swipe.
4. Polir leitor (template HTML do artigo + barra superior), login (hero) e configurações (cards agrupados).
5. Renderizar/compilar e inspecionar: corrigir clipping, contraste, estados ausentes e toques <48dp.

Output format: código Compose compilável + resumo das mudanças por tela, com checklist de estados cobertos.

Sense Check: valide que (a) nenhuma funcionalidade foi removida, (b) todos os estados têm representação visual,
(c) o app mantém contraste AA e alvos de toque adequados, (d) o resultado parece específico do produto
(nada de "UI genérica de IA"), e (e) build debug e release continuam passando.
```
