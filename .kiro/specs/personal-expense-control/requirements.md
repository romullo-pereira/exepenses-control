# Documento de Requisitos

## Introdução

Este documento descreve os requisitos funcionais e não funcionais da API REST de Controle de Despesas Pessoais. O sistema permite que usuários autenticados registrem despesas manualmente ou importem transações de contas bancárias conectadas. Eventos assíncronos são publicados via Apache Kafka para notificações, alertas de gastos e sincronização de dados. O projeto é implementado em Kotlin com Spring Boot 3.x, seguindo arquitetura hexagonal, com persistência em MongoDB.

---

## Glossário

- **Sistema**: A API REST de Controle de Despesas Pessoais como um todo.
- **Auth_Service**: Componente responsável pelo registro, login e validação de tokens JWT.
- **Expense_Service**: Componente responsável pelo cadastro, listagem e consulta de despesas.
- **Category_Service**: Componente responsável pelo gerenciamento de categorias personalizadas do usuário.
- **Bank_Integration_Service**: Componente responsável pela importação de transações de APIs bancárias externas.
- **Kafka_Producer**: Componente responsável por publicar eventos no Apache Kafka.
- **Kafka_Consumer**: Componente responsável por consumir eventos do Apache Kafka.
- **JWT**: JSON Web Token — token de autenticação stateless utilizado para autorizar requisições.
- **Usuário**: Pessoa física cadastrada no sistema que gerencia suas próprias despesas.
- **Despesa**: Registro financeiro de um gasto realizado pelo Usuário, podendo ser de origem manual ou importada.
- **Categoria**: Classificação personalizada atribuída a uma Despesa pelo Usuário.
- **Transação_Bancária**: Registro financeiro obtido de uma API bancária externa e convertido em Despesa.
- **Plaid**: API externa de conexão com contas bancárias norte-americanas (https://plaid.com/).
- **Fake_Bank_API**: API simulada de extratos e transações para desenvolvimento e testes (https://www.fakebankapi.com/).
- **BCB_API**: API pública do Banco Central do Brasil para dados de câmbio e taxas (https://dadosabertos.bcb.gov.br/).
- **Limite_de_Gasto**: Valor monetário configurado pelo Usuário acima do qual um alerta é disparado.
- **Arquitetura_Hexagonal**: Padrão arquitetural (Ports & Adapters) que separa o domínio das dependências externas.

---

## Requisitos

### Requisito 1: Registro de Usuário

**User Story:** Como um visitante, quero me cadastrar no sistema, para que eu possa acessar as funcionalidades de controle de despesas.

#### Critérios de Aceitação

1. WHEN uma requisição POST é recebida em `/auth/register` com e-mail e senha válidos, THE Auth_Service SHALL criar um novo Usuário com a senha armazenada como hash bcrypt e retornar o status HTTP 201.
2. WHEN uma requisição POST é recebida em `/auth/register` com um e-mail já cadastrado, THE Auth_Service SHALL retornar o status HTTP 409 com uma mensagem de erro descritiva.
3. WHEN uma requisição POST é recebida em `/auth/register` com e-mail em formato inválido ou senha com menos de 8 caracteres, THE Auth_Service SHALL retornar o status HTTP 400 com uma mensagem de validação descritiva.
4. THE Auth_Service SHALL armazenar os dados do Usuário no MongoDB na coleção `users`.

---

### Requisito 2: Autenticação de Usuário

**User Story:** Como um Usuário cadastrado, quero fazer login no sistema, para que eu receba um token JWT e possa acessar os endpoints protegidos.

#### Critérios de Aceitação

1. WHEN uma requisição POST é recebida em `/auth/login` com e-mail e senha corretos, THE Auth_Service SHALL retornar um JWT com validade de 24 horas e o status HTTP 200.
2. WHEN uma requisição POST é recebida em `/auth/login` com credenciais inválidas, THE Auth_Service SHALL retornar o status HTTP 401 com uma mensagem de erro descritiva.
3. THE Auth_Service SHALL assinar o JWT com algoritmo HS256 e incluir o identificador do Usuário no payload do token.
4. WHILE um JWT válido é fornecido no cabeçalho `Authorization: Bearer`, THE Sistema SHALL permitir o acesso aos endpoints protegidos.
5. IF um JWT expirado ou inválido é fornecido, THEN THE Auth_Service SHALL retornar o status HTTP 401.

---

### Requisito 3: Cadastro de Despesa

**User Story:** Como um Usuário autenticado, quero cadastrar uma despesa manualmente, para que eu possa registrar meus gastos no sistema.

#### Critérios de Aceitação

1. WHEN uma requisição POST autenticada é recebida em `/expenses` com valor, categoria, data e descrição válidos, THE Expense_Service SHALL persistir a Despesa no MongoDB com o campo `source` definido como `"manual"` e retornar o status HTTP 201.
2. WHEN uma Despesa é persistida com sucesso, THE Kafka_Producer SHALL publicar um evento `expense.created` contendo o identificador da Despesa, o identificador do Usuário, o valor e a categoria.
3. WHEN o valor da Despesa criada supera o Limite_de_Gasto configurado pelo Usuário, THE Kafka_Producer SHALL publicar um evento `alert.expense.high` contendo o identificador do Usuário e o valor da Despesa.
4. WHEN uma requisição POST é recebida em `/expenses` com campos obrigatórios ausentes ou valor menor ou igual a zero, THE Expense_Service SHALL retornar o status HTTP 400 com uma mensagem de validação descritiva.
5. IF uma requisição é recebida em `/expenses` sem JWT válido, THEN THE Auth_Service SHALL retornar o status HTTP 401.

---

### Requisito 4: Listagem de Despesas

**User Story:** Como um Usuário autenticado, quero listar minhas despesas, para que eu possa visualizar meu histórico de gastos.

#### Critérios de Aceitação

1. WHEN uma requisição GET autenticada é recebida em `/expenses`, THE Expense_Service SHALL retornar somente as Despesas associadas ao identificador do Usuário extraído do JWT, com status HTTP 200.
2. THE Expense_Service SHALL retornar as Despesas ordenadas por data em ordem decrescente.
3. WHEN nenhuma Despesa é encontrada para o Usuário, THE Expense_Service SHALL retornar uma lista vazia com status HTTP 200.
4. IF uma requisição é recebida em `/expenses` sem JWT válido, THEN THE Auth_Service SHALL retornar o status HTTP 401.

---

### Requisito 5: Consulta de Despesa por Identificador

**User Story:** Como um Usuário autenticado, quero consultar os detalhes de uma despesa específica, para que eu possa visualizar todas as informações de um gasto.

#### Critérios de Aceitação

1. WHEN uma requisição GET autenticada é recebida em `/expenses/{id}` com um identificador existente pertencente ao Usuário autenticado, THE Expense_Service SHALL retornar os dados completos da Despesa com status HTTP 200.
2. WHEN uma requisição GET é recebida em `/expenses/{id}` com um identificador inexistente, THE Expense_Service SHALL retornar o status HTTP 404 com uma mensagem de erro descritiva.
3. WHEN uma requisição GET é recebida em `/expenses/{id}` com um identificador pertencente a outro Usuário, THE Expense_Service SHALL retornar o status HTTP 403.
4. IF uma requisição é recebida em `/expenses/{id}` sem JWT válido, THEN THE Auth_Service SHALL retornar o status HTTP 401.

---

### Requisito 6: Gerenciamento de Categorias

**User Story:** Como um Usuário autenticado, quero criar e listar categorias personalizadas, para que eu possa classificar minhas despesas de acordo com minha realidade financeira.

#### Critérios de Aceitação

1. WHEN uma requisição POST autenticada é recebida em `/categories` com um nome de categoria válido, THE Category_Service SHALL adicionar a categoria à lista de categorias do Usuário no MongoDB e retornar o status HTTP 201.
2. WHEN uma requisição POST é recebida em `/categories` com um nome de categoria já existente para o mesmo Usuário, THE Category_Service SHALL retornar o status HTTP 409 com uma mensagem de erro descritiva.
3. WHEN uma requisição GET autenticada é recebida em `/categories`, THE Category_Service SHALL retornar somente as categorias associadas ao Usuário autenticado com status HTTP 200.
4. WHEN nenhuma categoria é encontrada para o Usuário, THE Category_Service SHALL retornar uma lista vazia com status HTTP 200.
5. IF uma requisição é recebida em `/categories` sem JWT válido, THEN THE Auth_Service SHALL retornar o status HTTP 401.

---

### Requisito 7: Importação de Transações Bancárias

**User Story:** Como um Usuário autenticado, quero importar transações da minha conta bancária conectada, para que eu não precise cadastrar manualmente cada gasto.

#### Critérios de Aceitação

1. WHEN uma requisição GET autenticada é recebida em `/bank/import-transactions`, THE Bank_Integration_Service SHALL consultar a API bancária externa configurada (Plaid, Fake_Bank_API ou BCB_API) e retornar as transações obtidas com status HTTP 200.
2. WHEN as transações são obtidas com sucesso da API bancária externa, THE Bank_Integration_Service SHALL persistir cada transação como uma Despesa no MongoDB com o campo `source` definido como `"importado"`.
3. WHEN a importação é concluída com sucesso, THE Kafka_Producer SHALL publicar um evento `bank.transactions.imported` contendo o identificador do Usuário e a quantidade de transações importadas.
4. WHEN a API bancária externa retorna erro ou está indisponível, THE Bank_Integration_Service SHALL retornar o status HTTP 502 com uma mensagem de erro descritiva.
5. WHEN a API bancária externa retorna transações duplicadas já existentes no MongoDB, THE Bank_Integration_Service SHALL ignorar as duplicatas e persistir somente as transações novas.
6. IF uma requisição é recebida em `/bank/import-transactions` sem JWT válido, THEN THE Auth_Service SHALL retornar o status HTTP 401.

---

### Requisito 8: Publicação e Consumo de Eventos Kafka

**User Story:** Como operador do sistema, quero que eventos de negócio sejam publicados e consumidos de forma assíncrona via Kafka, para que notificações e alertas sejam processados sem impactar a latência das requisições REST.

#### Critérios de Aceitação

1. THE Kafka_Producer SHALL publicar o evento `expense.created` no tópico de mesmo nome sempre que uma nova Despesa for persistida, seja por cadastro manual ou por importação bancária.
2. THE Kafka_Producer SHALL publicar o evento `alert.expense.high` no tópico de mesmo nome sempre que o valor de uma Despesa criada superar o Limite_de_Gasto do Usuário.
3. THE Kafka_Producer SHALL publicar o evento `bank.transactions.imported` no tópico de mesmo nome sempre que uma importação bancária for concluída com sucesso.
4. IF a publicação de um evento Kafka falhar, THEN THE Kafka_Producer SHALL registrar o erro em log com nível ERROR e não interromper o fluxo principal da requisição REST.
5. THE Kafka_Consumer SHALL processar mensagens dos tópicos subscritos de forma idempotente, de modo que o reprocessamento de uma mesma mensagem não produza efeitos duplicados.

---

### Requisito 9: Segurança e Autorização

**User Story:** Como Usuário, quero que meus dados sejam acessíveis somente por mim, para que minha privacidade financeira seja garantida.

#### Critérios de Aceitação

1. THE Auth_Service SHALL rejeitar qualquer requisição aos endpoints protegidos que não contenha um JWT válido, retornando o status HTTP 401.
2. THE Expense_Service SHALL garantir que um Usuário acesse somente Despesas associadas ao seu próprio identificador.
3. THE Category_Service SHALL garantir que um Usuário acesse somente categorias associadas ao seu próprio identificador.
4. THE Auth_Service SHALL armazenar senhas exclusivamente como hash bcrypt com fator de custo mínimo de 10.
5. WHEN um JWT é validado, THE Auth_Service SHALL verificar a assinatura e a data de expiração do token antes de conceder acesso.

---

### Requisito 10: Persistência e Integridade dos Dados

**User Story:** Como Usuário, quero que meus dados sejam armazenados de forma confiável, para que eu não perca informações sobre minhas despesas.

#### Critérios de Aceitação

1. THE Sistema SHALL persistir todos os dados de Usuários e Despesas no MongoDB utilizando Spring Data MongoDB.
2. THE Expense_Service SHALL garantir que cada Despesa contenha os campos obrigatórios: `userId`, `amount`, `category`, `date` e `source`.
3. THE Sistema SHALL garantir que o campo `email` na coleção `users` seja único por meio de índice no MongoDB.
4. WHEN uma operação de escrita no MongoDB falhar, THE Sistema SHALL retornar o status HTTP 500 com uma mensagem de erro genérica e registrar o erro em log com nível ERROR.
