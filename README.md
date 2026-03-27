# Gerenciador de Portfólio

Sistema para gerenciamento do portfólio de projetos de uma empresa, com acompanhamento do ciclo de vida dos projetos desde a análise de viabilidade até a finalização, incluindo equipe, orçamento, risco e indicadores consolidados.

## Objetivo

Este repositório será organizado como um monorepo com duas aplicações:

1. `backend/`: API REST em Java com Spring Boot
2. `frontend/`: interface web em React com Next.js e shadcn/ui

O projeto foi planejado com base em um desafio técnico para vaga de Desenvolvedor Java, preservando as regras de negócio e os critérios de avaliação propostos, mas com uma arquitetura separada entre backend e frontend para melhor experiência de uso, manutenção e apresentação.

## Escopo Funcional

O sistema deverá permitir:

- CRUD completo de projetos
- Cálculo dinâmico de classificação de risco
- Controle de transição de status com fluxo fixo
- Restrição de exclusão conforme status do projeto
- Integração com API REST externa mockada para criação e consulta de membros
- Associação de membros aos projetos com regras de elegibilidade e limite de alocação
- Geração de relatório resumido do portfólio
- Paginação e filtros na listagem de projetos
- Segurança básica para autenticação e autorização
- Documentação interativa da API

## Regras de Negócio

### Projeto

Cada projeto deverá conter os seguintes campos:

- Nome
- Data de início
- Previsão de término
- Data real de término
- Orçamento total
- Descrição
- Gerente responsável
- Status atual

### Classificação de risco

A classificação de risco será calculada dinamicamente com base em orçamento e prazo:

- Baixo risco: orçamento até `R$ 100.000` e prazo menor ou igual a `3 meses`
- Médio risco: orçamento entre `R$ 100.001` e `R$ 500.000` ou prazo entre `3 e 6 meses`
- Alto risco: orçamento acima de `R$ 500.000` ou prazo superior a `6 meses`

### Fluxo de status

Os status possíveis serão fixos e deverão respeitar a seguinte sequência:

`em análise -> análise realizada -> análise aprovada -> iniciado -> planejado -> em andamento -> encerrado`

Regra adicional:

- `cancelado` pode ser aplicado a qualquer momento
- Não será permitido pular etapas na transição de status

### Exclusão de projetos

Projetos com status abaixo não poderão ser excluídos:

- `iniciado`
- `em andamento`
- `encerrado`

### Membros

O cadastro de membros não será realizado diretamente no sistema principal.

Será disponibilizada uma API REST externa mockada para:

- Criar membros
- Consultar membros

Cada membro terá pelo menos:

- Nome
- Atribuição

Regras para associação em projetos:

- Apenas membros com atribuição `funcionário` poderão ser associados
- Cada projeto deverá ter no mínimo `1` e no máximo `10` membros
- Um membro não poderá estar alocado em mais de `3` projetos simultaneamente com status diferente de `encerrado` ou `cancelado`

### Relatório do portfólio

Deverá existir um endpoint de resumo com:

- Quantidade de projetos por status
- Total orçado por status
- Média de duração dos projetos encerrados
- Total de membros únicos alocados

## Arquitetura do Repositório

Estrutura planejada:

```text
.
├── backend/
│   ├── src/main/java/...
│   ├── src/main/resources/
│   ├── src/test/java/...
│   └── pom.xml
├── frontend/
│   ├── app/
│   ├── public/
│   └── package.json
└── README.md
```

## Como executar o projeto

### Pré-requisitos

- Java `17`
- Maven `3.9+`
- Node.js `24+`
- Docker e Docker Compose

### Subindo o banco de dados

Na raiz do repositório:

```bash
docker compose up -d postgres
```

O PostgreSQL ficará disponível com as seguintes credenciais locais:

- Banco: `portfolio_db`
- Usuário: `postgres`
- Senha: `admin`

### Executando o backend

```bash
cd backend
mvn spring-boot:run
```

A API ficará disponível em:

