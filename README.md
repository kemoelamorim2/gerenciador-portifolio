# Gerenciador de Portfólio

Sistema para gerenciamento do portfólio de projetos de uma empresa, com backend em Spring Boot e frontend em Next.js.

## Visão Geral

Este repositório está organizado como monorepo com duas aplicações:

- `backend/`: API REST em Java com Spring Boot
- `frontend/`: interface web em React com Next.js

O projeto foi desenvolvido para atender um desafio técnico de gestão de portfólio de projetos, cobrindo regras de negócio de ciclo de vida, risco, equipe, orçamento e consolidação de indicadores.

## Funcionalidades Implementadas

- cadastro, consulta, atualização e exclusão de projetos
- cálculo dinâmico de risco com base em orçamento e prazo
- controle de fluxo de status com validação de sequência
- bloqueio de exclusão para projetos em estados não permitidos
- integração com API REST mockada de membros
- alocação de membros em projetos com validações de capacidade e elegibilidade
- relatório resumido do portfólio
- paginação e filtros na listagem de projetos
- autenticação básica no backend
- documentação interativa da API
- interface web para operação do fluxo principal

## Arquitetura

### Backend

- arquitetura MVC
- separação entre `controller`, `service`, `repository`, `dto` e `entity`
- persistência com JPA + Hibernate
- tratamento global de exceções
- autenticação com Spring Security
- documentação OpenAPI
- testes automatizados com JUnit e Spring Test

### Frontend

- Next.js com App Router
- componentes reutilizáveis com `shadcn/ui`
- camada de integração HTTP isolada com `axios`
- hooks para consumo dos módulos da API
- autenticação básica consumindo o backend
- suporte a tema claro e escuro

## Escopo Principal

O sistema foi construído com foco em:

- CRUD de projetos
- cálculo dinâmico de risco
- fluxo controlado de status
- alocação de membros em projetos
- relatório resumido do portfólio
- autenticação básica
- documentação da API

## Estrutura do Repositório

```text
.
├── backend/
│   ├── src/main/java/
│   ├── src/main/resources/
│   ├── src/test/java/
│   └── pom.xml
├── frontend/
│   ├── app/
│   ├── components/
│   ├── hooks/
│   ├── lib/
│   ├── public/
│   └── package.json
├── docker-compose.yml
└── README.md
```

## Tecnologias

### Backend

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Spring Security
- PostgreSQL
- Spring Validation
- OpenAPI
- JUnit
- H2 para testes

### Frontend

- React
- Next.js
- TypeScript
- shadcn/ui
- Axios

## Pré-requisitos

Antes de rodar o projeto localmente, você precisa ter instalado:

- Java `17`
- Maven `3.9+`
- Node.js `24+`
- npm
- Docker
- Docker Compose

## Configuração Local

O fluxo recomendado para executar localmente é:

1. subir o container do PostgreSQL
2. confirmar que o banco está disponível
3. subir o backend
4. subir o frontend

## 1. Subindo o Docker

Na raiz do projeto, rode:

```bash
docker compose up -d postgres
```

Esse comando sobe apenas o banco PostgreSQL definido no [docker-compose.yml](/home/kemoel/projects/gerenciador-portifolio/docker-compose.yml).

Para verificar se o container está ativo:

```bash
docker compose ps
```

Se quiser ver os logs do banco:

```bash
docker compose logs -f postgres
```

## 2. Banco de Dados

O backend usa PostgreSQL local com estas credenciais:

- banco: `portfolio_db`
- usuário: `postgres`
- senha: `admin`
- porta: `5432`

Essas configurações estão alinhadas com:

- [docker-compose.yml](/home/kemoel/projects/gerenciador-portifolio/docker-compose.yml)
- [application.yaml](/home/kemoel/projects/gerenciador-portifolio/backend/src/main/resources/application.yaml)

Se for a primeira vez ou se houver conflito com volume antigo, você pode recriar o banco:

```bash
docker compose down -v
docker compose up -d postgres
```

Use esse comando com cuidado, porque ele remove o volume do banco local do projeto.

## 3. Subindo o Backend

Entre na pasta do backend:

```bash
cd backend
```

Rode a aplicação:

```bash
mvn spring-boot:run
```

