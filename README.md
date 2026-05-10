# 💰 Cash Flow API — Gerenciador Financeiro Pessoal

API REST para gerenciamento financeiro pessoal desenvolvida em **Java 17** com **Spring Boot 4**. Permite o controle completo de contas bancárias, carteiras, cartões de crédito, transações e transferências com regras de estorno automático e controle de fatura.

> **MVP Single-tenant** — focado em um único usuário, projetado para evolução futura com autenticação e multi-tenancy.

---

## 📐 Arquitetura

O projeto segue uma arquitetura em camadas com princípios de **Clean Architecture** e **SOLID**:

```
┌─────────────────────────────────────────────────────────┐
│                    Controllers (REST)                   │
│   CategoryController · AccountController · ...          │
├─────────────────────────────────────────────────────────┤
│                DTOs + Mappers (MapStruct)                │
│   Request/Response DTOs · CategoryMapper · ...          │
├─────────────────────────────────────────────────────────┤
│                   Services (Negócio)                    │
│   Validações · Estornos · Orquestração financeira       │
├─────────────────────────────────────────────────────────┤
│                 Repositories (Spring Data JPA)          │
│   AccountRepository · TransactionRepository · ...       │
├─────────────────────────────────────────────────────────┤
│                   Domain (Entidades JPA)                │
│   AbstractAccount · CreditCard · Transactions · ...     │
├─────────────────────────────────────────────────────────┤
│              Exceptions (GlobalExceptionHandler)        │
│   BusinessRuleException · ResourceNotFoundException     │
└─────────────────────────────────────────────────────────┘
```

---

## 🛠️ Stack Tecnológica

| Camada | Tecnologia |
|---|---|
| **Linguagem** | Java 17 |
| **Framework** | Spring Boot 4.0.6 |
| **Build** | Maven (com Maven Wrapper) |
| **Banco de Dados** | H2 (dev/test) · MySQL (produção/Docker) |
| **ORM** | Spring Data JPA (Hibernate) |
| **Mapeamento DTO ↔ Entity** | MapStruct 1.5.5 |
| **Boilerplate** | Lombok |
| **Documentação API** | Springdoc OpenAPI (Swagger UI) |
| **Testes** | JUnit 5 · Mockito · 89 testes unitários |
| **Infraestrutura** | Docker & Docker Compose |

---

## 📋 Funcionalidades

### Contas (`/api/accounts`)
- Cadastro de contas bancárias (`BANCO`) e carteiras físicas (`CARTEIRA`)
- Depósito e saque com validação de valores
- Permite saldo negativo (sem bloqueio por insuficiência)
- Listagem com filtro por tipo

### Categorias (`/api/categories`)
- Classificação de transações por tipo (`RECEITA`, `DESPESA`)
- Validação de nome duplicado
- Categoria padrão (ID 1) protegida contra exclusão

### Cartões de Crédito (`/api/credit-cards`)
- Vinculação obrigatória a uma conta bancária
- Controle de limite, saldo da fatura, dia de fechamento e vencimento
- Validação de dias (1-28) para compatibilidade com todos os meses

### Transações (`/api/transactions`)
- **Transações normais** (entrada/saída) com status `PENDENTE` ou `EFETIVADA`
- **Transações de cartão** com cálculo automático da fatura vigente baseado no dia de fechamento
- Estorno automático ao alterar status ou remover transação efetivada
- Atualização em lote (`updateBatch`)
- Busca com filtros combinados (status, categoria, tipo, conta, cartão)

### Transferências (`/api/transfers`)
- Espelhamento automático: cria uma transação de saída na conta A e uma de entrada na conta B
- Estorno em cascata ao remover

---

## 🧪 Testes

A camada de **Services** possui cobertura completa com **89 testes unitários** utilizando **JUnit 5** e **Mockito**:

| Classe de Teste | Testes | Cobertura |
|---|---|---|
| `AccountServiceImplTest` | 19 | Completa |
| `CategoryServiceImplTest` | 16 | Completa |
| `CreditCardServiceImplTest` | 24 | Completa |
| `TransactionServiceImplTest` | 24 | Completa |
| `TransferServiceImplTest` | 6 | Completa |

