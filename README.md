# 💰 Finanfe API

API REST para controle financeiro pessoal — gerencie receitas, despesas, categorias e obtenha relatórios de gastos por período.

## 🚀 Como executar

```bash
# Desenvolvimento (H2 em memória — sem configuração)
./mvnw spring-boot:run

# Produção (MySQL — requer MySQL rodando na porta 3306)
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

O servidor inicia em **http://localhost:8080**

---

## 📋 Endpoints

### Categorias — `/api/categorias`

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/categorias` | Lista todas as categorias |
| GET | `/api/categorias/{id}` | Busca categoria por ID |
| POST | `/api/categorias` | Cria nova categoria |
| PUT | `/api/categorias/{id}` | Atualiza categoria |
| DELETE | `/api/categorias/{id}` | Remove categoria |

**Exemplo — criar categoria:**
```json
POST /api/categorias
{
  "nome": "Mercado",
  "descricao": "Compras de supermercado",
  "icone": "🛒"
}
```

---

### Transações — `/api/transacoes`

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/transacoes` | Lista todas as transações |
| GET | `/api/transacoes?tipo=DESPESA` | Filtra por tipo (RECEITA ou DESPESA) |
| GET | `/api/transacoes?inicio=2024-01-01&fim=2024-01-31` | Filtra por período |
| GET | `/api/transacoes?categoriaId=1` | Filtra por categoria |
| GET | `/api/transacoes/{id}` | Busca transação por ID |
| POST | `/api/transacoes` | Cria nova transação |
| PUT | `/api/transacoes/{id}` | Atualiza transação |
| DELETE | `/api/transacoes/{id}` | Remove transação |

**Exemplo — criar transação:**
```json
POST /api/transacoes
{
  "descricao": "Compras no Pão de Açúcar",
  "valor": 320.50,
  "tipo": "DESPESA",
  "data": "2024-10-15",
  "categoriaId": 1,
  "observacao": "Compra mensal"
}
```

> **tipo** deve ser `RECEITA` ou `DESPESA`

---

### Relatórios — `/api/relatorios`

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/relatorios/mensal` | Relatório do mês atual |
| GET | `/api/relatorios/mensal?ano=2024&mes=10` | Relatório de mês específico |
| GET | `/api/relatorios/semanal` | Relatório da semana atual |
| GET | `/api/relatorios/semanal?referencia=2024-10-28` | Relatório da semana de uma data |
| GET | `/api/relatorios/periodo?inicio=2024-01-01&fim=2024-12-31` | Relatório de período customizado |

**Exemplo de resposta:**
```json
{
  "periodo": "10/2026",
  "totalReceitas": 5000.00,
  "totalDespesas": 820.50,
  "saldo": 4179.50,
  "gastosPorCategoria": [
    { "categoria": "Mercado",     "total": 619.80, "percentual": 75.5 },
    { "categoria": "Fast Food",   "total": 122.80, "percentual": 15.0 },
    { "categoria": "Transporte",  "total": 77.90,  "percentual": 9.5  }
  ]
}
```

---

## 🗄️ Banco de dados

| Perfil | Banco | URL |
|--------|-------|-----|
| `default` (dev) | H2 em memória | [http://localhost:8080/h2-console](http://localhost:8080/h2-console) |
| `prod` | MySQL | `localhost:3306/finanfe_db` |

---

## ❌ Respostas de erro

| Status | Situação |
|--------|----------|
| 400 | Dados inválidos (validação) |
| 400 | Regra de negócio violada (ex: categoria duplicada) |
| 404 | Recurso não encontrado |
| 500 | Erro interno do servidor |

**Exemplo — erro de validação:**
```json
{
  "status": 400,
  "erro": "Dados inválidos",
  "timestamp": "2026-10-29T15:24:33.899",
  "campos": {
    "valor": "Valor deve ser maior que zero",
    "descricao": "Descrição é obrigatória"
  }
}
```

---

## 🏗️ Estrutura do projeto

```
src/main/java/com/finanfe_api/
├── controller/
│   ├── CategoriaController.java
│   ├── TransacaoController.java
│   └── RelatorioController.java
├── service/
│   ├── CategoriaService.java
│   ├── TransacaoService.java
│   └── RelatorioService.java
├── model/
│   ├── Categoria.java
│   ├── Transacao.java
│   └── TipoTransacao.java (enum)
├── dto/
│   ├── CategoriaDTO.java
│   ├── TransacaoDTO.java
│   └── RelatorioDTO.java
├── repository/
│   ├── CategoriaRepository.java
│   └── TransacaoRepository.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   └── RecursoNaoEncontradoException.java
└── FinanfeApiApplication.java
```

## 🛠️ Tecnologias

- **Java 21**
- **Spring Boot 3.3.5**
- **Spring Data JPA + Hibernate**
- **H2** (desenvolvimento) / **MySQL** (produção)
- **Bean Validation** (jakarta.validation)
- **Maven**
