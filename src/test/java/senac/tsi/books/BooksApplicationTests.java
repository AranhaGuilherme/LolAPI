package senac.tsi.books;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BooksApplicationTests {

    private static final String ADMIN_KEY = "admin-test-key";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void dadosIniciaisDeLolSaoCarregados() throws Exception {
        mockMvc.perform(get("/api/v1/champions").header("X-API-Key", ADMIN_KEY).param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(32)));

        mockMvc.perform(get("/api/v1/teams").header("X-API-Key", ADMIN_KEY).param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(6)));

        mockMvc.perform(get("/api/v1/players").header("X-API-Key", ADMIN_KEY).param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(30)));

        mockMvc.perform(get("/api/v1/matchgames").header("X-API-Key", ADMIN_KEY).param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(8)));
    }

    @Test
    void endpointsDeBuscaPorIdNaoRetornamErroInterno() throws Exception {
        mockMvc.perform(get("/api/v1/champions/1").header("X-API-Key", ADMIN_KEY)).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/players/1").header("X-API-Key", ADMIN_KEY)).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/teams/1").header("X-API-Key", ADMIN_KEY)).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/coaches/1").header("X-API-Key", ADMIN_KEY)).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/matchgames/1").header("X-API-Key", ADMIN_KEY)).andExpect(status().isOk());
    }

    @Test
    void buscasCustomizadasSaoPaginadas() throws Exception {
        mockMvc.perform(get("/api/v1/champions/buscar").header("X-API-Key", ADMIN_KEY).param("nome", "ori").param("size", "2"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/players/buscar").header("X-API-Key", ADMIN_KEY).param("nome", "lee").param("size", "2"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/teams/buscar").header("X-API-Key", ADMIN_KEY).param("nome", "t").param("size", "2"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/coaches/buscar").header("X-API-Key", ADMIN_KEY).param("nome", "k").param("size", "2"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/matchgames/buscar").header("X-API-Key", ADMIN_KEY).param("duracao", "3").param("size", "2"))
                .andExpect(status().isOk());
    }

    @Test
    void apiKeyEObrigatoriaNosEndpointsProtegidos() throws Exception {
        mockMvc.perform(get("/api/v1/champions/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usuarioComumNaoPodeDeletar() throws Exception {
        mockMvc.perform(delete("/api/v1/champions/1").header("X-API-Key", "user-test-key"))
                .andExpect(status().isForbidden());
    }

    @Test
    void endpointPublicoGeraApiKey() throws Exception {
        mockMvc.perform(post("/api-keys/generate")
                        .param("username", "teste-swagger")
                        .param("role", "USER"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.keyValue").exists());
    }

    @Test
    void endpointPublicoGeraApiKeyAdminPorPadraoParaTestarTodaApi() throws Exception {
        String apiKey = gerarApiKeyPadrao("admin-gerado-padrao");

        MvcResult criado = mockMvc.perform(post("/api/v1/champions")
                        .header("X-API-Key", apiKey)
                        .header("X-Idempotency-Key", "admin-generated-can-create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Campeao Gerado Admin\",\"role\":\"MID\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = extrairId(criado.getResponse().getContentAsString());

        mockMvc.perform(delete("/api/v1/champions/" + id).header("X-API-Key", apiKey))
                .andExpect(status().isNoContent());
    }

    @Test
    void apiKeysNaoPossuemEndpointDeListagem() throws Exception {
        String apiKey = gerarApiKeyPadrao("admin-sem-listagem");

        mockMvc.perform(get("/api-keys").header("X-API-Key", apiKey))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void rotasVersionadasV1EV2EstaoDisponiveis() throws Exception {
        String apiKey = gerarApiKeyPadrao("admin-versionamento");

        mockMvc.perform(get("/api/v1/players/1").header("X-API-Key", apiKey))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v2/players/1").header("X-API-Key", apiKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamNome", is("T1")));
    }

    @Test
    void idempotenciaReaproveitaRespostaNoPost() throws Exception {
        String apiKey = gerarApiKey("idempotency-test", "ADMIN");
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
        String apiKey = gerarApiKey("idempotency-conflict-test", "ADMIN");

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
        String apiKey = gerarApiKey("rate-limit-test", "ADMIN");
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

    private String gerarApiKey(String username, String role) throws Exception {
        MvcResult result = mockMvc.perform(post("/api-keys/generate")
                        .param("username", username)
                        .param("role", role))
                .andExpect(status().isCreated())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        int start = body.indexOf("\"keyValue\":\"") + 12;
        int end = body.indexOf("\"", start);
        return body.substring(start, end);
    }

    private String gerarApiKeyPadrao(String username) throws Exception {
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
