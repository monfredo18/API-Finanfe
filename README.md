# Controle Financeiro API

API REST para controle financeiro pessoal, desenvolvida em **Java com Spring Boot**. O projeto está sendo construído de forma incremental, começando pelo mínimo que funciona e evoluindo em fases, com foco em boas práticas e aprendizado.

> **Status:** em desenvolvimento (Fase 1)

## Sobre o projeto

A ideia é ter uma API capaz de registrar receitas e despesas, organizar contas, calcular saldo e, nas fases finais, gerar relatórios e controlar acesso por usuário.

Cada fase termina com a API funcionando e testada, o que facilita entender o impacto de cada mudança.

## Tecnologias

| Tecnologia | Uso |
|---|---|
| Java [versão] | Linguagem principal |
| Spring Boot | Framework da API |
| Spring Web | Endpoints REST |
| Spring Data JPA | Acesso ao banco de dados |
| H2 Database | Banco em memória (Fase 1) |
| Bean Validation | Validação dos dados de entrada |
| JUnit 5 | Testes |
| Maven | Gerenciamento de dependências |

**Planejadas para as próximas fases:** PostgreSQL, Flyway, Docker, Spring Security + JWT, Testcontainers, Swagger (Springdoc).

## Roadmap

- [ ] **Fase 1 – O mínimo:** CRUD básico de transações com H2
- [ ] **Fase 2 – Conta e saldo:** entidade Conta, endpoint de saldo, DTOs e tratamento de erros
- [ ] **Fase 3 – Banco de verdade:** PostgreSQL com Docker e migrations com Flyway
- [ ] **Fase 4 – Usuários e segurança:** login com JWT e isolamento de dados por usuário
- [ ] **Fase 5 – Regras avançadas:** transferências, categorias, relatórios mensais, auditoria e testes de integração

## Arquitetura

A aplicação segue uma arquitetura em camadas:

```
Controller → Service → Repository → Banco de dados
```

- **controller:** recebe as requisições HTTP e devolve as respostas
- **service:** concentra as regras de negócio
- **repository:** acessa o banco de dados
- **model:** entidades mapeadas para tabelas

```
src/main/java/com/empresa/financeiro/
├── controller/
├── service/
├── repository/
└── model/
```

## Como rodar

### Pré-requisitos

- Java [versão] ou superior
- Maven (ou use o wrapper `./mvnw` incluído no projeto)

### Passos

```bash
# 1. Clone o repositório
git clone [URL-DO-REPOSITORIO]

# 2. Entre na pasta do projeto
cd controle-financeiro-api

# 3. Execute a aplicação
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

### Rodando os testes

```bash
./mvnw test
```

## Endpoints (Fase 1)

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/transacoes` | Cria uma transação |
| `GET` | `/transacoes` | Lista todas as transações |
| `DELETE` | `/transacoes/{id}` | Remove uma transação |

### Exemplo de requisição

`POST /transacoes`

```json
{
  "descricao": "Salário de outubro",
  "valor": 3500.00,
  "data": "2026-10-05",
  "tipo": "RECEITA"
}
```

### Exemplo de resposta

```json
{
  "id": 1,
  "descricao": "Salário de outubro",
  "valor": 3500.00,
  "data": "2026-10-05",
  "tipo": "RECEITA"
}
```

## Decisões de design

- **`BigDecimal` para valores monetários:** evita erros de arredondamento do `double`/`float`.
- **Evolução incremental:** cada fase adiciona só o necessário, mantendo o projeto sempre funcional.
- **Saldo calculado sob demanda:** mais simples e fácil de auditar; será otimizado apenas se necessário.

## Aprendizados

Este projeto também serve como estudo de:

- Estrutura de uma API REST com Spring Boot
- Separação de responsabilidades em camadas
- Modelagem de domínio financeiro
- Testes automatizados
- Evolução de arquitetura ao longo do tempo

## Autor

**[Douglas Monfredo Machado]**
[https://www.linkedin.com/in/douglas-monfredo-machado] · [monfredo18@gmail.com]
