# Passos para o Desenvolvimento do Frontend

Este documento organiza o desenvolvimento da interface web com base no contrato atual do backend Spring Boot já implementado no projeto.

O objetivo é transformar a API existente em uma experiência de uso clara, elegante e segura para uma empresa de gestão de projetos, com uma interface minimalista, fluida e profissional.

## 1. Análise do Backend

O backend já expõe os módulos necessários para iniciar o frontend:

- autenticação básica
- projetos
- membros
- alocação de membros em projetos
- relatório resumido do portfólio

### Autenticação

Segurança atual:

- autenticação HTTP Basic
- credenciais em memória:
  - usuário: `admin`
  - senha: `admin123`

Impacto no frontend:

- criar camada central de `fetch` com envio automático de `Authorization: Basic ...`
- considerar uma tela de login simples que armazene a credencial em memória ou `sessionStorage`

## 2. Endpoints disponíveis

### Projetos

#### `POST /api/projects`

Cria um projeto.

Request:

```json
{
  "name": "Projeto A",
  "startDate": "2026-03-26",
  "expectedEndDate": "2026-06-26",
  "budget": 100000.00,
  "description": "Descricao do projeto",
  "managerId": 1
}
```

Response:

```json
{
  "id": 1,
  "name": "Projeto A",
  "startDate": "2026-03-26",
  "expectedEndDate": "2026-06-26",
  "actualEndDate": null,
  "budget": 100000.00,
  "description": "Descricao do projeto",
  "managerId": 1,
  "managerName": "Maria",
  "status": "EM_ANALISE",
  "riskLevel": "BAIXO"
}
```

#### `GET /api/projects`

Lista projetos com paginação e filtros.

Parâmetros possíveis:

- `page`
- `size`
- `name`
- `status`
- `riskLevel`
- `managerId`
- `budgetMin`
- `budgetMax`
- `startDateFrom`
- `startDateTo`
- `expectedEndDateFrom`
- `expectedEndDateTo`

Response:

```json
{
  "content": [],
  "page": 0,
  "size": 10,
  "totalElements": 0,
  "totalPages": 0,
  "first": true,
  "last": true,
  "empty": true
}
```

#### `GET /api/projects/{id}`

Retorna um projeto por id.

#### `PUT /api/projects/{id}`

Atualiza um projeto completo.

Request:

```json
{
  "name": "Projeto Atualizado",
  "startDate": "2026-03-26",
  "expectedEndDate": "2026-07-26",
  "actualEndDate": null,
  "budget": 200000.00,
  "description": "Descricao nova",
  "managerId": 1,
  "status": "ANALISE_REALIZADA"
}
```

#### `PATCH /api/projects/{id}/status`

Atualiza apenas o status.

Request:

```json
{
  "status": "ANALISE_APROVADA"
}
```

#### `DELETE /api/projects/{id}`

Exclui projeto quando permitido.

### Membros

#### `POST /api/members`

Cria membro na API mockada.

Request:

```json
{
  "name": "Ana",
  "assignment": "funcionario"
}
```

Response:

```json
{
  "id": 1,
  "name": "Ana",
  "assignment": "funcionario"
}
```

#### `GET /api/members`

Lista membros.

#### `GET /api/members/{id}`

Busca membro por id.

### Alocação

#### `POST /api/projects/{projectId}/members`

Aloca membro em projeto.

Request:

```json
{
  "memberId": 2
}
```

Response:

```json
{
  "allocationId": 10,
  "projectId": 1,
  "memberId": 2,
  "memberName": "Carlos",
  "memberAssignment": "funcionario"
}
```

#### `GET /api/projects/{projectId}/members`

Lista membros alocados no projeto.

#### `DELETE /api/projects/{projectId}/members/{memberId}`

Remove membro do projeto.

### Relatório

#### `GET /api/reports/portfolio-summary`

Response:

```json
{
  "statusSummary": [
    {
      "status": "EM_ANALISE",
      "projectCount": 2,
      "totalBudget": 1000.00
    }
  ],
  "averageClosedProjectDurationInDays": 25.5,
  "totalUniqueAllocatedMembers": 4
}
```

### Erros

Formato padrão de erro:

```json
{
  "timestamp": "2026-03-26T17:00:00-03:00",
  "status": 400,
  "error": "Bad Request",
  "message": "name is required",
  "path": "/api/projects"
}
```

Impacto no frontend:

- criar um parser central de erro
- exibir mensagens amigáveis em `toast`, `alert` e estado de formulário

## 3. Estrutura sugerida do frontend

Estrutura recomendada para o projeto Next.js:

```text
frontend/
├── app/
│   ├── login/
│   ├── dashboard/
│   ├── projects/
│   │   ├── page.tsx
│   │   ├── new/
│   │   └── [id]/
│   ├── members/
│   └── reports/
├── components/
│   ├── ui/
│   ├── layout/
│   ├── forms/
│   ├── projects/
│   ├── members/
│   └── reports/
├── lib/
│   ├── api/
│   ├── auth/
│   ├── format/
│   └── utils/
├── hooks/
├── types/
└── styles/
```

## 4. Tipos TypeScript a criar primeiro

### Projetos

- `ProjectResponse`
- `ProjectCreateRequest`
- `ProjectUpdateRequest`
- `ProjectStatusUpdateRequest`
- `ProjectFilter`
- `PagedResponse<T>`

### Membros

- `MemberRequest`
- `MemberResponse`

### Alocação

- `ProjectMemberAllocationRequest`
- `ProjectMemberAllocationResponse`

### Relatório

- `PortfolioReportResponse`
- `PortfolioStatusSummaryResponse`

### Erro

- `ApiErrorResponse`

## 5. Serviços de API do frontend

Criar serviços centralizados:

