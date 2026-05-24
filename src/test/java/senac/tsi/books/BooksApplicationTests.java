package senac.tsi.books;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;

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
    void endpointsDeBuscaPorIdNaoRetornamErroInterno() throws Exception {
        mockMvc.perform(get("/champions/1").header("X-API-Key", ADMIN_KEY)).andExpect(status().isOk());
        mockMvc.perform(get("/players/1").header("X-API-Key", ADMIN_KEY)).andExpect(status().isOk());
        mockMvc.perform(get("/teams/1").header("X-API-Key", ADMIN_KEY)).andExpect(status().isOk());
        mockMvc.perform(get("/coaches/1").header("X-API-Key", ADMIN_KEY)).andExpect(status().isOk());
        mockMvc.perform(get("/matchgames/1").header("X-API-Key", ADMIN_KEY)).andExpect(status().isOk());
    }

    @Test
    void buscasCustomizadasSaoPaginadas() throws Exception {
        mockMvc.perform(get("/champions/buscar").header("X-API-Key", ADMIN_KEY).param("nome", "ori").param("size", "2"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/players/buscar").header("X-API-Key", ADMIN_KEY).param("nome", "lee").param("size", "2"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/teams/buscar").header("X-API-Key", ADMIN_KEY).param("nome", "t").param("size", "2"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/coaches/buscar").header("X-API-Key", ADMIN_KEY).param("nome", "k").param("size", "2"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/matchgames/buscar").header("X-API-Key", ADMIN_KEY).param("duracao", "3").param("size", "2"))
                .andExpect(status().isOk());
    }

    @Test
    void apiKeyEObrigatoriaNosEndpointsProtegidos() throws Exception {
        mockMvc.perform(get("/champions/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usuarioComumNaoPodeDeletar() throws Exception {
        mockMvc.perform(delete("/champions/1").header("X-API-Key", "user-test-key"))
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
    void idempotenciaReaproveitaRespostaNoPost() throws Exception {
        String apiKey = gerarApiKey("idempotency-test", "ADMIN");
        String body = """
                {
                  "nome": "Teste Idempotente",
                  "role": "MID"
                }
                """;

        mockMvc.perform(post("/champions")
                        .header("X-API-Key", apiKey)
                        .header("X-Idempotency-Key", "champion-idempotency-test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/champions")
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

        mockMvc.perform(post("/champions")
                        .header("X-API-Key", apiKey)
                        .header("X-Idempotency-Key", "same-key-other-endpoint")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Conflito Idempotente\",\"role\":\"MID\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/coaches")
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
            mockMvc.perform(get("/champions/1").header("X-API-Key", apiKey))
                    .andExpect(status().isOk())
                    .andExpect(header().string("X-RateLimit-Limit", "20"));
        }

        mockMvc.perform(get("/champions/1").header("X-API-Key", apiKey))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string("X-RateLimit-Limit", "20"))
                .andExpect(header().exists("Retry-After"));
    }

    @Test
    void versionamentoPorHeaderRetornaV2Resumida() throws Exception {
        mockMvc.perform(get("/players/1")
                        .header("X-API-Key", ADMIN_KEY)
                        .header("X-API-Version", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamNome", is("T1")));
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
}
