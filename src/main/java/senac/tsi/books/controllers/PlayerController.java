package senac.tsi.books.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
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
import senac.tsi.books.config.PagedModelBuilder;
import senac.tsi.books.entities.Player;
import senac.tsi.books.entities.Team;
import senac.tsi.books.exceptions.RecursoNaoEncontradoException;
import senac.tsi.books.repositories.PlayerRepository;
import senac.tsi.books.repositories.TeamRepository;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/players")
@Tag(name = "Jogadores")
@DefaultApiResponses
public class PlayerController {

    @Autowired
    private PlayerRepository repository;

    @Autowired
    private TeamRepository teamRepository;

    @Operation(summary = "Listar jogadores", description = "Retorna todos os jogadores com paginacao")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public PagedModel<EntityModel<Player>> listar(Pageable pageable) {
        return PagedModelBuilder.from(repository.findAll(pageable), this::montarModelo);
    }

    @Operation(summary = "Buscar jogador por ID", description = "Retorna um jogador com HATEOAS")
    @ApiResponse(responseCode = "200", description = "Jogador encontrado")
    @ApiResponse(responseCode = "404", description = "Jogador nao encontrado")
    @GetMapping("/{id}")
    public EntityModel<Player> buscarPorId(@PathVariable Long id) {
        return montarModelo(buscarPlayer(id));
    }

    @Operation(
            summary = "Criar jogador",
            description = "Cria um novo jogador",
            parameters = @Parameter(
                    name = "X-Idempotency-Key",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "Chave unica para evitar processamento duplicado do POST",
                    example = "player-001"
            )
    )
    @ApiResponse(responseCode = "201", description = "Jogador criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados invalidos")
    @PostMapping
    public ResponseEntity<Player> criar(@Valid @RequestBody Player player) {
        player.setTeam(resolverTeam(player.getTeam()));
        Player salvo = repository.save(player);
        URI location = linkTo(methodOn(PlayerController.class).buscarPorId(salvo.getId())).toUri();
        return ResponseEntity.created(location).body(salvo);
    }

    @Operation(summary = "Atualizar jogador", description = "Atualiza um jogador existente")
    @ApiResponse(responseCode = "200", description = "Jogador atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Jogador nao encontrado")
    @ApiResponse(responseCode = "400", description = "Dados invalidos")
    @PutMapping("/{id}")
    public ResponseEntity<Player> atualizar(@PathVariable Long id, @Valid @RequestBody Player player) {
        Player existente = buscarPlayer(id);

        existente.setNome(player.getNome());
        existente.setNick(player.getNick());
        existente.setRole(player.getRole());
        existente.setTeam(resolverTeam(player.getTeam()));

        return ResponseEntity.ok(repository.save(existente));
    }

    @Operation(summary = "Deletar jogador", description = "Remove um jogador do sistema")
    @ApiResponse(responseCode = "204", description = "Jogador removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Jogador nao encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        buscarPlayer(id);
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar jogador por nome", description = "Busca jogadores que contenham o nome informado")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    @GetMapping("/buscar")
    public PagedModel<EntityModel<Player>> buscarPorNome(@RequestParam String nome, Pageable pageable) {
        return PagedModelBuilder.from(repository.findByNomeContainingIgnoreCase(nome, pageable), this::montarModelo);
    }

    private EntityModel<Player> montarModelo(Player player) {
        Long id = player.getId();
        return EntityModel.of(player,
                linkTo(methodOn(PlayerController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(PlayerController.class).atualizar(id, null)).withRel("update"),
                linkTo(methodOn(PlayerController.class).deletar(id)).withRel("delete"),
                linkTo(methodOn(PlayerController.class).listar(Pageable.unpaged())).withRel("lista-jogadores")
        );
    }

    private Player buscarPlayer(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Jogador com ID " + id + " nao encontrado"));
    }

    private Team resolverTeam(Team team) {
        if (team == null || team.getId() == null) {
            return null;
        }
        return teamRepository.findById(team.getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Time com ID " + team.getId() + " nao encontrado"));
    }
}
