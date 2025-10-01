# 🚀 MotoGrid

Aplicação web + API em **Spring Boot 3.2.5** para gestão de **Motos** e **Pátios**, com:

- **Frontend** em **Thymeleaf** (fragments reutilizáveis: `head`, `header`, `footer`) e páginas de **lista** e **formulário** para Motos e Pátios.
- **Autenticação e autorização** com **Spring Security** (login por formulário, perfis `ADMIN` e `OPERADOR`, CSRF configurado).
- **Banco H2 in-memory** com **Flyway** (4 migrações + *seed*).
- **API REST** documentada por **Swagger/OpenAPI**.
- **Validações (Jakarta)**, tratamento de erros e **tema escuro** com **Bootswatch Darkly** + CSS customizado.
- **Fluxo adicional:** **Exportação de Motos para XLSX** diretamente da tela de lista (**botão “Exportar XLSX”**).

---

## 📑 Sumário
- [Arquitetura & Tecnologias](#arquitetura--tecnologias)
- [Como Rodar](#como-rodar)
- [Login, Perfis e Autorização](#login-perfis-e-autorização)
- [Frontend (Thymeleaf)](#frontend-thymeleaf)
- [Fluxo Adicional — Exportar XLSX](#fluxo-adicional--exportar-xlsx)
- [Banco de Dados & Flyway](#banco-de-dados--flyway)
- [API REST & Swagger](#api-rest--swagger)
- [Tratamento de Erros (REST)](#tratamento-de-erros-rest)
- [Estrutura de Pastas](#estrutura-de-pastas)
- [Roteiro de Testes (passo a passo)](#roteiro-de-testes-passo-a-passo)
- [Troubleshooting](#troubleshooting)
- [Checklist da Sprint](#checklist-da-sprint)
- [Autores](#autores)

---

## Arquitetura & Tecnologias
- **Java 17**
- **Spring Boot 3.2.5** (Web, **Thymeleaf**, Security, Validation)
- **Spring Data JPA**
- **H2 Database** (memória)
- **Flyway 9.22.x**
- **Springdoc OpenAPI 2.3.x** (Swagger)
- **Thymeleaf Extras Spring Security 6**
- **Bootswatch Darkly** + `static/css/app.css`
- **Maven**

---

## Como Rodar

### Pré-requisitos
- **JDK 17** instalado
- **Maven** (ou usar o wrapper `mvnw` da raiz do projeto)

### Passos
1) Clonar o repositório e entrar na pasta do projeto.
2) Executar a aplicação:
    - **Linux/Mac:** `./mvnw spring-boot:run`
    - **Windows:** `mvn spring-boot:run`

### Endereços úteis
- **Web (Home):** `http://localhost:8080/web`
- **Login:** `http://localhost:8080/login`
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **H2 Console:** `http://localhost:8080/console`
    - JDBC URL: `jdbc:h2:mem:motogrid`
    - User: `sa`
    - Password: *(em branco)*

---

## Login, Perfis e Autorização

**Usuários (in-memory):**

| Usuário    | Senha | Perfis   |
|------------|:-----:|----------|
| `admin`    | `123` | `ADMIN`  |
| `operador` | `123` | `OPERADOR` |

**Regras de acesso (principais):**
- **Público (permitAll):** Swagger (`/v3/api-docs/**`, `/swagger-ui.html`, `/swagger-ui/**`), H2 (`/console/**`), estáticos (`/css/**`, `/img/**`), `/error`, `/login`.
- **Web (Thymeleaf):**
    - `GET /web/**` → `ADMIN` **ou** `OPERADOR`
    - Demais ações em `/web/**` (criar/editar/excluir) → **somente `ADMIN`**
- **REST:**
    - `GET /motos/**` e `GET /patios/**` → `ADMIN` **ou** `OPERADOR`
    - `POST/PUT/DELETE` em `/motos/**` e `/patios/**` → **somente `ADMIN`**

**CSRF:** ativo para formulários do **/web/** e **ignorado** para **REST/Swagger/H2**.  
**Login:** formulário customizado em `/login`, `defaultSuccessUrl("/web", true)`.  
**Logout:** via **POST** (use o botão **Sair** na navbar). Abrir `/logout` por GET pode mostrar erro — é esperado.  
**403:** `/acesso-negado`.

---

## Frontend (Thymeleaf)

### Fragments
- `templates/fragments/head.html`  
  Inclui `<meta>`, título dinâmico, favicon, **Bootswatch Darkly** e `@{/css/app.css}`.  
  Uso: `th:replace="~{fragments/head :: head('Título da Página')}"`
- `templates/fragments/header.html`  
  Navbar com links **Motos**, **Pátios** e **Sair** (visibilidade com `sec:authorize`).  
  Uso: `th:replace="~{fragments/header :: header}"`
- `templates/fragments/footer.html`  
  Rodapé (`© MotoGrid`) e bundle do Bootstrap.  
  Uso: `th:replace="~{fragments/footer :: footer}"`

### Páginas
- `templates/home.html` — Boas-vindas e navegação rápida.
- `templates/login.html` — Tela de login (mensagens de erro/sucesso).
- `templates/access-denied.html` — 403 (acesso negado).
- `templates/motos/list.html` — Lista com **badges** por status, ações (condicionais por perfil) e **botão para exportar XLSX**.
- `templates/motos/form.html` — Form para criar/editar (CSRF + validações).
- `templates/patios/list.html` — Lista com ações.
- `templates/patios/form.html` — Form para criar/editar (CSRF + validações).

### Estilo (tema escuro)
- `static/css/app.css` aplica:
    - Navbar com gradiente e *blur*.
    - Cards/tabelas/links com cores **Darkly** + overrides.
    - **Badges**:
        - `DISPONIVEL` → success
        - `EM_USO` → primary
        - `EM_MANUTENCAO` → warning
        - outros → secondary

---

## Fluxo Adicional — Exportar XLSX

Permite baixar a lista de motos em **Excel (.xlsx)** diretamente da tela **Motos**.

**Como usar (UI):**
- Na página **Motos**, clique em **Exportar XLSX**.

**Endpoint (usado pela UI):**
```
GET /web/motos/export.xlsx?status=<opcional>&patioId=<opcional>
```

**Parâmetros (opcionais):**
- `status` — filtra por status (`DISPONIVEL`, `EM_USO`, `EM_MANUTENCAO`, `INATIVA`).
- `patioId` — filtra por pátio (ID).

**Formato do arquivo:**
- Arquivo **.xlsx** (Excel) com as colunas **Placa**, **Modelo**, **Status** e **Pátio**.

**Exemplos rápidos:**
```
/web/motos/export.xlsx
/web/motos/export.xlsx?status=EM_USO&patioId=1
```

**Segurança:** requer autenticação; disponível para `ADMIN` e `OPERADOR` (mesmo controle de `/web/**`).

---

## Banco de Dados & Flyway

**H2 (memória)** — dados são recriados a cada inicialização.

**Migrações (executadas automaticamente):**
1. `V1__create_table_patio.sql`
2. `V2__create_table_moto.sql`
3. `V3__index_moto_placa.sql`
4. `V4__seed_base.sql` *(pátios & motos iniciais)*

---

## API REST & Swagger

**Swagger UI:** `http://localhost:8080/swagger-ui.html`  
**OpenAPI JSON:** `http://localhost:8080/v3/api-docs`


### Endpoints principais

**Pátios**
- `GET /patios` — lista (paginável)
- `GET /patios/{id}`
- `POST /patios` — **ADMIN**
- `PUT /patios/{id}` — **ADMIN**
- `DELETE /patios/{id}` — **ADMIN**

Exemplo `POST /patios`:
```json
{
  "nome": "Pátio Zona Norte",
  "cidade": "Guarulhos",
  "capacidade": 80
}
```

Exemplo `PUT /patios/{id}`:
```json
{
  "id": 1,
  "nome": "Pátio Zona Leste",
  "cidade": "São Paulo",
  "capacidade": 100
}
```

**Motos**
- `GET /motos` — lista (paginável)
- `GET /motos/{id}`
- `POST /motos` — **ADMIN**
- `PUT /motos/{id}` — **ADMIN**
- `DELETE /motos/{id}` — **ADMIN**

Exemplo `POST /motos`:
```json
{
  "placa": "ABC1D23",
  "modelo": "Honda Biz",
  "status": "DISPONIVEL",
  "patioId": 1
}
```

Exemplo `PUT /motos/{id}`:
```json
{
  "id": 1,
  "placa": "XYZ5A67",
  "modelo": "Yamaha Factor",
  "status": "EM_MANUTENCAO",
  "patioId": 1
}
```

---

## Tratamento de Erros (REST)

Respostas padronizadas em JSON com `timestamp`, `status`, `error`, `message` e `path`.

**422 – Validação**
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

**404 – Não encontrado**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Pátio não encontrado"
}
```

**400 – Status inválido no filtro**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Status inválido. Use: DISPONIVEL, EM_USO, EM_MANUTENCAO ou INATIVA."
}
```

**400 – ID divergente (PUT)**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "ID do corpo e da URL não conferem"
}
```

**500 – Erro genérico**
```json
{
  "status": 500,
  "error": "Internal Server Error",
  "message": "Erro interno: ..."
}
```

---


## Roteiro de Testes (passo a passo)

### A) Autenticação & Autorização
1. Acesse `/login`:
    - Entre como **admin/123** → redireciona para `/web`.
    - Entre como **operador/123** → redireciona para `/web`.
2. Navbar: abra **Motos** e **Pátios** com ambos os perfis – **ambos** podem visualizar.
3. Com **operador**, confirme que **não** há botões de **criar/editar/excluir** (aparece “Somente leitura”).
4. Com **admin**, confirme que **criar/editar/excluir** funcionam.
5. Clique em **Sair** (navbar) → sessão encerrada (logout **POST**).

### B) CRUD Web (Thymeleaf)
1. **Pátios → Novo pátio**: criar, voltar à lista e conferir registro.
2. **Editar pátio** e confirmar persistência.
3. **Excluir pátio** (com `admin`).
4. **Motos → Nova moto** vinculando a um pátio existente.
5. **Editar** e **excluir** uma moto.

### C) Fluxo adicional — Exportar XLSX
1. Em **/web/motos**, clique **Exportar XLSX** → abra no Excel: colunas e acentos corretos.
2. Teste filtros via URL (ex.: `?status=EM_USO&patioId=1`).

### D) Validações
1. Tente salvar **pátio** sem `nome`/`cidade` → mensagens de erro ao lado dos campos.
2. Tente salvar **moto** sem `placa`/`modelo` ou sem `patioId` → mensagens de erro.
3. Se houver regra de placa, teste formato inválido → deve rejeitar.

### E) API REST (Swagger)
1. Logado, abra `/swagger-ui.html`.
2. Execute `GET /patios` e `GET /motos` → deve listar seed + registros criados.
3. Com **ADMIN**, teste `POST/PUT/DELETE` para ambos recursos.
4. Com **OPERADOR**, `POST/PUT/DELETE` devem falhar com **403** (ou 401, conforme o caso).

### F) H2 & Migrações
1. Acesse `/console` e conecte (`jdbc:h2:mem:motogrid`).
2. Verifique as tabelas (`PATIO`, `MOTO`) e a tabela do **Flyway**.
3. Confira dados de **seed** (V4).

---


## Autores
- **Gabriel Gomes Mancera** (RM: 555427)
- **Victor Hugo Carvalho** (RM: 558550)
- **Juliana de Andrade Sousa** (RM: 558834)
