package senac.tsi.books.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import senac.tsi.books.config.DefaultApiResponses;
import senac.tsi.books.config.PagedModelBuilder;
import senac.tsi.books.dto.PlayerDTOV2;
import senac.tsi.books.entities.Player;
import senac.tsi.books.exceptions.RecursoNaoEncontradoException;
import senac.tsi.books.repositories.PlayerRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v2/players")
@Tag(name = "Jogadores")
@DefaultApiResponses
public class PlayerV2Controller {

    @Autowired
    private PlayerRepository repository;

    @Operation(summary = "Listar jogadores - v2", description = "Retorna jogadores em formato resumido de v2, com nome do time aberto no DTO.")
    @ApiResponse(responseCode = "200", description = "Lista v2 retornada com sucesso")
    @GetMapping
    public PagedModel<EntityModel<PlayerDTOV2>> listar(Pageable pageable) {
        return PagedModelBuilder.from(repository.findAll(pageable).map(PlayerDTOV2::new), this::montarModelo);
    }

    @Operation(summary = "Buscar jogador por ID - v2", description = "Retorna um jogador em formato resumido de v2.")
    @ApiResponse(responseCode = "200", description = "Jogador v2 encontrado")
    @ApiResponse(responseCode = "404", description = "Jogador nao encontrado")
    @GetMapping("/{id}")
    public EntityModel<PlayerDTOV2> buscarPorId(@PathVariable Long id) {
        return montarModelo(new PlayerDTOV2(buscarPlayer(id)));
    }

    private EntityModel<PlayerDTOV2> montarModelo(PlayerDTOV2 player) {
        Long id = player.getId();
        return EntityModel.of(player,
                linkTo(methodOn(PlayerV2Controller.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(PlayerController.class).buscarPorId(id)).withRel("v1"),
                linkTo(methodOn(PlayerV2Controller.class).listar(Pageable.unpaged())).withRel("lista-jogadores-v2")
        );
    }

    private Player buscarPlayer(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Jogador com ID " + id + " nao encontrado"));
    }
}
