# Personal Expense Control API

REST API para controle de despesas pessoais com autenticação JWT, integração bancária e eventos assíncronos via Apache Kafka.

## Descrição

O sistema permite que usuários autenticados registrem despesas manualmente ou importem transações de contas bancárias conectadas (Plaid, Fake Bank API ou BCB API). Eventos de negócio são publicados via Kafka para notificações, alertas de gastos e sincronização de dados.

## Tecnologias

- **Kotlin** + **Spring Boot 3.x**
- **MongoDB** — persistência de usuários, despesas e categorias (Spring Data MongoDB)
- **Apache Kafka** — publicação e consumo de eventos assíncronos
- **JWT (HS256)** — autenticação stateless com validade de 24h
- **BCrypt** — hash de senhas com fator de custo mínimo 10
- **Gradle** — build e gerenciamento de dependências
- **Testcontainers** — testes de integração com MongoDB e Kafka
- **Docker Compose** — ambiente local

## Arquitetura

O projeto segue arquitetura hexagonal (Ports & Adapters), separando o domínio das dependências externas.

```
src/
├── main/kotlin/.../expensensecontrol/
│   ├── domain/
│   │   ├── commons/        # Utilitários e mensagens padrão
│   │   ├── exception/      # Exceções de domínio
│   │   ├── model/          # Modelos (User, Expense, Category, enums)
│   │   └── service/        # Lógica de negócio (AuthService, ExpenseService)
│   └── infrastructure/
│       ├── config/         # Configurações (Security, Global)
│       ├── controller/     # Endpoints REST (Auth, Expense)
│       ├── handler/        # Tratamento global de exceções
│       ├── persistence/    # Repositórios MongoDB
│       └── security/       # JWT (filter, token, util)
└── test/kotlin/            # Testes unitários e de integração
```

## Endpoints

| Método | Endpoint                      | Descrição                              | Auth |
|--------|-------------------------------|----------------------------------------|------|
| POST   | `/auth/register`              | Cadastro de usuário                    | Não  |
| POST   | `/auth/login`                 | Login e geração de JWT                 | Não  |
| POST   | `/expenses`                   | Cadastro manual de despesa             | Sim  |
| GET    | `/expenses`                   | Listagem de despesas do usuário        | Sim  |
| GET    | `/expenses/{id}`              | Consulta de despesa por ID             | Sim  |
| POST   | `/categories`                 | Criação de categoria personalizada     | Sim  |
| GET    | `/categories`                 | Listagem de categorias do usuário      | Sim  |
| GET    | `/bank/import-transactions`   | Importação de transações bancárias     | Sim  |

## Eventos Kafka

| Tópico                        | Publicado quando                                              |
|-------------------------------|---------------------------------------------------------------|
| `expense.created`             | Nova despesa persistida (manual ou importada)                 |
| `alert.expense.high`          | Valor da despesa supera o limite de gasto configurado         |
| `bank.transactions.imported`  | Importação bancária concluída com sucesso                     |

## Setup Local

### Pré-requisitos

- Java 21
- Docker e Docker Compose

### Executar

1. Clone o repositório:
   ```bash
   git clone <repository-url>
   cd expensensecontrol
   ```

2. Suba os serviços (MongoDB + Kafka):
   ```bash
   docker-compose up -d
   ```

3. Configure as variáveis de ambiente (veja `local-variables.env`).

4. Execute a aplicação:
   ```bash
   ./gradlew bootRun
   ```

A API estará disponível em `http://localhost:8080`.

## Testes

```bash
./gradlew test
```

Relatórios gerados em `build/reports/tests`.