Se quiser garantir recompilação limpa antes:

```bash
mvn clean spring-boot:run
```

### URLs do backend

Depois que o backend subir, ele ficará disponível em:

- API principal: `http://localhost:8080`
- health check: `http://localhost:8080/api/health`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- API mockada de membros: `http://localhost:8080/mock-api/members`

### Autenticação do backend

A API usa autenticação básica.

Credenciais locais:

- usuário: `admin`
- senha: `admin123`

## 4. Subindo o Frontend

Em outro terminal, entre na pasta do frontend:

```bash
cd frontend
```

Instale as dependências:

```bash
npm install
```

Suba a aplicação:

```bash
npm run dev
```

O frontend ficará disponível em:

- `http://localhost:3000`

## 5. Ordem Recomendada de Execução

Se quiser subir tudo sem erro de dependência entre serviços, siga exatamente esta sequência:

1. iniciar o Docker

```bash
docker compose up -d postgres
```

2. verificar se o banco está ativo

```bash
docker compose ps
```

3. subir o backend

```bash
cd backend
mvn spring-boot:run
```

4. abrir outro terminal e subir o frontend

```bash
cd frontend
npm install
npm run dev
```

5. acessar no navegador

- frontend: `http://localhost:3000`
- swagger: `http://localhost:8080/swagger-ui/index.html`

## 6. Rodando a API Mockada de Membros

O sistema principal não deve cadastrar membros diretamente pela interface principal.

Para atender o requisito do desafio, existe uma API REST mockada dedicada em:

- `GET /mock-api/members`
- `GET /mock-api/members/{id}`
- `POST /mock-api/members`

Além disso, o backend já sobe com uma carga inicial de membros mockados via:

- [MockMemberDataSeeder.java](/home/kemoel/projects/gerenciador-portifolio/backend/src/main/java/com/portfolio/manager/member/config/MockMemberDataSeeder.java)

Isso permite usar o sistema sem precisar cadastrar manualmente membros logo no primeiro uso.

## 7. Dados Iniciais

Quando a tabela `members` está vazia, o backend cria automaticamente membros iniciais como:

- `Ana Martins` / `funcionario`
- `Carlos Lima` / `funcionario`
- `Juliana Rocha` / `funcionario`
- `Pedro Henrique` / `funcionario`
- `Larissa Gomes` / `funcionario`
- `Rafael Costa` / `funcionario`
- `Marina Alves` / `gerente`
- `Bruno Ferreira` / `analista`
- `Camila Nunes` / `coordenador`
- `Felipe Barros` / `designer`
- `Patricia Melo` / `qa`
- `Thiago Ribeiro` / `tech lead`

## 8. Comandos Úteis

### Backend

Compilar:

```bash
cd backend
mvn clean compile
```

Rodar testes:

```bash
cd backend
mvn test
```

### Frontend

Build de produção:

```bash
cd frontend
npm run build
```

## 9. Possíveis Problemas Locais

### Porta 5432 em uso

Se você já tiver outro PostgreSQL rodando localmente, o Docker pode não conseguir subir na mesma porta.

Nesse caso:

- pare a instância local conflitante
- ou ajuste a porta no [docker-compose.yml](/home/kemoel/projects/gerenciador-portifolio/docker-compose.yml)

### Erro de autenticação no banco

Se o backend reclamar de usuário ou senha incorretos:

```bash
docker compose down -v
docker compose up -d postgres
```

### Frontend sem acessar a API

Verifique se:

- o backend está rodando na porta `8080`
- o frontend está rodando na porta `3000`
- a autenticação foi feita com `admin/admin123`

## 10. Fluxo de Demonstração

Uma sequência simples para demonstrar o projeto:

1. abrir o frontend
2. fazer login com `admin / admin123`
3. listar projetos
4. criar um novo projeto
5. visualizar detalhe do projeto
6. consultar membros mockados
7. alocar membros ao projeto
8. consultar o relatório do portfólio

## 11. Observações

- a interface principal consome a API mockada de membros, mas não expõe o cadastro direto como fluxo de negócio principal
- os membros iniciais são carregados automaticamente para facilitar os testes locais
- a exclusão de projetos respeita as regras de status e remove alocações vinculadas antes do delete quando permitido
