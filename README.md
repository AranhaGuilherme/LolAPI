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

- `professor-test-key`
- `aluno-test-key`

Use a chave no header:

```http
X-API-Key: professor-test-key
```

Tambem e possivel gerar uma nova chave:

```http
POST /api-keys/generate?username=aluno
```

Qualquer chave valida autoriza as operacoes protegidas.
Nao existe endpoint para listar chaves de API, para evitar exposicao de chaves cadastradas.

Para revogar uma chave, use uma chave valida no header:

```http
DELETE /api-keys/{keyValue}
X-API-Key: professor-test-key
```

## Versionamento

A API mantem o CRUD principal em `/api/v1/...`. A demonstracao de versionamento fica separada em uma area propria:

- v1: `/api/v1/champions`, `/api/v1/players`, `/api/v1/teams`, `/api/v1/coaches`, `/api/v1/matchgames`
- API Version v1: `/api-version/v1/players/{id}`
- API Version v2: `/api-version/v2/players/{id}`

## Dados iniciais

Ao iniciar com H2, a API ja carrega uma massa demonstrativa de League of Legends:

- 32 campeoes distribuidos entre `TOP`, `JUNGLE`, `MID`, `ADC` e `SUPPORT`
- 6 times: `T1`, `Gen.G`, `G2 Esports`, `Fnatic`, `Cloud9` e `LOUD`
- 30 jogadores, com 5 jogadores por time e campeoes associados
- 6 coaches, cada um associado a um time
- 8 partidas com times, vencedor, jogadores e campeoes relacionados

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
- Documentacao de erros `400`, `401`, `404`, `409`, `429` e `500`
- API Key por header `X-API-Key`, sem cargos
- Geracao publica de API Key de teste
- Revogacao de API Key sem endpoint de listagem
- Idempotencia em `POST` com `X-Idempotency-Key`
- Rate limit fixo de **20 requisicoes por minuto**
- CORS configurado para frontends locais
- Versionamento demonstrativo separado em `/api-version/v1/...` e `/api-version/v2/...`

## Exemplos

Buscar jogador no CRUD principal:

```http
GET /api/v1/players/1
X-API-Key: professor-test-key
```

Comparar jogador na area de versionamento:

```http
GET /api-version/v1/players/1
X-API-Key: professor-test-key
```

```http
GET /api-version/v2/players/1
X-API-Key: professor-test-key
```

Criar campeao com idempotencia:

```http
POST /api/v1/champions
X-API-Key: professor-test-key
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

Os testes verificam contexto da aplicacao, carga inicial de dados de LoL, buscas por ID, buscas paginadas, API Key, geracao e revogacao de chaves, ausencia de listagem de chaves, idempotencia, rate limit de 20/min e versionamento separado em API Version.