- `http://localhost:8080`
- Health check: `http://localhost:8080/api/health`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- API mockada de membros: `http://localhost:8080/mock-api/members`

Credenciais iniciais da autenticação básica:

- Usuário: `admin`
- Senha: `admin123`

### Executando o frontend

```bash
cd frontend
npm install
npm run dev
```

O frontend ficará disponível em:

- `http://localhost:3000`

### Executando os dois projetos a partir da raiz

Depois de instalar as dependências do frontend e da raiz:

```bash
npm install
npm run install:all
npm run dev
```

Scripts disponíveis na raiz:

- `npm run dev`: sobe backend e frontend em paralelo
- `npm run dev:back`: sobe apenas o backend
- `npm run dev:front`: sobe apenas o frontend
- `npm run install:all`: instala dependências do frontend

## Stack Tecnológica

### Backend

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Spring Security
- Spring Validation
- Spring OAuth2 Client
- JWT
- Spring Mail
- Thymeleaf
- Swagger / OpenAPI
- PostgreSQL
- Lombok
- JUnit e Spring Security Test
- H2 para testes

### Frontend

- React
- Next.js
- TypeScript
- shadcn/ui
- Integração com a API REST do backend

## Dependências previstas no backend

Dependências já definidas para a aplicação Spring Boot:

- `spring-boot-starter-data-jpa`
- `spring-boot-starter-oauth2-client`
- `spring-boot-starter-security`
- `spring-boot-starter-validation`
- `spring-boot-starter-web`
- `spring-boot-starter-mail`
- `spring-boot-starter-thymeleaf`
- `io.jsonwebtoken:jjwt-api`
- `io.jsonwebtoken:jjwt-impl`
- `io.jsonwebtoken:jjwt-jackson`
- `org.springdoc:springdoc-openapi-starter-webmvc-api`
- `org.postgresql:postgresql`
- `org.projectlombok:lombok`
- `spring-boot-starter-test`
- `spring-security-test`
- `com.h2database:h2`

## Diretrizes de Implementação

O backend deverá seguir:

- Arquitetura MVC
- Separação clara entre camadas `controller`, `service` e `repository`
- Uso de DTOs e mapeamento dedicado entre camadas
- Tratamento global de exceções
- Clean Code e princípios SOLID
- Persistência com JPA + Hibernate
- Segurança básica com Spring Security
- Testes unitários com cobertura mínima de `70%` nas regras de negócio

## Proposta de módulos do backend

Módulos centrais previstos:

- `project`: gestão de projetos
- `member`: integração com serviço externo de membros
- `allocation`: associação de membros aos projetos
- `portfolio-report`: consolidação de indicadores
- `security`: autenticação e autorização

## Proposta de telas do frontend

Telas iniciais previstas:

- Dashboard do portfólio
- Listagem de projetos com filtros e paginação
- Cadastro e edição de projeto
- Detalhe do projeto
- Gestão de membros associados
- Visualização do relatório resumido
- Login

## Critérios de Qualidade

Os principais critérios considerados neste projeto são:

- Clareza arquitetural
- Consistência das regras de negócio
- Legibilidade do código
- Testabilidade
- Documentação da API
- Segurança básica funcional
- Boa experiência de uso no frontend

## Roadmap Inicial

1. Estruturar o monorepo com `backend` e `frontend`
2. Configurar o backend com Spring Boot, PostgreSQL, segurança e documentação OpenAPI
3. Modelar entidades, DTOs, regras de negócio e fluxo de status
4. Implementar API mockada ou mecanismo de simulação para membros
5. Criar interface no frontend para operação do portfólio
6. Adicionar testes unitários e refinamentos finais

## Observações

- O projeto evitará qualquer uso do nome proibido no enunciado em arquivos, pastas, código ou documentação.
- O foco inicial deste repositório é consolidar uma base sólida de arquitetura e documentação antes da implementação.
- Esta base inicial contém um endpoint simples de verificação no backend e uma página inicial no frontend para validar a integração local.
- O backend libera sem autenticação apenas os endpoints de health check e Swagger nesta etapa inicial.
