package senac.tsi.books.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import senac.tsi.books.config.ApiKeyStore;
import senac.tsi.books.config.DefaultApiResponses;
import senac.tsi.books.exceptions.RecursoNaoEncontradoException;

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
            description = "Cria uma chave para usar no header X-API-Key."
    )
    @ApiResponse(responseCode = "201", description = "Chave gerada com sucesso")
    @PostMapping("/generate")
    public ResponseEntity<ApiKeyResponse> gerar(@RequestParam String username) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiKeyResponse(apiKeyStore.generate(username)));
    }

    @Operation(
            summary = "Revogar API Key",
            description = "Desativa uma chave de API. Exige uma X-API-Key valida no header. Nao existe endpoint para listar chaves."
    )
    @ApiResponse(responseCode = "200", description = "Chave revogada com sucesso")
    @ApiResponse(responseCode = "404", description = "Chave nao encontrada")
    @DeleteMapping("/{keyValue}")
    public ApiKeyResponse revogar(@PathVariable String keyValue) {
        return apiKeyStore.revoke(keyValue)
                .map(ApiKeyResponse::new)
                .orElseThrow(() -> new RecursoNaoEncontradoException("API Key informada nao foi encontrada"));
    }

    public record ApiKeyResponse(
            String username,
            String keyValue,
            boolean active,
            LocalDateTime createdAt
    ) {
        public ApiKeyResponse(ApiKeyStore.ApiKeyData data) {
            this(data.username(), data.keyValue(), data.active(), data.createdAt());
        }
    }
}
