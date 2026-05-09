# Cash Flow API - Gerenciador Financeiro (Spring Boot)

O **Cash Flow API** é um backend de gerenciamento financeiro pessoal desenvolvido em Java. Originalmente concebido como uma aplicação CLI (Command Line Interface) com JDBC puro, o projeto está em **fase de migração** para uma arquitetura RESTful moderna utilizando o ecossistema Spring.
Trata-se de um MVP (Minimum Viable Product) focado em um usuário único (Single-tenant), inspirado nas principais funcionalidades de controle de fluxo de caixa, permitindo o rastreio de despesas, receitas e transferências entre múltiplas contas e cartões de crédito.

## 🚀 Visão Geral e Arquitetura

O sistema foi desenhado utilizando os princípios de Clean Architecture e SOLID. Com a atual migração, estamos substituindo a manipulação manual de banco de dados (ORM manual/JDBC) pela robustez do **Spring Data JPA**, e a interface de linha de comando por **Controladores REST**.

## 🛠️ Stack Tecnológica (Atualizada)

- **Linguagem:** Java 17

- **Framework Principal:** Spring Boot

- **Gerenciador de Dependências:** Maven

- **Banco de Dados:** (Multi-environment): H2 Database (Dev/Test) e MySQL (Produção/Docker)

- **Acesso a Dados:** Spring Data JPA (Hibernate)

- **Mapeamento de Objetos:** MapStruct

- **Boilerplate:** Lombok

- **Infraestrutura:** Docker & Docker Compose

- **Arquitetura:** Camadas (Domain, Repository, Config, Services, Controller)

## 🚧 Status do Projeto: Em Migração (Refatoração CLI ➡️ REST API)

Atualmente, o projeto está passando por uma refatoração arquitetural profunda.

- [x] **Fase 1:** Setup do ecossistema Spring, perfis YAML (h2 e mysql) e limpeza de pacotes legados (UI/CLI).
- [x] **Fase 2:** Mapeamento Objeto-Relacional (JPA/Entities) da camada de Domínio.
- [x] **Fase 3:** Refatoração da Camada de Acesso a Dados (Spring Data Repositories).
- [x] **Fase 4:** Criação de Controllers, DTOs e Mappers (MapStruct).
- [x] **Fase 5:** Implementação de Exceptions e Global Exception Handler.
- [x] **Fase 6:** Refatoração dos Services e Controllers para o padrão RESTful.
- [ ] **Fase 7:** Testes Automatizados e Documentação (Swagger/OpenAPI).

## 📂 Estrutura de Pacotes

A arquitetura foi desenhada utilizando os princípios de Clean Code e Inversão de Dependência, o que facilitará uma futura migração para frameworks como o Spring Boot. O projeto está dividido em:

- **`org.example.config.database`**: Configurações de conexão (JDBC) e scripts de inicialização do banco de dados H2 (DDL/DML).
- **`org.example.domain`**: Contém o coração das regras de negócio.
  - **`.models`**: Entidades de domínio (AbstractAccount, CreditCard, Transactions, etc.) utilizando herança e polimorfismo.
  - **`.enums`**: Tipos padronizados (AccountType, TransactionStatus, etc.).
  - **`.interfaces`**: Contratos de comportamento base (ex: operações de depósito/saque).
- **`org.example.repositories`**: Padrão Repository para abstração do acesso aos dados, com implementações puras em SQL para o H2.
- **`org.example.services`**: Camada de lógica de negócio e orquestração.
  - **`.impl`**: Implementações concretas dos serviços, garantindo a matemática financeira e as regras de estorno automático.
- **`org.example.ui`**: Componentes da Interface de Linha de Comando (CLI), organizados em Menus modulares (AccountMenu, TransactionMenu, etc.).

## 📋 Requisitos Implementados

- [x] **RF-01: Gestão de Contas e Carteiras:** Cadastro de contas bancárias e carteiras físicas com saldo consolidado (Permite saldo negativo).
- [x] **RF-02: Gestão de Cartões de Crédito:** Cadastro de cartões vinculados a bancos com controle de limites, dia de fechamento e vencimento.
- [x] **RF-03: Gestão de Categorias:** Classificação de transações por tipo (Receita, Despesa, Movimentação).
- [x] **RF-04: Registro de Transações:** Suporte a entradas e saídas normais, com alteração dinâmica de status (Pendente/Efetivada) alterando o saldo da conta em tempo real.
- [x] **RF-05: Transferências:** Orquestração de movimentação entre duas contas distintas, com espelhamento de transações (Saída A -> Entrada B) e estorno em cascata.
- [x] **RF-06: Despesas de Cartão:** Registro de transações vinculadas a um cartão de crédito, respeitando a data da fatura atual.

## 🔧 Como Executar (Ambiente de Desenvolvimento)

A aplicação possui suporte a múltiplos perfis de configuração (profiles).

1. **Pré-requisitos:** Ter o JDK 17 e Maven instalados.

2. **Clonar o repositório:**

```bash
git clone https://github.com/Luiz-Fernando-25/cash-flow-API.git
```

3. **Compilar o projeto** *(obrigatório antes de abrir no VS Code ou qualquer IDE)*:

```bash
./mvnw compile
```
> Este passo é necessário porque o **MapStruct** gera as implementações dos mappers em tempo de compilação. Sem ele, o editor pode exibir falsos erros nos arquivos `*Mapper.java`. Se estiver usando **VS Code**, essa compilação é feita automaticamente ao abrir o projeto.

4. **Execução Local (Perfil H2):** A aplicação subirá na porta `8080` com banco de dados em memória.

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

5. **Execução com MySQL (via Docker Compose):**

```bash
docker-compose up -d
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
```

6. **Documentação interativa (Swagger UI):** Após iniciar, acesse:
```
http://localhost:8080/swagger-ui/index.html
```

## 🏗️ Próximos Passos (Roadmap)

- [ ] Fechamento de Faturas: Desenvolver a rotina de consolidação de gastos de cartão de crédito baseada no dia de vencimento.

## 🤖 Sobre o Desenvolvimento

Este projeto é parte de um estudo aprofundado de arquitetura backend e evolução de software. A transição de um modelo procedural/manual (JDBC/CLI) para um ecossistema moderno (Spring Boot/JPA) visa consolidar conhecimentos em injeção de dependências, ORM, design de APIs e arquitetura limpa.
