package senac.tsi.books.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import senac.tsi.books.config.DefaultApiResponses;
import senac.tsi.books.dto.PlayerDTOV2;
import senac.tsi.books.entities.Player;
import senac.tsi.books.exceptions.RecursoNaoEncontradoException;
import senac.tsi.books.repositories.PlayerRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api-version")
@Tag(name = "API Version")
@DefaultApiResponses
public class ApiVersionController {

    @Autowired
    private PlayerRepository repository;

    @Operation(summary = "Buscar jogador na versao 1", description = "Demonstra a v1 do recurso de jogador com HATEOAS e link para a v2.")
    @ApiResponse(responseCode = "200", description = "Jogador v1 encontrado")
    @ApiResponse(responseCode = "404", description = "Jogador nao encontrado")
    @GetMapping("/v1/players/{id}")
    public EntityModel<Player> buscarJogadorV1(@PathVariable Long id) {
        Player player = buscarPlayer(id);
        return EntityModel.of(player,
                linkTo(methodOn(ApiVersionController.class).buscarJogadorV1(id)).withSelfRel(),
                linkTo(methodOn(ApiVersionController.class).buscarJogadorV2(id)).withRel("v2"),
                linkTo(methodOn(PlayerController.class).buscarPorId(id)).withRel("crud-jogador")
        );
    }

    @Operation(summary = "Buscar jogador na versao 2", description = "Demonstra a v2 do recurso de jogador em formato resumido, com HATEOAS e link para a v1.")
    @ApiResponse(responseCode = "200", description = "Jogador v2 encontrado")
    @ApiResponse(responseCode = "404", description = "Jogador nao encontrado")
    @GetMapping("/v2/players/{id}")
    public EntityModel<PlayerDTOV2> buscarJogadorV2(@PathVariable Long id) {
        return EntityModel.of(new PlayerDTOV2(buscarPlayer(id)),
                linkTo(methodOn(ApiVersionController.class).buscarJogadorV2(id)).withSelfRel(),
                linkTo(methodOn(ApiVersionController.class).buscarJogadorV1(id)).withRel("v1"),
                linkTo(methodOn(PlayerController.class).buscarPorId(id)).withRel("crud-jogador")
        );
    }

    private Player buscarPlayer(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Jogador com ID " + id + " nao encontrado"));
    }
}
