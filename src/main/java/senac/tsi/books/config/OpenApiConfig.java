package senac.tsi.books.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI lolApiOpenAPI() {
        String schemeName = "X-API-Key";
        return new OpenAPI()
                .info(new Info()
                        .title("LolAPI - Torneios de League of Legends")
                        .version("2.0")
                        .description("""
                                API RESTful academica desenvolvida em Spring Boot para gerenciamento de torneios de League of Legends.

                                **Dominio principal:** campeoes, jogadores, times, coaches e partidas.

                                **Autorizacao:** use o botao **Authorize** do Swagger e informe uma chave no header `X-API-Key`.
                                Para testes, gere uma chave em `POST /api-keys/generate`; por padrao ela sai como `ADMIN`.

                                **Idempotencia:** operacoes `POST` das entidades principais exigem o header `X-Idempotency-Key`.

                                **Rate limit:** limite fixo de 20 requisicoes por minuto por cliente/chave.

                                **Versionamento:** rotas v1 estao disponiveis em `/api/v1/...`; a v2 demonstrativa de jogadores esta em `/api/v2/players`.
                                """)
                )
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Ambiente local")
                ))
                .tags(List.of(
                        new Tag().name("API Keys").description("Geracao e gerenciamento minimo de chaves de API"),
                        new Tag().name("Campeoes").description("CRUD e consultas de campeoes de League of Legends"),
                        new Tag().name("Jogadores").description("CRUD, consultas e versionamento v1/v2 de jogadores"),
                        new Tag().name("Times").description("CRUD e consultas de times"),
                        new Tag().name("Coaches").description("CRUD e consultas de coaches"),
                        new Tag().name("Partidas").description("CRUD e consultas de partidas")
                ))
                .components(new Components().addSecuritySchemes(schemeName,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name(ApiKeyFilter.API_KEY_HEADER)))
                .addSecurityItem(new SecurityRequirement().addList(schemeName));
    }
}
