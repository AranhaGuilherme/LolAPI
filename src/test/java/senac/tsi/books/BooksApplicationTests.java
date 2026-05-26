package senac.tsi.books;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BooksApplicationTests {

    private static final String TEST_KEY = "professor-test-key";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void dadosIniciaisDeLolSaoCarregados() throws Exception {
        mockMvc.perform(get("/api/v1/champions").header("X-API-Key", TEST_KEY).param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(32)));

        mockMvc.perform(get("/api/v1/teams").header("X-API-Key", TEST_KEY).param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(6)));

        mockMvc.perform(get("/api/v1/players").header("X-API-Key", TEST_KEY).param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(30)));

        mockMvc.perform(get("/api/v1/matchgames").header("X-API-Key", TEST_KEY).param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(8)));
    }

    @Test
    void endpointsDeBuscaPorIdNaoRetornamErroInterno() throws Exception {
        mockMvc.perform(get("/api/v1/champions/1").header("X-API-Key", TEST_KEY)).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/players/1").header("X-API-Key", TEST_KEY)).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/teams/1").header("X-API-Key", TEST_KEY)).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/coaches/1").header("X-API-Key", TEST_KEY)).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/matchgames/1").header("X-API-Key", TEST_KEY)).andExpect(status().isOk());
    }

    @Test
    void buscasCustomizadasSaoPaginadas() throws Exception {
        mockMvc.perform(get("/api/v1/champions/buscar").header("X-API-Key", TEST_KEY).param("nome", "ori").param("size", "2"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/players/buscar").header("X-API-Key", TEST_KEY).param("nome", "lee").param("size", "2"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/teams/buscar").header("X-API-Key", TEST_KEY).param("nome", "t").param("size", "2"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/coaches/buscar").header("X-API-Key", TEST_KEY).param("nome", "k").param("size", "2"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/matchgames/buscar").header("X-API-Key", TEST_KEY).param("duracao", "3").param("size", "2"))
                .andExpect(status().isOk());
    }

    @Test
    void apiKeyEObrigatoriaNosEndpointsProtegidos() throws Exception {
        mockMvc.perform(get("/api/v1/champions/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void swaggerNaoForcaServidorLocalhost() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.servers[0].url", is("/")));
    }

    @Test
    void swaggerDocumentaHeaderDeIdempotenciaNosPosts() throws Exception {
        MvcResult result = mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andReturn();

        String openApi = result.getResponse().getContentAsString();
        assertTrue(openApi.contains("\"name\":\"X-Idempotency-Key\""));
        assertTrue(openApi.contains("\"in\":\"header\""));
    }

    @Test
    void apiKeyValidaPodeDeletar() throws Exception {
        String apiKey = gerarApiKey("chave-valida-delete");

        MvcResult criado = mockMvc.perform(post("/api/v1/champions")
                        .header("X-API-Key", apiKey)
                        .header("X-Idempotency-Key", "valid-key-can-delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Campeao Delete\",\"role\":\"MID\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = extrairId(criado.getResponse().getContentAsString());

        mockMvc.perform(delete("/api/v1/champions/" + id).header("X-API-Key", apiKey))
                .andExpect(status().isNoContent());
    }

    @Test
    void endpointPublicoGeraApiKey() throws Exception {
        mockMvc.perform(post("/api-keys/generate")
                        .param("username", "teste-swagger"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.keyValue").exists());
    }

    @Test
    void endpointPublicoGeraApiKeyParaTestarTodaApi() throws Exception {
        String apiKey = gerarApiKey("chave-gerada");

        MvcResult criado = mockMvc.perform(post("/api/v1/champions")
                        .header("X-API-Key", apiKey)
                        .header("X-Idempotency-Key", "generated-key-can-create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Campeao Gerado\",\"role\":\"MID\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = extrairId(criado.getResponse().getContentAsString());

        mockMvc.perform(delete("/api/v1/champions/" + id).header("X-API-Key", apiKey))
                .andExpect(status().isNoContent());
    }

    @Test
    void postsComIdZeroGeramIdAutomaticamente() throws Exception {
        String apiKey = gerarApiKey("id-zero-posts");

        mockMvc.perform(post("/api/v1/champions")
                        .header("X-API-Key", apiKey)
                        .header("X-Idempotency-Key", "id-zero-champion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":0,\"nome\":\"Campeao ID Zero\",\"role\":\"MID\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", greaterThan(0)));

        mockMvc.perform(post("/api/v1/coaches")
                        .header("X-API-Key", apiKey)
                        .header("X-Idempotency-Key", "id-zero-coach")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":0,\"nome\":\"Coach ID Zero\",\"experiencia\":4}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", greaterThan(0)));

        mockMvc.perform(post("/api/v1/teams")
                        .header("X-API-Key", apiKey)
                        .header("X-Idempotency-Key", "id-zero-team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":0,\"nome\":\"Time ID Zero\",\"regiao\":\"BR\",\"coachId\":0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", greaterThan(0)));

        mockMvc.perform(post("/api/v1/players")
                        .header("X-API-Key", apiKey)
                        .header("X-Idempotency-Key", "id-zero-player")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":0,\"nome\":\"Jogador ID Zero\",\"nick\":\"Zero\",\"role\":\"ADC\",\"teamId\":0,\"championIds\":[0]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", greaterThan(0)));

        mockMvc.perform(post("/api/v1/matchgames")
                        .header("X-API-Key", apiKey)
                        .header("X-Idempotency-Key", "id-zero-matchgame")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 0,
                                  "duracao": "35:00",
                                  "timeAId": 1,
                                  "timeBId": 2,
                                  "vencedorId": 1,
                                  "playerIds": [1],
                                  "championIds": [1]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", greaterThan(0)));
    }

    @Test
    void entradasInvalidasConhecidasNaoRetornamErroInterno() throws Exception {
        String apiKey = gerarApiKey("sem-erro-500");

        mockMvc.perform(post("/api/v1/players")
                        .header("X-API-Key", apiKey)
                        .header("X-Idempotency-Key", "invalid-team-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":0,\"nome\":\"Jogador Invalido\",\"nick\":\"Invalid\",\"role\":\"ADC\",\"teamId\":999999}"))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/v1/matchgames")
                        .header("X-API-Key", apiKey)
                        .header("X-Idempotency-Key", "invalid-match-required-teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":0,\"duracao\":\"35:00\",\"timeAId\":0,\"timeBId\":0}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/v1/champions")
                        .header("X-API-Key", apiKey)
                        .header("X-Idempotency-Key", "invalid-enum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":0,\"nome\":\"Enum Invalido\",\"role\":\"meio\"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(delete("/api/v1/teams/1").header("X-API-Key", apiKey))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void apiKeyValidaPodeRevogarOutraApiKey() throws Exception {
        String apiKey = gerarApiKey("chave-para-revogar");

        mockMvc.perform(delete("/api-keys/" + apiKey).header("X-API-Key", TEST_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active", is(false)));

        mockMvc.perform(get("/api/v1/champions/1").header("X-API-Key", apiKey))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void apiKeysNaoPossuemEndpointDeListagem() throws Exception {
        String apiKey = gerarApiKey("sem-listagem");

        mockMvc.perform(get("/api-keys").header("X-API-Key", apiKey))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void apiVersionExpoeV1EV2EmSecaoSeparada() throws Exception {
        String apiKey = gerarApiKey("versionamento");

        mockMvc.perform(get("/api-version/v1/players/1").header("X-API-Key", apiKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.v2.href").exists());

        mockMvc.perform(get("/api-version/v2/players/1").header("X-API-Key", apiKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamNome", is("T1")))
                .andExpect(jsonPath("$._links.v1.href").exists());
    }

    @Test
    void idempotenciaReaproveitaRespostaNoPost() throws Exception {
        String apiKey = gerarApiKey("idempotency-test");
        String body = """
                {
                  "nome": "Teste Idempotente",
                  "role": "MID"
                }
                """;

        mockMvc.perform(post("/api/v1/champions")
                        .header("X-API-Key", apiKey)
                        .header("X-Idempotency-Key", "champion-idempotency-test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/champions")
                        .header("X-API-Key", apiKey)
                        .header("X-Idempotency-Key", "champion-idempotency-test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Idempotency-Replayed", "true"));
    }

    @Test
    void idempotenciaRejeitaMesmaChaveEmOutroEndpoint() throws Exception {
        String apiKey = gerarApiKey("idempotency-conflict-test");

        mockMvc.perform(post("/api/v1/champions")
                        .header("X-API-Key", apiKey)
                        .header("X-Idempotency-Key", "same-key-other-endpoint")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Conflito Idempotente\",\"role\":\"MID\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/coaches")
                        .header("X-API-Key", apiKey)
                        .header("X-Idempotency-Key", "same-key-other-endpoint")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Coach Conflito\",\"experiencia\":3}"))
                .andExpect(status().isConflict());
    }

    @Test
    void rateLimitPermiteApenas20RequisicoesPorMinuto() throws Exception {
        String apiKey = gerarApiKey("rate-limit-test");
        for (int i = 0; i < 20; i++) {
            mockMvc.perform(get("/api/v1/champions/1").header("X-API-Key", apiKey))
                    .andExpect(status().isOk())
                    .andExpect(header().string("X-RateLimit-Limit", "20"));
        }

        mockMvc.perform(get("/api/v1/champions/1").header("X-API-Key", apiKey))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string("X-RateLimit-Limit", "20"))
                .andExpect(header().exists("Retry-After"));
    }

    private String gerarApiKey(String username) throws Exception {
        MvcResult result = mockMvc.perform(post("/api-keys/generate")
                        .param("username", username))
                .andExpect(status().isCreated())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        int start = body.indexOf("\"keyValue\":\"") + 12;
        int end = body.indexOf("\"", start);
        return body.substring(start, end);
    }

    private Long extrairId(String body) {
        int start = body.indexOf("\"id\":") + 5;
        int end = body.indexOf(",", start);
        if (end < 0) {
            end = body.indexOf("}", start);
        }
        return Long.valueOf(body.substring(start, end));
    }
}
