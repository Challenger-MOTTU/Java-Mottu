# 🚀 MotoGridAPI

API REST desenvolvida para o projeto da **1ª Sprint do Challenge FIAP (Java Advanced)**.  
O sistema permite o **gerenciamento de motos e pátios**, com funcionalidades de CRUD, filtros, paginação, cache, validações e documentação automática via Swagger.

✅ Este projeto atende 100% dos requisitos técnicos exigidos pela entrega da Sprint 1.

---

## 🎯 Objetivo da API

Oferecer uma solução backend robusta para:
- Cadastrar, atualizar e listar motos.
- Relacionar motos a pátios.
- Filtrar motos por status ou placa.
- Validar e padronizar erros via DTO e tratamento global.
- Gerenciar os pátios disponíveis.
- Exibir documentação interativa via Swagger.

---

## 🛠 Tecnologias Utilizadas

- Java 17
- Spring Boot 3.4.5
- Spring Web
- Spring Data JPA
- H2 Database (in-memory)
- Bean Validation (Jakarta)
- Springdoc OpenAPI 2.5.0 (Swagger)
- Lombok
- Maven

---

## ▶️ Instruções para Executar

### Pré-requisitos:
- JDK 17 instalado
- Maven configurado
- IDE como IntelliJ ou VSCode

### Passos:

1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/motogrid-api.git
   ```

2. Acesse o projeto e execute via sua IDE ou terminal:
   ```bash
   ./mvnw spring-boot:run
   ```

3. Acesse os recursos:
    - **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
    - **H2 Console**: [http://localhost:8080/console](http://localhost:8080/console)
        - JDBC URL: `jdbc:h2:mem:motogrid`
        - Username: `sa`
        - Password: (em branco)

---

## 🔗 Endpoints e Exemplos de Body

### 📦 `/patios`

#### POST `/patios`
```json
{
  "nome": "Pátio Zona Norte",
  "cidade": "Guarulhos",
  "capacidade": 80
}
```

#### PUT `/patios/{id}`
```json
{
  "id": 1,
  "nome": "Pátio Zona Leste",
  "cidade": "São Paulo",
  "capacidade": 100
}
```

---

### 🛵 `/motos`

#### POST `/motos`
```json
{
  "placa": "ABC1234",
  "modelo": "Honda Biz",
  "status": "DISPONIVEL",
  "patioId": 1
}
```

#### PUT `/motos/{id}`
```json
{
  "id": 1,
  "placa": "XYZ5678",
  "modelo": "Yamaha Factor",
  "status": "EM_MANUTENCAO",
  "patioId": 1
}
```

---

## ❌ Tratamento de Erros

A API possui um mecanismo centralizado de tratamento de exceções, retornando respostas padronizadas em JSON com `timestamp`, `status`, `error`, `message` e `path`.

### 🔸 Erro de Validação (HTTP 422)
```json
{
  "status": 422,
  "error": "Erro de Validação",
  "messages": {
    "placa": "A placa é obrigatória",
    "nome": "O nome do pátio é obrigatório"
  }
}
```

### 🔸 Entidade Não Encontrada (HTTP 404)
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Pátio não encontrado"
}
```

### 🔸 Status Inválido no Filtro (HTTP 400)
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Status inválido. Use: DISPONIVEL, EM_USO, EM_MANUTENCAO ou INATIVA."
}
```

### 🔸 ID do PUT divergente (HTTP 400)
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "ID do corpo e da URL não conferem"
}
```

### 🔸 Erro Genérico (HTTP 500)
```json
{
  "status": 500,
  "error": "Internal Server Error",
  "message": "Erro interno: ..."
}
```

---

## 👥 Alunos Participantes

- Gabriel Gomes Mancera (RM: 555427)
- Victor Hugo Carvalho (RM: 558550)
- Juliana de Andrade Sousa (RM: 558834)

---

## ✅ Funcionalidades Extras

- 🔍 Filtros dinâmicos por `placa` e `status` da moto
- 📃 Paginação com suporte ao `Pageable`
- 💾 Cache para otimização no endpoint de listagem de motos
- ⚠️ Tratamento global e centralizado de exceções
- 🔄 Uso de DTOs para encapsulamento de dados
- 🛑 Validação de enums com mensagem 400 personalizada
- 🔐 Validação de consistência entre ID da URL e do corpo no PUT
