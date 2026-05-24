# LolAPI

API RESTful em Spring Boot para gerenciamento de torneios de League of Legends.

## Tecnologias

- Java 21
- Spring Boot 4.0.3
- Maven Wrapper
- Spring Web MVC
- Spring Data JPA
- H2 Database
- Bean Validation
- Spring HATEOAS
- Springdoc OpenAPI / Swagger

## Como executar

```bash
./mvnw spring-boot:run
```

No Windows:

```bash
mvnw.cmd spring-boot:run
```

## URLs uteis

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`
- H2 Console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Usuario: `sa`
- Senha: vazia

## Chaves de teste

O projeto sobe com duas chaves iniciais:

- Admin: `admin-test-key`
- Usuario comum: `user-test-key`

Use a chave no header:

```http
X-API-Key: admin-test-key
```

Tambem e possivel gerar uma nova chave:

```http
POST /api-keys/generate?username=aluno&role=ADMIN
```

## Recursos implementados

- 5 entidades principais: `Champion`, `Player`, `Team`, `Coach`, `MatchGame`
- Relacionamentos `One-to-One`, `One-to-Many`, `Many-to-One` e `Many-to-Many`
- CRUD completo por entidade
- Buscas customizadas por entidade
- Listagens e buscas paginadas com `PagedModel`
- HATEOAS com links `self`, `update`, `delete` e listagem
- Bean Validation nas entidades
- Tratamento global de erros com `@RestControllerAdvice`
- Swagger/OpenAPI com esquema de seguranca `X-API-Key`
- API Key com roles `USER` e `ADMIN`
- `DELETE` exige chave `ADMIN`
- Idempotencia em `POST` com `X-Idempotency-Key`
- Rate limit fixo de **20 requisicoes por minuto**
- CORS configurado para frontends locais
- Versionamento por header em `/players/{id}` com `X-API-Version`

## Exemplos

Buscar jogador v1, com HATEOAS:

```http
GET /players/1
X-API-Key: admin-test-key
```

Buscar jogador v2, resumido:

```http
GET /players/1
X-API-Key: admin-test-key
X-API-Version: 2
```

Criar campeao com idempotencia:

```http
POST /champions
X-API-Key: admin-test-key
X-Idempotency-Key: exemplo-001
Content-Type: application/json

{
  "nome": "Ahri",
  "role": "MID"
}
```

Se a mesma requisicao for repetida com a mesma chave idempotente, a API retorna a resposta armazenada e adiciona:

```http
Idempotency-Replayed: true
```

## Testes

```bash
mvnw.cmd test
```

Os testes verificam contexto da aplicacao, buscas por ID, buscas paginadas, API Key, permissao de `DELETE`, idempotencia, rate limit de 20/min e versionamento por header.
