# Plano de Implementação: API REST de Controle de Despesas Pessoais

## Overview

Refatoração do projeto para arquitetura hexagonal (Ports & Adapters) com Kotlin + Spring Boot 3.x, MongoDB, Kafka e JWT HS256. As tarefas seguem a ordem de dependência: estrutura base → domínio → casos de uso → adapters → integração.

## Tasks

- [x] 1. Reestruturar o projeto para arquitetura hexagonal
  - Criar os pacotes `domain/model`, `domain/port/inbound`, `domain/port/outbound`, `domain/exception`, `application/usecase`, `adapters/inbound/rest`, `adapters/outbound/persistence`, `adapters/outbound/messaging`, `adapters/outbound/banking`
  - Remover ou mover arquivos existentes que não seguem a estrutura hexagonal (controllers, services, repositories atuais)
  - Atualizar `build.gradle.kts` com as dependências: `kotest-runner-junit5:5.9.1`, `kotest-property:5.9.1`, `mockk:1.13.10`, `de.flapdoodle.embed.mongo.spring30x:4.12.0`
  - _Requirements: 10.1_

- [x] 2. Implementar modelos de domínio e exceções
  - [x] 2.1 Criar entidades de domínio `User` e `Expense` com anotações MongoDB (`@Document`, `@Id`, `@Indexed`)
    - `User`: id, email (unique), passwordHash, categories, expenseLimit
    - `Expense`: id, userId, amount, category, date, description, source (MANUAL/IMPORTED), externalId
    - Criar enum `ExpenseSource`
    - _Requirements: 1.4, 3.1, 7.2, 10.2, 10.3_
  - [x] 2.2 Criar DTOs de request/response: `RegisterRequest`, `LoginRequest`, `TokenResponse`, `CreateExpenseRequest`, `ExpenseResponse`, `CreateCategoryRequest`, `CategoryResponse`, `ImportResult`
    - Adicionar anotações de validação Bean Validation (`@NotBlank`, `@Email`, `@Size`, `@Positive`) nos campos obrigatórios
    - _Requirements: 1.3, 3.4_
  - [x] 2.3 Criar payloads de eventos Kafka: `ExpenseCreatedEvent`, `ExpenseHighAlertEvent`, `BankTransactionsImportedEvent`
    - _Requirements: 8.1, 8.2, 8.3_
  - [x] 2.4 Criar exceções de domínio: `DuplicateEmailException`, `InvalidInputException`, `InvalidCredentialsException`, `UnauthorizedException`, `ForbiddenException`, `NotFoundException`, `DuplicateCategoryException`, `BankApiException`, `DatabaseException`
    - _Requirements: 1.2, 1.3, 2.2, 5.2, 5.3, 6.2, 7.4, 10.4_
  - [x] 2.5 Escrever testes de propriedade para os modelos de domínio
    - **Property 24: Campos obrigatórios presentes em toda Despesa persistida**
    - **Validates: Requirements 10.2**

- [x] 3. Definir interfaces de portas (Ports)
  - [x] 3.1 Criar inbound ports (interfaces de casos de uso): `RegisterUserUseCase`, `AuthenticateUserUseCase`, `CreateExpenseUseCase`, `ListExpensesUseCase`, `GetExpenseByIdUseCase`, `CreateCategoryUseCase`, `ListCategoriesUseCase`, `ImportBankTransactionsUseCase`
    - _Requirements: 1.1, 2.1, 3.1, 4.1, 5.1, 6.1, 7.1_
  - [x] 3.2 Criar outbound ports (interfaces de repositórios e serviços externos): `UserRepository`, `ExpenseRepository`, `CategoryRepository`, `EventPublisher`, `BankApiClient`
    - _Requirements: 1.4, 3.2, 3.3, 7.1, 8.1, 8.2, 8.3_