**Executar testes:**
```bash
# Windows
.\mvnw.cmd test

# Linux / Mac
./mvnw test
```

---

## ⚠️ Tratamento de Erros

A API utiliza um `GlobalExceptionHandler` com respostas padronizadas:

| Exceção | HTTP Status | Quando |
|---|---|---|
| `MethodArgumentNotValidException` | `400 Bad Request` | Validação de DTOs (`@NotBlank`, `@NotNull`) |
| `ResourceNotFoundException` | `404 Not Found` | Entidade não encontrada pelo ID |
| `BusinessRuleException` | `422 Unprocessable Entity` | Violação de regra de negócio |

Formato de resposta de erro:
```json
{
  "timestamp": "2026-05-10T19:00:00",
  "status": 422,
  "error": "Business rule violation",
  "message": "Já existe esse nome na lista de categorias.",
  "path": "/api/categories"
}
```

---

## 🔧 Como Executar

### Pré-requisitos
- JDK 17+
- Docker (opcional, para MySQL)

### 1. Clonar o repositório
```bash
git clone https://github.com/Luiz-Fernando-25/cash-flow-API.git
cd cash-flow-API
```

### 2. Compilar o projeto
> **Obrigatório** antes de abrir na IDE — o MapStruct gera implementações dos mappers em tempo de compilação.

```bash
# Windows
.\mvnw.cmd compile

# Linux / Mac
./mvnw compile
```

### 3. Executar com H2 (desenvolvimento)
Banco em memória, sem configuração adicional. A aplicação sobe na porta `8080`.

```bash
# Windows
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=h2

# Linux / Mac
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

### 4. Executar com MySQL (produção)
```bash
docker-compose up -d

# Windows
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=mysql

# Linux / Mac
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
```

### 5. Acessar Swagger UI
```
http://localhost:8080/swagger-ui/index.html
```

---

## 📂 Estrutura de Pacotes

```
src/main/java/org/example/
├── Main.java
├── config/                    # Configurações (DataSource, profiles)
├── controllers/               # Endpoints REST (5 controllers)
├── domain/
│   ├── enums/                 # AccountType, TransactionStatus, TransactionType, CategoryType
│   ├── interfaces/            # Contratos base (depósito/saque)
│   └── models/                # Entidades JPA (10 classes com herança e polimorfismo)
├── dtos/                      # Request, Response e Update DTOs (15 records)
├── exceptions/                # Exceções + GlobalExceptionHandler
├── mappers/                   # Interfaces MapStruct (geração automática)
├── repositories/              # Spring Data JPA Repositories
└── services/
    └── impl/                  # Regras de negócio (estornos, validações, fatura)

src/test/java/org/example/
└── services/impl/             # 89 testes unitários (JUnit 5 + Mockito)
```

---

## 📊 Evolução do Projeto

| Fase | Descrição | Status |
|---|---|---|
| 1 | Setup Spring Boot, perfis YAML (H2/MySQL), limpeza de pacotes CLI | ✅ |
| 2 | Mapeamento JPA das entidades de domínio | ✅ |
| 3 | Migração para Spring Data Repositories | ✅ |
| 4 | Controllers REST, DTOs e Mappers (MapStruct) | ✅ |
| 5 | Global Exception Handler e exceções customizadas | ✅ |
| 6 | Padronização RESTful nível 2 (Richardson Maturity Model) | ✅ |
| 7 | Testes unitários da camada de Services | ✅ |
| 8 | Testes de Controllers (MockMvc) | 🔜 |
| 9 | Fechamento de faturas de cartão de crédito | 🔜 |

---

## 🤖 Sobre o Desenvolvimento

Projeto de estudo aprofundado de arquitetura backend e evolução de software. A transição de um modelo procedural (JDBC/CLI) para um ecossistema moderno (Spring Boot/JPA/REST) consolida conhecimentos em:

- Injeção de dependências e inversão de controle
- ORM e mapeamento objeto-relacional
- Design de APIs RESTful
- Testes automatizados com mocks
- Tratamento de exceções padronizado
- Arquitetura limpa e princípios SOLID
