package senac.tsi.books.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import senac.tsi.books.config.ApiKeyStore;
import senac.tsi.books.config.DefaultApiResponses;
import senac.tsi.books.entities.ApiKeyRole;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api-keys")
@Tag(name = "API Keys")
@DefaultApiResponses
public class ApiKeyController {

    private final ApiKeyStore apiKeyStore;

    public ApiKeyController(ApiKeyStore apiKeyStore) {
        this.apiKeyStore = apiKeyStore;
    }

    @Operation(
            summary = "Gerar API Key",
            description = "Cria uma chave para usar no header X-API-Key. Por padrao gera ADMIN para facilitar testes no Swagger."
    )
    @ApiResponse(responseCode = "201", description = "Chave gerada com sucesso")
    @PostMapping("/generate")
    public ResponseEntity<ApiKeyResponse> gerar(
            @RequestParam String username,
            @RequestParam(defaultValue = "ADMIN") ApiKeyRole role) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiKeyResponse(apiKeyStore.generate(username, role)));
    }

    public record ApiKeyResponse(
            String username,
            String keyValue,
            ApiKeyRole role,
            boolean active,
            LocalDateTime createdAt
    ) {
        public ApiKeyResponse(ApiKeyStore.ApiKeyData data) {
            this(data.username(), data.keyValue(), data.role(), data.active(), data.createdAt());
        }
    }
}
