# 🚀 MotoGrid — Web + API (Spring Boot)

Aplicação web + API para gestão de **Motos** e **Pátios** construída com **Spring Boot 3.2.5**.  
Esta versão consolida a **4ª Sprint (Java Advanced + DB Relacional/Não‑Relacional)** com integrações Oracle e MongoDB, exportações e evidências para avaliação.

> **Deploy:** https://java-mottu.onrender.com  
> **Swagger UI:** https://java-mottu.onrender.com/swagger-ui.html  
> **Home Web:** https://java-mottu.onrender.com/web  
> **Login:** https://java-mottu.onrender.com/login

---

## 📌 Sumário
- [Arquitetura & Tecnologias](#arquitetura--tecnologias)
- [Narrativa da Solução (o porquê das escolhas)](#narrativa-da-solução-o-porquê-das-escolhas)
- [Como Rodar Localmente](#como-rodar-localmente)
- [Perfis, Login e Autorização](#perfis-login-e-autorização)
- [Frontend (Thymeleaf)](#frontend-thymeleaf)
- [Relatórios (Export XLSX/CSV)](#relatórios-export-xlsxcsv)
- [Banco de Dados & Migrações](#banco-de-dados--migrações)
- [API REST & Swagger](#api-rest--swagger)
- [Tratamento de Erros (PADRÃO)](#tratamento-de-erros-padrão)
- [Integrações Oracle & MongoDB](#integrações-oracle--mongodb)
    - [Oracle (função, procedure e DBMS_OUTPUT)](#oracle-função-procedure-e-dbms_output)
    - [MongoDB (import, índices e agregações)](#mongodb-import-índices-e-agregações)
- [Evidências da 4ª Sprint](#evidências-da-4ª-sprint)
- [Roteiro de Testes (sugestão para o vídeo)](#roteiro-de-testes-sugestão-para-o-vídeo)
- [🌐 Deploy (Render)](#-deploy-render)
- [Troubleshooting](#troubleshooting)
- [Checklist de Entrega (rubrica do professor)](#checklist-de-entrega-rubrica-do-professor)
- [Estrutura de Pastas](#estrutura-de-pastas)
- [Autores](#autores)

---

## 🧱 Arquitetura & Tecnologias

- **Java 17**, **Maven**
- **Spring Boot 3.2.5**
    - Web, **Thymeleaf**, Security, Validation
    - Spring Data **JPA** (H2 / Oracle)
    - Spring Data **MongoDB**
    - Cache com `@EnableCaching`
- **Banco Relacional (H2)** em memória (dev) / arquivo (prod)
- **Flyway** (migrations V1..V4 + seed)
- **OpenAPI/Swagger** (springdoc)
- **UI**: Bootswatch **Darkly** + CSS custom (`static/css/app.css`)
- **Relatórios**: **Apache POI** (XLSX) + CSV “Excel‑friendly” (BOM + `sep=`)
- **Oracle**  via `JdbcTemplate`
- **MongoDB** para analytics (documentos + agregações)

---

## 🧠 Narrativa da Solução (o porquê das escolhas)

- **H2** como banco primário em **dev** por simplicidade e velocidade. Em **prod**, H2 em **arquivo** (`AUTO_SERVER=TRUE`) garante persistência durante *cold starts* do Render.
- **Flyway** controla o versionamento do schema e a carga de dados **seed**, permitindo reprodutibilidade do ambiente de correção.
- **Security**: perfis `ADMIN` e `OPERADOR`. CSRF **ativo** apenas no fluxo **Web** (formularios), **ignorado** no REST/Swagger/H2 para facilitar testes.
- **DTO + Mapper**: isolamento entre entidade e transporte, facilitando validação e evolução.
- **Exportações**: XLSX (com cabeçalho estilizado, *freeze* e *autofilter*) e CSV compatível com Excel.
- **Oracle**: encapsulado em `OracleProcService` com criação condicional do `JdbcTemplate` **apenas** se houver `oracle.datasource.url`, evitando travas de ambiente.
- **MongoDB**: armazena **motos** e suas **movimentações** para consultas analíticas (somatórios por pátio/tipo, totalizações e amostras), mantendo o relacional simples para o CRUD transacional.
- **Deploy Render**: variáveis de ambiente habilitam Oracle/Mongo sem alterar código; rotas públicas para avaliação.

---

## 🖥️ Como Rodar Localmente

### Pré‑requisitos
- **JDK 17**
- **Maven** (ou usar o wrapper `mvnw`)

### Passos
```bash
# clonar e entrar
git clone <repo-url>
cd <repo>

# rodar
./mvnw spring-boot:run   # Mac/Linux
mvn spring-boot:run      # Windows
```

### Endpoints úteis (local)
- Web: `http://localhost:8080/web`
- Login: `http://localhost:8080/login`
- Swagger: `http://localhost:8080/swagger-ui.html`
- H2 Console: `http://localhost:8080/console`
    - JDBC URL: `jdbc:h2:mem:motogrid` | User: `sa` | Password: *(vazio)*

**Arquivos de configuração** (principais):
- `src/main/resources/application.properties` (dev padrão, H2 memória + Mongo local)
- `src/main/resources/application-oracle.properties` (Oracle como **banco principal** — use para rodar com Oracle)
- `src/main/resources/application-prod.properties` (deploy Render: H2 arquivo + variáveis de ambiente)

---

## 🔐 Perfis, Login e Autorização

| Usuário    | Senha | Perfis     |
|------------|:-----:|------------|
| `admin`    | `123` | `ADMIN`    |
| `operador` | `123` | `OPERADOR` |

- Público (`permitAll`): `/v3/api-docs/**`, `/swagger-ui.html`, `/swagger-ui/**`, `/console/**`, `/css/**`, `/img/**`, `/error`, `/login`, `/actuator/health`, `/actuator/info`.
- **Web (Thymeleaf)**:
    - `GET /web/**` → `ADMIN` **ou** `OPERADOR`
    - `POST/PUT/PATCH/DELETE /web/**` → **ADMIN**
- **REST (CRUD)**:
    - `GET /motos/**`, `GET /patios/**` → `ADMIN` **ou** `OPERADOR`
    - Modificações (`POST/PUT/DELETE`) → **ADMIN**
- **CSRF**: ativo no Web; **ignorado** para REST/Swagger/H2/actuator.

---

## 🎨 Frontend (Thymeleaf)

**Fragments**: `fragments/head`, `fragments/header`, `fragments/footer`  
**Páginas**: `home`, `login`, `access-denied`, `motos/{list,form}`, `patios/{list,form}`  
**Tema**: Bootswatch **Darkly** + ajustes em `static/css/app.css`  
Badges de status: `DISPONIVEL` (success), `EM_USO` (primary), `EM_MANUTENCAO` (warning), outros (secondary).

---

## 📊 Relatórios (Export XLSX/CSV)

- **XLSX**: botão **Exportar XLSX** na lista de Motos.  
  Endpoint usado pela UI:
  ```
  GET /web/motos/export.xlsx?status=<opcional>&patioId=<opcional>
  ```
- **CSV**: export compatível com Excel (BOM + `sep=,`/`;`).
- Filtros opcionais: `status` (`DISPONIVEL`, `EM_USO`, `EM_MANUTENCAO`, `INATIVA`) e `patioId`.

---

## 🗄️ Banco de Dados & Migrações

- **H2 (dev)**: em memória, recriado a cada execução.
- **Flyway** (auto):
    1. `V1__create_table_patio.sql`
    2. `V2__create_table_moto.sql`
    3. `V3__index_moto_placa.sql` (único em `MOTO.PLACA`)
    4. `V4__seed_base.sql` (pátios + motos iniciais)

---

## 🔎 API REST & Swagger

Acesse **/swagger-ui.html** para testar. Principais recursos:

### Pátios
- `GET /patios` (paginável), `GET /patios/{id}`
- `POST /patios` *(ADMIN)*
- `PUT /patios/{id}` *(ADMIN)*
- `DELETE /patios/{id}` *(ADMIN)*

### Motos
- `GET /motos` (paginável)
- `GET /motos/buscar/placa?placa=ABC`
- `GET /motos/buscar/status?status=DISPONIVEL`
- `POST /motos` *(ADMIN)*
- `PUT /motos/{id}` *(ADMIN)*
- `DELETE /motos/{id}` *(ADMIN)*

**Exemplo POST /motos**
```json
{
  "placa": "ABC1D23",
  "modelo": "Honda Biz",
  "status": "DISPONIVEL",
  "patioId": 1
}
```

---

## 🚨 Tratamento de Erros (PADRÃO)

`GlobalExceptionHandler` padroniza respostas JSON:
- **422** validação (lista por campo)
- **404** não encontrado
- **400** regra de negócio (ex.: status inválido; id divergente)
- **500** erro genérico

---

## 🔗 Integrações Oracle & MongoDB

### Oracle (função, procedure e DBMS_OUTPUT)

**Config opcional** (só cria `JdbcTemplate` Oracle se houver URL):
```properties
# application.properties (dev) — exemplo de credenciais acadêmicas
oracle.datasource.url=jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl
oracle.datasource.username=rmXXXXX
oracle.datasource.password=XXXXXX
oracle.datasource.driver-class-name=oracle.jdbc.OracleDriver
```

**Endpoints (Swagger):**
- `GET /api/oracle/validar-placa/{placa}` → executa **função** `pkg_motogrid.validar_placa`
- `GET /api/oracle/motos/procedure` → executa **procedure** `PKG_MOTOGRID.LISTAR_MOTOS_RC` (REF CURSOR)
- `GET /api/oracle/resumo/dbms-output` → captura linhas via **DBMS_OUTPUT**

> **Observação**: também existe um `OracleController` para consulta direta das evidências.


### MongoDB (import, índices e agregações)

**Executar Mongo local** (ex.: Docker):
```bash
docker run -d --name mongo -p 27017:27017 mongo:6
```

**Importar dataset** (duas formas):
1) **Via mongosh (OS shell)**:
```bash
# estando na pasta onde está o arquivo .js
mongosh --file mongo_setup_motogrid.js
# ou importar JSONL diretamente
mongoimport --uri "mongodb://localhost:27017/motogrid" -c motos --file motos.jsonl --jsonArray=false
```
2) **Dentro do mongosh** (prompt do shell):
```javascript
load('mongo_setup_motogrid.js')
```

**O script cria/garante**:
- DB `motogrid`, coleção `motos`
- **Índice único** em `placa`
- Carga de amostra a partir do `motos.jsonl` (ou *insertMany* fallback)

**Endpoints (Swagger):**
- `GET /api/mongo/motos?limit=50` — amostra de documentos
- `GET /api/mongo/motos/{placa}` — busca por placa (ignore case)
- `GET /api/mongo/stats/por-patio-tipo` — soma por pátio e tipo de movimentação
- `GET /api/mongo/stats/total?desde=YYYY-MM-DD` — total geral (filtro opc. por data)
- `GET /api/mongo/sample` — 2 docs para evidência
- `GET /api/mongo/indices` — lista de índices da coleção

**Modelo (resumo) — `MotoDoc`**
```json
{
  "id_moto": 1,
  "placa": "ABC1234",
  "modelo": "Honda CG 160",
  "cor": "Preta",
  "ano": 2020,
  "patio": "Patio Central",
  "movimentacoes": [
    {"tipo":"ENTRADA","data":"2024-05-01","valor":100.0,"funcionario":"Carlos Silva"},
    {"tipo":"SAIDA","data":"2024-05-05","valor": 90.0,"funcionario":"Ana Costa"}
  ]
}
```

---

## 🌐 Deploy (Render)

A aplicação está publicada em **https://java-mottu.onrender.com**.

**`application-prod.properties`** (trechos importantes):
```properties
server.port=${PORT:8080}

# H2 em arquivo (persiste entre reinícios do container)
spring.datasource.url=jdbc:h2:file:./data/motogrid;AUTO_SERVER=TRUE;MODE=LEGACY
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update

# Mongo (se houver instância externa)
spring.data.mongodb.uri=${MONGODB_URI:mongodb://localhost:27017/motogrid}

# Oracle (opcional no deploy)
oracle.datasource.url=${ORACLE_URL:}
oracle.datasource.username=${ORACLE_USER:}
oracle.datasource.password=${ORACLE_PASS:}
oracle.datasource.driver-class-name=oracle.jdbc.OracleDriver
```

> O primeiro acesso após inatividade pode levar alguns segundos (a instância “acorda”).

---

## 👥 Autores

- **Gabriel Gomes Mancera** — RM: 555427
- **Victor Hugo Carvalho** — RM: 558550
- **Juliana de Andrade Sousa** — RM: 558834

---

> Dúvidas ou correções? Abra uma *issue* ou nos chame! 🚀
