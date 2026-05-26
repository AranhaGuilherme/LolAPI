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
                                Para testes, gere uma chave em `POST /api-keys/generate`.

                                **Idempotencia:** operacoes `POST` das entidades principais exigem o header `X-Idempotency-Key`.

                                **Criacao:** em `POST`, envie `id: 0` ou omita o campo `id`; a API deixa o banco gerar o proximo ID automaticamente.

                                **Rate limit:** limite fixo de 20 requisicoes por minuto por cliente/chave.

                                **Versionamento:** o CRUD principal fica em `/api/v1/...`; a demonstracao de v1/v2 fica separada em `/api-version/v1/players/{id}` e `/api-version/v2/players/{id}`.
                                """)
                )
                .servers(List.of(
                        new Server().url("/").description("Mesmo host e porta do Swagger")
                ))
                .tags(List.of(
                        new Tag().name("API Keys").description("Geracao e gerenciamento minimo de chaves de API"),
                        new Tag().name("Campeoes").description("CRUD e consultas de campeoes de League of Legends"),
                        new Tag().name("Jogadores").description("CRUD e consultas de jogadores"),
                        new Tag().name("API Version").description("Demonstracao separada de versionamento v1/v2 com HATEOAS"),
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