- [x] 4. Implementar casos de uso de autenticação
  - [x] 4.1 Implementar `RegisterUserUseCaseImpl`
    - Validar unicidade de e-mail via `UserRepository.existsByEmail`; lançar `DuplicateEmailException` se duplicado
    - Fazer hash da senha com BCrypt (fator de custo 10) antes de persistir
    - Persistir via `UserRepository.save`
    - _Requirements: 1.1, 1.2, 1.4, 9.4_
  - [x] 4.2 Escrever testes de propriedade para `RegisterUserUseCase`
    - **Property 1: Registro armazena senha como hash bcrypt válido**
    - **Validates: Requirements 1.1, 1.4, 9.4**
  - [x] 4.3 Escrever testes de propriedade para `RegisterUserUseCase`
    - **Property 2: E-mail duplicado causa conflito**
    - **Validates: Requirements 1.2, 10.3**
  - [x] 4.4 Escrever testes de propriedade para `RegisterUserUseCase`
    - **Property 3: Validação de entrada no registro**
    - **Validates: Requirements 1.3**
  - [x] 4.5 Implementar `AuthenticateUserUseCaseImpl`
    - Buscar usuário por e-mail; lançar `InvalidCredentialsException` se não encontrado ou senha incorreta
    - Gerar JWT HS256 com `userId` no payload e expiração de 24h
    - _Requirements: 2.1, 2.2, 2.3_
  - [x] 4.6 Escrever testes de propriedade para `AuthenticateUserUseCase`
    - **Property 4: Login retorna JWT com userId e expiração correta**
    - **Validates: Requirements 2.1, 2.3**
  - [x] 4.7 Escrever testes de propriedade para `AuthenticateUserUseCase`
    - **Property 5: Login com credenciais inválidas é rejeitado**
    - **Validates: Requirements 2.2**

- [-] 5. Implementar infraestrutura de segurança JWT
  - [x] 5.1 Implementar `JwtUtil` para geração e validação de tokens JWT HS256 com expiração de 24h
    - _Requirements: 2.3, 2.4, 2.5, 9.5_
  - [x] 5.2 Implementar `JwtAuthenticationFilter` para interceptar requisições, extrair e validar o token do header `Authorization: Bearer`, e injetar `userId` no `SecurityContext`
    - Lançar `UnauthorizedException` para token ausente, inválido ou expirado
    - _Requirements: 2.4, 2.5, 9.1_
  - [x] 5.3 Configurar `SecurityConfig` para liberar `/auth/register` e `/auth/login` e proteger todos os demais endpoints
    - _Requirements: 2.4, 9.1_
  - [x] 5.4 Escrever testes de propriedade para o filtro JWT
    - **Property 6: Apenas JWT válido concede acesso a endpoints protegidos**
    - **Validates: Requirements 2.4, 2.5, 9.1**

- [x] 6. Checkpoint — Garantir que todos os testes passem
  - Garantir que todos os testes passem; perguntar ao usuário se houver dúvidas.

- [x] 7. Implementar casos de uso de despesas
  - [x] 7.1 Implementar `CreateExpenseUseCaseImpl`
    - Validar `amount > 0` e campos obrigatórios; lançar `InvalidInputException` se inválido
    - Persistir despesa com `source = MANUAL` via `ExpenseRepository.save`
    - Publicar `ExpenseCreatedEvent` via `EventPublisher.publishExpenseCreated` (capturar exceção Kafka com try/catch e logar com ERROR)
    - Verificar `amount > user.expenseLimit` e publicar `ExpenseHighAlertEvent` se aplicável
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 8.1, 8.2, 8.4_
  - [x] 7.2 Escrever testes de propriedade para `CreateExpenseUseCase`
    - **Property 7: Criação de despesa persiste com source=MANUAL**
    - **Validates: Requirements 3.1**
  - [x] 7.3 Escrever testes de propriedade para `CreateExpenseUseCase`
    - **Property 8: Evento expense.created publicado com dados corretos**
    - **Validates: Requirements 3.2, 8.1**
  - [ ]* 7.4 Escrever testes de propriedade para `CreateExpenseUseCase`
    - **Property 9: Alerta de gasto alto publicado somente quando valor supera limite**
    - **Validates: Requirements 3.3, 8.2**
  - [ ]* 7.5 Escrever testes de propriedade para `CreateExpenseUseCase`
    - **Property 10: Validação de entrada na criação de despesa**
    - **Validates: Requirements 3.4**
  - [ ]* 7.6 Escrever testes de propriedade para `CreateExpenseUseCase`
    - **Property 22: Falha no Kafka não interrompe o fluxo REST**
    - **Validates: Requirements 8.4**
  - [x] 7.7 Implementar `ListExpensesUseCaseImpl`
    - Buscar despesas por `userId` via `ExpenseRepository.findByUserId`
    - Ordenar resultado por data decrescente
    - _Requirements: 4.1, 4.2, 4.3, 9.2_
  - [ ]* 7.8 Escrever testes de propriedade para `ListExpensesUseCase`
    - **Property 11: Isolamento de despesas por usuário na listagem**
    - **Validates: Requirements 4.1, 9.2**
  - [ ]* 7.9 Escrever testes de propriedade para `ListExpensesUseCase`
    - **Property 12: Despesas listadas em ordem decrescente de data**
    - **Validates: Requirements 4.2**
  - [x] 7.10 Implementar `GetExpenseByIdUseCaseImpl`
    - Buscar via `ExpenseRepository.findByIdAndUserId`; lançar `NotFoundException` se não encontrado, `ForbiddenException` se pertencer a outro usuário
    - _Requirements: 5.1, 5.2, 5.3, 9.2_
  - [ ]* 7.11 Escrever testes de propriedade para `GetExpenseByIdUseCase`
    - **Property 13: Round trip de consulta de despesa por id**
    - **Validates: Requirements 5.1**
  - [ ]* 7.12 Escrever testes de propriedade para `GetExpenseByIdUseCase`
    - **Property 14: Acesso a despesa de outro usuário é negado**
    - **Validates: Requirements 5.3**

