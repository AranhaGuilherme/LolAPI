package senac.tsi.books.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI lolApiOpenAPI() {
        String schemeName = "X-API-Key";
        return new OpenAPI()
                .info(new Info()
                        .title("LolAPI")
                        .version("1.0")
                        .description("API RESTful para gerenciamento de torneios de League of Legends."))
                .components(new Components().addSecuritySchemes(schemeName,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name(ApiKeyFilter.API_KEY_HEADER)))
                .addSecurityItem(new SecurityRequirement().addList(schemeName));
    }
}
