# Levantamento de Ajustes do Projeto

Este documento consolida a análise do estado atual do projeto em relação ao desafio técnico, com foco em:

- aderência aos requisitos funcionais e não funcionais
- gaps do backend
- gaps do frontend
- ordem recomendada de implementação dos ajustes

## 1. Observação sobre a fonte dos requisitos

O PDF enviado não pôde ser extraído automaticamente neste ambiente por indisponibilidade de ferramentas locais de leitura de PDF.

Para a análise abaixo, foi usado o conjunto de requisitos já refletido no [README.md](README.md), que está alinhado com o enunciado do desafio compartilhado anteriormente.

## 2. Diagnóstico geral

### Backend

O backend está funcional e cobre boa parte do desafio:

- CRUD de projetos implementado
- cálculo de risco implementado
- transição de status implementada
- regras de exclusão implementadas
- membros implementados
- alocação de membros implementada
- relatório resumido implementado
- paginação e filtros implementados
- segurança básica implementada
- tratamento global de exceções implementado
- documentação OpenAPI implementada

Mesmo assim, há ajustes importantes para que a entrega fique realmente aderente ao que foi pedido.

### Frontend

O frontend já prova a integração com a API e cobre os fluxos principais de login e projetos, mas ainda não entrega completamente a proposta funcional do projeto.

Os maiores gaps estão em:

- gestão de membros como fluxo próprio
- filtros e paginação visíveis para o usuário
- tela dedicada de relatório
- melhor aderência às regras de negócio da API

## 3. O que já está aderente

### Backend

- arquitetura com separação entre `controller`, `service`, `repository`, `dto` e `exception`
- persistência com JPA/Hibernate
- uso de PostgreSQL na configuração principal
- DTOs separados para entrada e saída
- tratamento global de exceções com payload padronizado
- autenticação básica com Spring Security
- paginação e filtros para listagem de projetos
- testes automatizados criados para controller, service, DTO, repository e exception handler
- relatório resumido do portfólio

### Frontend

- login com persistência de credenciais
- dashboard inicial
- listagem de projetos
- criação de projeto
- edição de projeto
- detalhe de projeto
- associação e remoção de membros no projeto
- design system inicial com tema claro/escuro

## 4. Gaps do backend

### 4.1. API de membros ainda não está realmente tratada como integração externa mockada

Hoje o projeto expõe [MemberController.java](backend/src/main/java/com/portfolio/manager/member/controller/MemberController.java) dentro da mesma aplicação principal.

Isso atende parcialmente ao requisito, mas não reproduz exatamente a ideia de:

- API REST externa mockada
- consumo por integração

#### Ajuste recomendado

- criar uma abstração de integração, por exemplo `MemberClient`
- manter a implementação mockada local inicialmente
- separar a lógica de integração da lógica de domínio

### 4.2. Regra de no mínimo 1 membro por projeto está apenas parcialmente atendida

Hoje a remoção impede que o projeto fique com menos de 1 membro em [ProjectMemberAllocationService.java](backend/src/main/java/com/portfolio/manager/allocation/service/ProjectMemberAllocationService.java), mas:

- um projeto pode ser criado sem nenhum membro
- um projeto pode permanecer sem membros indefinidamente

#### Ajuste recomendado

- definir em que momento a regra do mínimo deve ser exigida
- opção mais segura: impedir avanço para certos status sem pelo menos 1 membro
- opção mais rígida: exigir membro logo após criação do projeto

### 4.3. Regra de atribuição tem inconsistência com acentuação

No backend, a regra aceita apenas `funcionario` sem acento em [ProjectMemberAllocationService.java](backend/src/main/java/com/portfolio/manager/allocation/service/ProjectMemberAllocationService.java).

O enunciado fala em `funcionário`.

#### Ajuste recomendado

- normalizar a comparação para aceitar com e sem acento
- idealmente transformar atribuição em enum ou constante central

### 4.4. OpenAPI está inconsistente com a segurança real da aplicação

Em [OpenApiConfig.java](backend/src/main/java/com/portfolio/manager/config/OpenApiConfig.java), a documentação declara:

- JWT
- API Key

Mas a aplicação real usa:

- HTTP Basic

Além disso, a descrição da API está genérica e fala de pagamentos e multi-tenant, o que não corresponde ao projeto.

#### Ajuste recomendado

- documentar `Basic Auth` corretamente
- alinhar título, descrição, contato e escopo da API ao contexto de gestão de portfólio

### 4.5. Suite de testes está quebrando no ambiente atual

Ao rodar `mvn test`, os testes falham por inicialização do Mockito no ambiente atual.

Erro principal encontrado:

- `Could not initialize inline Byte Buddy mock maker`

Isso afeta diretamente a confiabilidade da cobertura.

#### Ajuste recomendado

- substituir o mock maker inline por estratégia compatível com o ambiente
- revisar configuração de Mockito/Surefire
- garantir que `mvn test` e `mvn verify` fechem com sucesso

### 4.6. Padrão de mapeamento precisava ser consolidado

O projeto havia sido planejado com `ModelMapper`, mas já foi consolidado para mapeamento manual dedicado em [ProjectMapper.java](backend/src/main/java/com/portfolio/manager/project/mapper/ProjectMapper.java).

Esse ponto já foi ajustado, e o caminho adotado agora é:

- manter mapper manual e explícito
- evitar dependência desnecessária

### 4.7. Documentação e configuração do projeto estão desatualizadas

Há divergências entre o código e o [README.md](README.md):

- rota do Swagger
- dependência do SpringDoc
- credenciais e configuração do banco
- menção a `Health` mesmo sem endpoint ativo no código atual

#### Ajuste recomendado

- alinhar README com o estado real do projeto
- documentar claramente como subir backend, frontend, banco e Swagger

## 5. Gaps do frontend

### 5.1. Não existe módulo de membros como fluxo próprio

O frontend consome membros apenas como apoio ao projeto.

Hoje não há:

- tela de listagem de membros
- tela de cadastro de membros

Isso gera um problema prático:

- para criar projeto, é preciso existir gerente
- mas o usuário não tem uma tela clara para criar esse gerente

#### Ajuste recomendado

- criar módulo `members`
- listar membros
- cadastrar membro
- permitir uso posterior no projeto e nas alocações

### 5.2. Listagem de projetos ainda não entrega filtros e paginação reais na interface

O backend já suporta filtros e paginação, mas a tela em [frontend/app/projects/page.tsx](frontend/app/projects/page.tsx):

- sempre busca `page=0`
- sempre usa `size=20`
- não mostra filtros
- não mostra controles de paginação

#### Ajuste recomendado

- criar barra de filtros
- criar paginação visual
- sincronizar estado com query string

### 5.3. Não existe tela dedicada para relatório resumido

Hoje o dashboard mostra um recorte, mas não existe uma página própria para:

- quantidade por status
- orçamento por status
- duração média
- total de membros únicos

#### Ajuste recomendado

- criar rota própria de relatório
- transformar o dashboard em entrada executiva
- deixar o relatório com leitura detalhada

### 5.4. Tela de detalhes do projeto força ações sem guiar bem o fluxo permitido

Em [frontend/app/projects/[id]/page.tsx](frontend/app/projects/[id]/page.tsx), todos os botões de status ficam visíveis.

O backend protege a regra, mas a UX ainda:

- incentiva tentativa e erro
- não mostra claramente o próximo status válido

#### Ajuste recomendado

- exibir apenas transições permitidas
- manter `cancelado` como ação especial
- mostrar mensagens vindas da API quando a regra bloquear

### 5.5. Regra de atribuição está inconsistente no frontend

Em [ProjectMembers.tsx](frontend/components/projects/ProjectMembers.tsx), o filtro local procura `funcionário` com acento.

O backend valida `funcionario` sem acento.

#### Ajuste recomendado

- normalizar a atribuição no frontend
- idealmente usar valor padronizado vindo do backend

### 5.6. Falta parser central de erros da API

Hoje vários fluxos ainda mostram mensagens genéricas em `toast`.

#### Ajuste recomendado

- criar utilitário central para converter `ApiErrorResponse`
- reaproveitar esse parser em projetos, membros e alocação

## 6. Ajustes prioritários no backend

### Prioridade 1

- corrigir a configuração de testes para que `mvn test` volte a passar
- alinhar OpenAPI com a segurança real
- alinhar README e configuração real do projeto

### Prioridade 2

- formalizar melhor a integração mockada de membros
- normalizar a regra de atribuição `funcionario/funcionário`
- fechar a regra de mínimo de 1 membro por projeto

### Prioridade 3

- limpar dependências/configurações não utilizadas

## 7. Ajustes prioritários no frontend

### Prioridade 1

- criar módulo de membros com listagem e cadastro
- ajustar fluxos de projeto para depender de membros reais

### Prioridade 2

- implementar filtros e paginação visíveis na tela de projetos
- melhorar a UX da transição de status no detalhe do projeto

### Prioridade 3

- criar página dedicada de relatório
- centralizar o tratamento de erros da API
- refinar mensagens e estados vazios

## 8. Passos recomendados de implementação

### Etapa 1. Estabilização do backend

- corrigir problema do Mockito nos testes
- validar `mvn test`
- validar `mvn verify`
- revisar cobertura real do JaCoCo após a estabilização

### Etapa 2. Alinhamento de contrato e documentação

- corrigir [OpenApiConfig.java](backend/src/main/java/com/portfolio/manager/config/OpenApiConfig.java)
- atualizar [README.md](README.md)
- revisar exemplos de request/response

### Etapa 3. Fechamento das regras de negócio pendentes

- normalizar atribuição de membro
- fechar regra do mínimo de membros por projeto
- separar melhor a integração mockada de membros

### Etapa 4. Fechamento funcional do frontend

- criar telas de membros
- conectar seleção de gerente e alocação ao módulo de membros
- implementar filtros e paginação em projetos
- implementar relatório dedicado

### Etapa 5. Refinamento de UX

- parser central de erro
- mensagens mais específicas
- estados de carregamento e vazio
- regras de status refletidas visualmente na UI

## 9. Conclusão

O projeto já tem uma base boa e cobre boa parte do desafio, especialmente no backend.

Os maiores desvios hoje não estão na ausência total de funcionalidade, mas em:

- aderência exata a algumas regras do enunciado
- estabilidade da suite de testes
- completude do frontend para operação real
- consistência entre documentação e código

Se esses ajustes forem executados na ordem acima, o projeto fica muito mais sólido para entrega técnica e apresentação.