- [ ] 8. Implementar casos de uso de categorias
  - [ ] 8.1 Implementar `CreateCategoryUseCaseImpl`
    - Verificar duplicata via `CategoryRepository.existsByUserIdAndName`; lançar `DuplicateCategoryException` se existir
    - Persistir via `CategoryRepository.addCategory`
    - _Requirements: 6.1, 6.2_
  - [ ]* 8.2 Escrever testes de propriedade para `CreateCategoryUseCase`
    - **Property 15: Criação de categoria e round trip de listagem**
    - **Validates: Requirements 6.1**
  - [ ]* 8.3 Escrever testes de propriedade para `CreateCategoryUseCase`
    - **Property 16: Categoria duplicada causa conflito**
    - **Validates: Requirements 6.2**
  - [ ] 8.4 Implementar `ListCategoriesUseCaseImpl`
    - Buscar categorias por `userId` via `CategoryRepository.findByUserId`
    - _Requirements: 6.3, 6.4, 9.3_
  - [ ]* 8.5 Escrever testes de propriedade para `ListCategoriesUseCase`
    - **Property 17: Isolamento de categorias por usuário**
    - **Validates: Requirements 6.3, 9.3**

- [ ] 9. Checkpoint — Garantir que todos os testes passem
  - Garantir que todos os testes passem; perguntar ao usuário se houver dúvidas.

- [ ] 10. Implementar adapter de persistência MongoDB
  - [ ] 10.1 Implementar `UserRepositoryAdapter` usando Spring Data MongoDB (`MongoRepository`), implementando a interface `UserRepository` do domínio
    - Capturar exceções de infraestrutura e relançar como `DatabaseException`
    - _Requirements: 1.4, 10.1, 10.3, 10.4_
  - [ ] 10.2 Implementar `ExpenseRepositoryAdapter` usando Spring Data MongoDB, implementando a interface `ExpenseRepository` do domínio
    - Incluir método `findByUserIdOrderByDateDesc` para ordenação
    - Capturar exceções de infraestrutura e relançar como `DatabaseException`
    - _Requirements: 3.1, 4.2, 10.1, 10.4_
  - [ ] 10.3 Implementar `CategoryRepositoryAdapter` para gerenciar a lista de categorias embutida no documento `User`
    - _Requirements: 6.1, 6.3, 10.1_
  - [ ]* 10.4 Escrever testes de propriedade para persistência
    - **Property 24: Campos obrigatórios presentes em toda Despesa persistida**
    - **Validates: Requirements 10.2**
  - [ ]* 10.5 Escrever testes de propriedade para persistência
    - **Property 25: Falha no MongoDB retorna HTTP 500**
    - **Validates: Requirements 10.4**

