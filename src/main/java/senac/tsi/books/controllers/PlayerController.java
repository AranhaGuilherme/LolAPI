package senac.tsi.books.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import senac.tsi.books.entities.Player;
import senac.tsi.books.entities.Team;
import senac.tsi.books.exceptions.RecursoNaoEncontradoException;
import senac.tsi.books.repositories.PlayerRepository;
import senac.tsi.books.repositories.TeamRepository;

import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/players")
public class PlayerController {

    @Autowired
    private PlayerRepository repository;

    @Autowired
    private TeamRepository teamRepository;

    @Operation(summary = "Listar jogadores", description = "Retorna todos os jogadores com paginação")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public Page<Player> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Operation(summary = "Buscar jogador por ID", description = "Retorna um jogador específico pelo ID")
    @ApiResponse(responseCode = "200", description = "Jogador encontrado")
    @ApiResponse(responseCode = "404", description = "Jogador não encontrado")
    @GetMapping("/{id}")
    public EntityModel<Player> buscarPorId(@PathVariable Long id) {
        Player player = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Jogador com ID " + id + " não encontrado"));

        return EntityModel.of(player,
                linkTo(methodOn(PlayerController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(PlayerController.class).listar(PageRequest.of(0, 10))).withRel("lista-jogadores")
        );
    }

    @Operation(summary = "Criar jogador", description = "Cria um novo jogador")
    @ApiResponse(responseCode = "201", description = "Jogador criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @PostMapping
    public ResponseEntity<Player> criar(@Valid @RequestBody Player player) {
        player.setTeam(resolverTeam(player.getTeam()));
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(player));
    }

    @Operation(summary = "Atualizar jogador", description = "Atualiza um jogador existente")
    @ApiResponse(responseCode = "200", description = "Jogador atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Jogador não encontrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @PutMapping("/{id}")
    public ResponseEntity<Player> atualizar(@PathVariable Long id, @Valid @RequestBody Player player) {
        Player existente = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Jogador com ID " + id + " não encontrado"));

        existente.setNome(player.getNome());
        existente.setNick(player.getNick());
        existente.setRole(player.getRole());
        existente.setTeam(resolverTeam(player.getTeam()));

        return ResponseEntity.ok(repository.save(existente));
    }

    @Operation(summary = "Deletar jogador", description = "Remove um jogador do sistema")
    @ApiResponse(responseCode = "204", description = "Jogador removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Jogador não encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Jogador com ID " + id + " não encontrado"));
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar jogador por nome", description = "Busca jogadores que contenham o nome informado")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    @GetMapping("/buscar")
    public List<Player> buscarPorNome(@RequestParam String nome) {
        return repository.findByNomeContaining(nome);
    }

    private Team resolverTeam(Team team) {
        if (team == null || team.getId() == null) {
            return null;
        }
        return teamRepository.findById(team.getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Time com ID " + team.getId() + " não encontrado"));
    }
}
