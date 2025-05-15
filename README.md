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


A API possui um mecanismo centralizado de tratamento de exceções, retornando respostas padronizadas em JSON com `timestamp`, `status`, `error`, `message` e `path`. Isso garante clareza nos testes e documentação Swagger.

### 🔸 Erro de Validação (HTTP 422)

Quando campos obrigatórios ou inválidos são enviados:

```json
{
  "timestamp": "2025-05-15T01:56:00",
  "status": 422,
  "error": "Erro de Validação",
  "messages": {
    "placa": "A placa deve conter exatamente 7 caracteres.",
    "nome": "O campo nome é obrigatório."
  },
  "path": "/motos"
}
```

### 🔸 Entidade Não Encontrada (HTTP 404)

Quando um recurso não existe no banco (ex: `/patios/99`):

```json
{
  "timestamp": "2025-05-15T01:55:00",
  "status": 404,
  "error": "Not Found",
  "message": "Pátio não encontrado",
  "path": "/patios/99"
}
```

### 🔸 Erro Genérico (HTTP 500)

Quando há erro interno inesperado, como sort inválido:

```json
{
  "timestamp": "2025-05-15T01:57:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Erro interno: No property 'string' found for type 'Moto'",
  "path": "/motos/buscar/placa"
}
```

Esse comportamento é implementado na classe `GlobalExceptionHandler` com `@RestControllerAdvice`.

---


## 👥 Alunos Participantes

- Gabriel Gomes Mancera (RM: 555427)
- Victor Hugo Carvalho  (RM: 558550)
- Juliana de Andrade Sousa (RM: 558834)


---


## ✅ Funcionalidades Extras

- 🔍 Filtros dinâmicos por `placa` e `status` da moto
- 📃 Paginação com suporte ao `Pageable`
- 💾 Cache para otimização no endpoint de listagem de motos
- ⚠️ Tratamento global e centralizado de exceções
- 🔄 Uso de DTOs para encapsulamento de dados e separação das entidades


---