- [ ] 11. Implementar adapter de mensageria Kafka
  - [ ] 11.1 Implementar `KafkaEventPublisher` implementando a interface `EventPublisher` do domínio
    - Publicar nos tópicos `expense.created`, `alert.expense.high` e `bank.transactions.imported`
    - Configurar `KafkaProducerConfig` com serialização JSON
    - _Requirements: 8.1, 8.2, 8.3_
  - [ ] 11.2 Implementar `KafkaConsumer` para os tópicos subscritos com lógica de idempotência (verificar se evento já foi processado antes de agir)
    - _Requirements: 8.5_
  - [ ]* 11.3 Escrever testes de propriedade para idempotência do Kafka Consumer
    - **Property 23: Idempotência do Kafka Consumer**
    - **Validates: Requirements 8.5**

- [ ] 12. Implementar adapter de integração bancária
  - [ ] 12.1 Implementar `BankApiClientAdapter` usando `WebClient` para chamar a API bancária externa (Fake_Bank_API)
    - Capturar erros HTTP e de rede; relançar como `BankApiException`
    - _Requirements: 7.1, 7.4_
  - [ ] 12.2 Implementar `ImportBankTransactionsUseCaseImpl`
    - Chamar `BankApiClient.fetchTransactions`
    - Filtrar duplicatas via `ExpenseRepository.existsByExternalIdAndUserId`
    - Persistir transações novas com `source = IMPORTED`
    - Publicar `BankTransactionsImportedEvent` com count das transações novas
    - _Requirements: 7.1, 7.2, 7.3, 7.5, 8.3_
  - [ ]* 12.3 Escrever testes de propriedade para importação bancária
    - **Property 18: Importação persiste transações com source=IMPORTED**
    - **Validates: Requirements 7.2**
  - [ ]* 12.4 Escrever testes de propriedade para importação bancária
    - **Property 19: Deduplicação de transações importadas**
    - **Validates: Requirements 7.5**
  - [ ]* 12.5 Escrever testes de propriedade para importação bancária
    - **Property 20: Evento bank.transactions.imported com count correto**
    - **Validates: Requirements 7.3, 8.3**
  - [ ]* 12.6 Escrever testes de propriedade para importação bancária
    - **Property 21: Falha na API bancária retorna HTTP 502**
    - **Validates: Requirements 7.4**

- [ ] 13. Implementar controllers REST e handler de exceções
  - [ ] 13.1 Implementar `AuthController` com endpoints `POST /auth/register` e `POST /auth/login`, delegando para os respectivos casos de uso
    - _Requirements: 1.1, 1.2, 1.3, 2.1, 2.2_
  - [ ] 13.2 Implementar `ExpenseController` com endpoints `POST /expenses`, `GET /expenses` e `GET /expenses/{id}`, extraindo `userId` do `SecurityContext`
    - _Requirements: 3.1, 3.4, 4.1, 5.1, 5.2, 5.3_
  - [ ] 13.3 Implementar `CategoryController` com endpoints `POST /categories` e `GET /categories`
    - _Requirements: 6.1, 6.2, 6.3, 6.4_
  - [ ] 13.4 Implementar `BankController` com endpoint `GET /bank/import-transactions`
    - _Requirements: 7.1, 7.4_
  - [ ] 13.5 Implementar `GlobalExceptionHandler` (`@ControllerAdvice`) mapeando todas as exceções de domínio para os status HTTP correspondentes com corpo `{ "error": "mensagem" }`
    - _Requirements: 1.2, 1.3, 2.2, 5.2, 5.3, 6.2, 7.4, 10.4_

- [ ] 14. Checkpoint final — Garantir que todos os testes passem
  - Garantir que todos os testes passem; perguntar ao usuário se houver dúvidas.

## Notes

- Tarefas marcadas com `*` são opcionais e podem ser puladas para um MVP mais rápido
- Cada tarefa referencia requisitos específicos para rastreabilidade
- Os checkpoints garantem validação incremental a cada fase
- Testes de propriedade usam Kotest com módulo `kotest-property` (mínimo 100 iterações por propriedade)
- Testes unitários usam JUnit 5 + MockK
- Cada teste de propriedade deve ter o comentário: `// Feature: personal-expense-control, Property N: <título>`
