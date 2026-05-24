package senac.tsi.books.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import senac.tsi.books.config.DefaultApiResponses;
import senac.tsi.books.entities.ApiKey;
import senac.tsi.books.entities.ApiKeyRole;
import senac.tsi.books.exceptions.RecursoNaoEncontradoException;
import senac.tsi.books.repositories.ApiKeyRepository;

import java.util.Map;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping({"/api-keys", "/api/v1/api-keys"})
@Tag(name = "API Keys")
@DefaultApiResponses
public class ApiKeyController {

    @Autowired
    private ApiKeyRepository repository;

    @Operation(
            summary = "Gerar API Key",
            description = "Cria uma chave de API para uso no header X-API-Key. Endpoint publico para testes; por padrao gera role ADMIN para permitir testar todos os endpoints protegidos."
    )
    @ApiResponse(responseCode = "201", description = "Chave gerada com sucesso")
    @PostMapping("/generate")
    public ResponseEntity<ApiKey> gerar(
            @RequestParam String username,
            @RequestParam(defaultValue = "ADMIN") ApiKeyRole role) {
        ApiKey apiKey = new ApiKey(username, UUID.randomUUID().toString(), role);
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(apiKey));
    }

    @Operation(summary = "Buscar API Key por ID", description = "Retorna uma chave pelo ID. Nao existe endpoint de listagem de chaves por seguranca.")
    @ApiResponse(responseCode = "200", description = "Chave encontrada")
    @ApiResponse(responseCode = "404", description = "Chave nao encontrada")
    @GetMapping("/{id}")
    public EntityModel<ApiKey> buscarPorId(@PathVariable Long id) {
        return montarModelo(buscarApiKey(id));
    }

    @Operation(summary = "Atualizar API Key", description = "Atualiza usuario, role ou status ativo da chave")
    @ApiResponse(responseCode = "200", description = "Chave atualizada com sucesso")
    @PutMapping("/{id}")
    public ApiKey atualizar(@PathVariable Long id, @Valid @RequestBody ApiKey apiKey) {
        ApiKey existente = buscarApiKey(id);

        existente.setUsername(apiKey.getUsername());
        existente.setRole(apiKey.getRole());
        existente.setActive(apiKey.isActive());
        return repository.save(existente);
    }

    @Operation(summary = "Revogar API Key", description = "Desativa uma chave de API")
    @ApiResponse(responseCode = "200", description = "Chave revogada com sucesso")
    @ApiResponse(responseCode = "404", description = "Chave nao encontrada")
    @DeleteMapping("/{id}")
    public Map<String, Object> revogar(@PathVariable Long id) {
        ApiKey existente = buscarApiKey(id);

        existente.setActive(false);
        repository.save(existente);
        return Map.of("id", id, "active", false, "mensagem", "API Key revogada com sucesso.");
    }

    private EntityModel<ApiKey> montarModelo(ApiKey apiKey) {
        Long id = apiKey.getId();
        return EntityModel.of(apiKey,
                linkTo(methodOn(ApiKeyController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(ApiKeyController.class).atualizar(id, null)).withRel("update"),
                linkTo(methodOn(ApiKeyController.class).revogar(id)).withRel("revoke")
        );
    }

    private ApiKey buscarApiKey(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("API Key com ID " + id + " nao encontrada"));
    }
}