- `auth-api.ts`
- `projects-api.ts`
- `members-api.ts`
- `allocations-api.ts`
- `reports-api.ts`

Responsabilidades:

- envio dos headers
- serialização de query params
- parse do payload
- tratamento padronizado de erro

## 6. Telas prioritárias

### 1. Login

Objetivo:

- capturar usuário e senha
- persistir autenticação simples
- redirecionar para dashboard

### 2. Dashboard

Objetivo:

- mostrar visão geral do portfólio
- exibir cartões com métricas principais
- destacar distribuição por status
- oferecer atalhos para projetos, membros e relatórios

### 3. Listagem de projetos

Objetivo:

- tabela paginada
- filtros laterais ou toolbar superior
- ações rápidas:
  - visualizar
  - editar
  - alterar status
  - excluir

### 4. Cadastro de projeto

Objetivo:

- formulário com:
  - nome
  - datas
  - orçamento
  - descrição
  - gerente

### 5. Detalhe do projeto

Objetivo:

- dados completos do projeto
- status atual
- risco calculado
- membros alocados
- ações de alocação e remoção

### 6. Membros

Objetivo:

- listar membros
- cadastrar novos membros na API mockada
- filtrar por atribuição

### 7. Relatório

Objetivo:

- visualizar resumo do portfólio
- usar gráficos e cards executivos

## 7. Fluxo recomendado de implementação

### Etapa 1. Base da aplicação

- configurar `shadcn/ui`
- configurar tipagem compartilhada
- criar cliente HTTP
- implementar autenticação básica
- criar layout principal com sidebar e header

### Etapa 2. Design system

- criar tokens visuais
- criar componentes-base:
  - `Button`
  - `Input`
  - `Textarea`
  - `Select`
  - `Dialog`
  - `Drawer`
  - `Table`
  - `Badge`
  - `Card`
  - `Tabs`
  - `Toast`
  - `Skeleton`

### Etapa 3. Projetos

- criar listagem paginada
- conectar filtros
- criar formulário de criação
- criar edição
- criar detalhe do projeto

### Etapa 4. Membros

- criar listagem
- criar formulário de cadastro
- integrar com alocação do projeto

### Etapa 5. Relatório

- criar painel com:
  - cards de números
  - gráfico por status
  - resumo de orçamento
  - bloco de duração média

### Etapa 6. Refinamento

- estados de loading
- tratamento de erro
- empty states
- responsividade
- acessibilidade

## 8. Design system proposto

Direção visual:

- minimalista
- corporativa
- elegante
- leve
- com boa sensação de espaço e legibilidade

### Conceito

A interface deve parecer um produto interno premium de gestão de portfólio, com leitura rápida para gestores e sensação de organização. O visual deve evitar excesso de ornamento e também evitar aparência genérica de painel administrativo básico.

### Personalidade visual

- superfícies claras e respiráveis
- alto contraste textual
- tipografia sóbria
- cantos suaves
- hierarquia forte entre dados e ações
- uso comedido de cor

### Paleta sugerida

- fundo principal: `#f4f6f3`
- fundo elevado: `#fbfcfa`
- texto principal: `#162018`
- texto secundário: `#5f6b62`
- borda: `#d7dfd6`
- primária: `#1f6b52`
- primária escura: `#164b39`
- destaque quente: `#c98a2e`
- erro: `#b64545`

### Tipografia

Sugestão:

- títulos: `Manrope` ou `Plus Jakarta Sans`
- corpo: `Inter` ou `Manrope`

Escala:

- `12`
- `14`
- `16`
- `20`
- `24`
- `32`
- `40`

### Espaçamento

Usar escala fluida baseada em:

- `4`
- `8`
- `12`
- `16`
- `24`
- `32`
- `48`
- `64`

### Componentes visuais

- cards com borda discreta e sombra muito suave
- badges de status com fundo tonal e texto forte
- tabelas limpas com linhas espaçadas
- filtros em barra superior ou painel lateral compacto
- formulários em grid responsivo

### Movimento

Usar animações curtas e discretas:

- fade/slide em drawers e dialogs
- skeletons para carregamento
- transições suaves em hover e foco

## 9. Mapeamento de status e risco para UI

### Status

- `EM_ANALISE`
- `ANALISE_REALIZADA`
- `ANALISE_APROVADA`
- `INICIADO`
- `PLANEJADO`
- `EM_ANDAMENTO`
- `ENCERRADO`
- `CANCELADO`

Sugestão visual:

- análise: tons neutros e azuis suaves
- andamento: verde moderado
- encerrado: verde escuro
- cancelado: vermelho discreto

### Risco

- `BAIXO`: verde
- `MEDIO`: dourado
- `ALTO`: vermelho

## 10. Componentes específicos do domínio

### Projetos

- `ProjectTable`
- `ProjectFilters`
- `ProjectForm`
- `ProjectStatusBadge`
- `ProjectRiskBadge`
- `ProjectDetailsCard`

### Membros

- `MemberTable`
- `MemberForm`
- `ProjectMembersPanel`
- `AllocationList`

### Relatório

- `PortfolioSummaryCards`
- `StatusBudgetChart`
- `StatusDistributionList`
- `DurationKpi`

## 11. Ordem ideal de construção do frontend

1. autenticação básica e layout base
2. tokens do design system
3. listagem de projetos com paginação
4. filtros de projetos
5. criação e edição de projeto
6. tela de detalhe do projeto
7. membros
8. alocação de membros
9. relatório resumido
10. refinamento visual e UX

## 12. Resultado esperado do frontend

Ao final, o frontend deve entregar:

- interface responsiva para desktop e tablet
- experiência clara de gestão de projetos
- visual corporativo refinado
- integração completa com o backend atual
- componentes reutilizáveis
- base sólida para demonstração do desafio técnico
