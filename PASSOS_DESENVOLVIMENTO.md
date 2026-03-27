# Passos para o Desenvolvimento

Este documento organiza a implementação da API do projeto com base nos requisitos funcionais e não funcionais definidos no README.md.

O objetivo é conduzir o desenvolvimento com foco em:

- cobertura completa dos requisitos do desafio
- arquitetura MVC bem definida
- boas práticas de Clean Code e SOLID
- uso de DTOs e mapeamento manual dedicado
- tratamento global de exceções
- cobertura consistente de testes em todas as camadas

## 1. Estrutura base do backend

Primeiro, consolidar a estrutura do projeto para suportar a evolução da API:

- `config`
- `controller`
- `service`
- `repository`
- `entity`
- `dto`
- `mapper`
- `exception`
- `enums`
- `client` ou `integration`

Organização por domínio sugerida:

- `project`
- `member`
- `allocation`
- `report`
- `security`

## 2. Configurações iniciais

Antes das regras de negócio, preparar a base técnica:

- configurar `Spring Security` com autenticação básica em memória
- configurar `Swagger/OpenAPI`
- estruturar mapeadores dedicados para conversão entre entidades e DTOs
- configurar tratamento global de exceções com `@RestControllerAdvice`
- configurar cobertura com `JaCoCo`
- ajustar perfis para desenvolvimento e testes

## 3. Dependências complementares

Adicionar ao backend:

- plugin `jacoco-maven-plugin`

Opcionalmente:

- `spring-boot-starter-actuator`

## 4. Modelagem do domínio

Modelar os elementos centrais da aplicação:

- `Project`
- `ProjectStatus`
- `RiskLevel`
- `Member`
- entidade de associação entre projeto e membro, se necessário

Campos principais de `Project`:

- `id`
- `name`
- `startDate`
- `expectedEndDate`
- `actualEndDate`
- `budget`
- `description`
- `manager`
- `status`
- `riskLevel`

## 5. DTOs

Separar claramente entrada e saída:

- `ProjectCreateRequest`
- `ProjectUpdateRequest`
- `ProjectResponse`
- `ProjectStatusUpdateRequest`
- `MemberRequest`
- `MemberResponse`
- `ProjectMemberAllocationRequest`
- `PortfolioReportResponse`
- `ApiErrorResponse`

Boas práticas:

- DTO de entrada com validação usando `Bean Validation`
- DTO de saída sem expor detalhes internos
- nada de expor entidades diretamente nos controllers

## 6. Regras de negócio prioritárias

Implementar no `service` com foco forte em testes:

- CRUD completo de projetos
- cálculo dinâmico da classificação de risco
- transição de status sem pular etapas
- `cancelado` permitido a qualquer momento
- bloqueio de exclusão para projetos em `iniciado`, `em andamento` e `encerrado`
- associação apenas de membros com atribuição `funcionário`
- mínimo de `1` e máximo de `10` membros por projeto
- máximo de `3` projetos simultâneos por membro em projetos não encerrados ou cancelados
- geração do relatório resumido do portfólio

## 7. Integração de membros

Como o cadastro de membros deve vir de uma API REST externa mockada:

- criar uma abstração `MemberClient`
- começar com implementação mockada local
- manter a estrutura pronta para troca futura por integração REST real

Operações esperadas:

- criar membro
- consultar membro por id
- listar membros

## 8. Controllers da API

Endpoints previstos:

### ProjectController

- `POST /api/projects`
- `GET /api/projects`
- `GET /api/projects/{id}`
- `PUT /api/projects/{id}`
- `PATCH /api/projects/{id}/status`
- `DELETE /api/projects/{id}`

### ProjectMemberController

- `POST /api/projects/{id}/members`
- `DELETE /api/projects/{id}/members/{memberId}`

### MemberController

- `POST /api/members`
- `GET /api/members`
- `GET /api/members/{id}`

### PortfolioReportController

- `GET /api/reports/portfolio-summary`

## 9. Paginação e filtros

Na listagem de projetos, implementar:

- paginação com `Pageable`
- filtros por nome
- filtros por status
- filtros por gerente
- filtros por risco
- filtros por faixa de orçamento
- filtros por intervalo de datas

Pode ser implementado com:

- `Specification`
- consultas personalizadas no repositório

## 10. Tratamento global de exceções

Criar exceções específicas para o domínio:

- `ResourceNotFoundException`
- `BusinessRuleException`
- `InvalidStatusTransitionException`
- `ProjectDeletionNotAllowedException`
- `MemberAllocationException`
- `ExternalIntegrationException`

O handler global deve tratar:

- erros de validação
- erros de regra de negócio
- recurso não encontrado
- exceções inesperadas

Formato sugerido da resposta de erro:

- `timestamp`
- `status`
- `error`
- `message`
- `path`

## 11. Estratégia de testes

Os testes devem cobrir não apenas services, mas também controllers, DTOs e tratamento de exceções.

### Services

Cobrir:

- cálculo de risco
- transições de status
- regras de exclusão
- regras de associação de membros
- geração de relatório

### Controllers

Usar `MockMvc` para validar:

- status HTTP
- payload de request e response
- autenticação/autorização
- contratos dos endpoints

### DTOs

Validar:

- campos obrigatórios
- formatos inválidos
- restrições de tamanho e valor

### Mapeamento

Testar:

- conversão de entity para response DTO
- conversão de request DTO para entity

### Repository

Usar testes com `H2` para:

- consultas customizadas
- filtros
- paginação

### Exception Handler

Validar:

- respostas padronizadas para erros de validação
- respostas padronizadas para erros de negócio
- respostas para exceções não tratadas

## 12. Meta de cobertura

Objetivos recomendados:

- mínimo de `70%` de cobertura total
- pelo menos `80%` na camada de serviço
- todas as regras críticas cobertas por testes automatizados

## 13. Ordem sugerida de implementação

### Fase 1

- configurar `ModelMapper`
- configurar `GlobalExceptionHandler`
- configurar `JaCoCo`
- consolidar segurança e OpenAPI

### Fase 2

- criar enums
- criar entidades
- criar repositórios
- criar DTOs iniciais

### Fase 3

- implementar regras de negócio de projetos
- implementar CRUD de projetos
- implementar cálculo de risco
- implementar transição de status

### Fase 4

- implementar mock da API de membros
- implementar associação de membros aos projetos
- aplicar regras de alocação

### Fase 5

- implementar relatório resumido
- implementar paginação e filtros
- revisar documentação Swagger

### Fase 6

- ampliar testes unitários e de integração
- validar cobertura
- revisar código com foco em Clean Code e SOLID

## 14. Boas práticas que devem ser mantidas

- controllers finos, services com regra de negócio
- repositories focados em persistência
- DTOs para entrada e saída
- entidades sem responsabilidade de apresentação
- nomes claros e consistentes
- métodos pequenos e com responsabilidade única
- evitar duplicação
- validações centralizadas
- respostas de erro padronizadas

## 15. Entrega esperada da API

Ao final, a API deverá apresentar:

- arquitetura MVC clara
- separação adequada entre camadas
- documentação Swagger funcional
- segurança básica ativa
- tratamento global de exceções
- regras de negócio implementadas
- testes automatizados cobrindo camadas críticas
- cobertura mínima exigida atendida
