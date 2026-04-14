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

import senac.tsi.books.entities.Champion;
import senac.tsi.books.entities.MatchGame;
import senac.tsi.books.entities.Player;
import senac.tsi.books.entities.Team;
import senac.tsi.books.exceptions.RecursoNaoEncontradoException;
import senac.tsi.books.repositories.ChampionRepository;
import senac.tsi.books.repositories.MatchGameRepository;
import senac.tsi.books.repositories.PlayerRepository;
import senac.tsi.books.repositories.TeamRepository;

import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/matchgames")
public class MatchGameController {

    @Autowired
    private MatchGameRepository repository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ChampionRepository championRepository;

    @Operation(summary = "Listar partidas", description = "Retorna todas as partidas com paginação")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public Page<MatchGame> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Operation(summary = "Buscar partida por ID", description = "Retorna uma partida específica pelo ID")
    @ApiResponse(responseCode = "200", description = "Partida encontrada")
    @ApiResponse(responseCode = "404", description = "Partida não encontrada")
    @GetMapping("/{id}")
    public EntityModel<MatchGame> buscarPorId(@PathVariable Long id) {
        MatchGame match = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Partida com ID " + id + " não encontrada"));

        return EntityModel.of(match,
                linkTo(methodOn(MatchGameController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(MatchGameController.class).listar(PageRequest.of(0, 10))).withRel("lista-partidas")
        );
    }

    @Operation(summary = "Criar partida", description = "Cria uma nova partida no sistema")
    @ApiResponse(responseCode = "201", description = "Partida criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @PostMapping
    public ResponseEntity<MatchGame> criar(@Valid @RequestBody MatchGame match) {
        resolverRelacoes(match);
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(match));
    }

    @Operation(summary = "Atualizar partida", description = "Atualiza os dados de uma partida existente")
    @ApiResponse(responseCode = "200", description = "Partida atualizada com sucesso")
    @ApiResponse(responseCode = "404", description = "Partida não encontrada")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @PutMapping("/{id}")
    public ResponseEntity<MatchGame> atualizar(@PathVariable Long id, @Valid @RequestBody MatchGame match) {
        MatchGame existente = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Partida com ID " + id + " não encontrada"));

        existente.setDuracao(match.getDuracao());
        resolverRelacoes(match);
        existente.setTimeA(match.getTimeA());
        existente.setTimeB(match.getTimeB());
        existente.setVencedor(match.getVencedor());
        existente.setPlayers(match.getPlayers());
        existente.setChampions(match.getChampions());

        return ResponseEntity.ok(repository.save(existente));
    }

    @Operation(summary = "Deletar partida", description = "Remove uma partida do sistema")
    @ApiResponse(responseCode = "204", description = "Partida removida com sucesso")
    @ApiResponse(responseCode = "404", description = "Partida não encontrada")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Partida com ID " + id + " não encontrada"));
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar partida por duração", description = "Busca partidas que contenham a duração informada")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    @GetMapping("/buscar")
    public List<MatchGame> buscarPorDuracao(@RequestParam String duracao) {
        return repository.findByDuracaoContaining(duracao);
    }

    private Team resolverTime(Team time) {
        if (time == null || time.getId() == null) {
            return null;
        }
        return teamRepository.findById(time.getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Time com ID " + time.getId() + " não encontrado"));
    }

    private void resolverRelacoes(MatchGame match) {
        match.setTimeA(resolverTime(match.getTimeA()));
        match.setTimeB(resolverTime(match.getTimeB()));
        match.setVencedor(resolverTime(match.getVencedor()));

        if (match.getPlayers() != null) {
            List<Player> players = match.getPlayers().stream()
                    .filter(p -> p != null && p.getId() != null)
                    .map(p -> playerRepository.findById(p.getId())
                            .orElseThrow(() -> new RecursoNaoEncontradoException("Jogador com ID " + p.getId() + " não encontrado")))
                    .collect(Collectors.toList());
            match.setPlayers(players);
        }

        if (match.getChampions() != null) {
            List<Champion> champions = match.getChampions().stream()
                    .filter(c -> c != null && c.getId() != null)
                    .map(c -> championRepository.findById(c.getId())
                            .orElseThrow(() -> new RecursoNaoEncontradoException("Campeão com ID " + c.getId() + " não encontrado")))
                    .collect(Collectors.toList());
            match.setChampions(champions);
        }
    }
}
