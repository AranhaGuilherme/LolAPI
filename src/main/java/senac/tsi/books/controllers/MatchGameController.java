package senac.tsi.books.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import senac.tsi.books.config.DefaultApiResponses;
import senac.tsi.books.config.PagedModelBuilder;
import senac.tsi.books.entities.Champion;
import senac.tsi.books.entities.MatchGame;
import senac.tsi.books.entities.Player;
import senac.tsi.books.entities.Team;
import senac.tsi.books.exceptions.RecursoNaoEncontradoException;
import senac.tsi.books.repositories.ChampionRepository;
import senac.tsi.books.repositories.MatchGameRepository;
import senac.tsi.books.repositories.PlayerRepository;
import senac.tsi.books.repositories.TeamRepository;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping({"/matchgames", "/api/v1/matchgames"})
@Tag(name = "Partidas")
@DefaultApiResponses
public class MatchGameController {

    @Autowired private MatchGameRepository repository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private PlayerRepository playerRepository;
    @Autowired private ChampionRepository championRepository;

    @Operation(summary = "Listar partidas", description = "Retorna partidas com paginacao e HATEOAS")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public PagedModel<EntityModel<MatchGame>> listar(Pageable pageable) {
        return PagedModelBuilder.from(repository.findAll(pageable), this::montarModelo);
    }

    @Operation(summary = "Buscar partida por ID", description = "Retorna uma partida especifica pelo ID")
    @ApiResponse(responseCode = "200", description = "Partida encontrada")
    @ApiResponse(responseCode = "404", description = "Partida nao encontrada")
    @GetMapping("/{id}")
    public EntityModel<MatchGame> buscarPorId(@PathVariable Long id) {
        return montarModelo(buscarMatch(id));
    }

    @Operation(summary = "Criar partida", description = "Cria uma nova partida no sistema")
    @ApiResponse(responseCode = "201", description = "Partida criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados invalidos")
    @PostMapping
    public ResponseEntity<MatchGame> criar(@Valid @RequestBody MatchGame match) {
        resolverRelacoes(match);
        validarTimesDaPartida(match);
        MatchGame salvo = repository.save(match);
        URI location = linkTo(methodOn(MatchGameController.class).buscarPorId(salvo.getId())).toUri();
        return ResponseEntity.created(location).body(salvo);
    }

    @Operation(summary = "Atualizar partida", description = "Atualiza os dados de uma partida existente")
    @ApiResponse(responseCode = "200", description = "Partida atualizada com sucesso")
    @ApiResponse(responseCode = "404", description = "Partida nao encontrada")
    @ApiResponse(responseCode = "400", description = "Dados invalidos")
    @PutMapping("/{id}")
    public ResponseEntity<MatchGame> atualizar(@PathVariable Long id, @Valid @RequestBody MatchGame match) {
        MatchGame existente = buscarMatch(id);
        existente.setDuracao(match.getDuracao());
        resolverRelacoes(match);
        validarTimesDaPartida(match);
        existente.setTimeA(match.getTimeA());
        existente.setTimeB(match.getTimeB());
        existente.setVencedor(match.getVencedor());
        existente.setPlayers(match.getPlayers());
        existente.setChampions(match.getChampions());
        return ResponseEntity.ok(repository.save(existente));
    }

    @Operation(summary = "Deletar partida", description = "Remove uma partida do sistema")
    @ApiResponse(responseCode = "204", description = "Partida removida com sucesso")
    @ApiResponse(responseCode = "404", description = "Partida nao encontrada")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        buscarMatch(id);
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar partida por duracao", description = "Busca partidas que contenham a duracao informada com paginacao")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    @GetMapping("/buscar")
    public PagedModel<EntityModel<MatchGame>> buscarPorDuracao(@RequestParam String duracao, Pageable pageable) {
        return PagedModelBuilder.from(repository.findByDuracaoContaining(duracao, pageable), this::montarModelo);
    }

    private EntityModel<MatchGame> montarModelo(MatchGame match) {
        Long id = match.getId();
        return EntityModel.of(match,
                linkTo(methodOn(MatchGameController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(MatchGameController.class).atualizar(id, null)).withRel("update"),
                linkTo(methodOn(MatchGameController.class).deletar(id)).withRel("delete"),
                linkTo(methodOn(MatchGameController.class).listar(Pageable.unpaged())).withRel("lista-partidas")
        );
    }

    private MatchGame buscarMatch(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Partida com ID " + id + " nao encontrada"));
    }

    private Team resolverTime(Team time) {
        if (time == null || time.getId() == null) {
            return null;
        }
        return teamRepository.findById(time.getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Time com ID " + time.getId() + " nao encontrado"));
    }

    private void resolverRelacoes(MatchGame match) {
        match.setTimeA(resolverTime(match.getTimeA()));
        match.setTimeB(resolverTime(match.getTimeB()));
        match.setVencedor(resolverTime(match.getVencedor()));

        if (match.getPlayers() != null) {
            List<Player> players = match.getPlayers().stream()
                    .filter(p -> p != null && p.getId() != null)
                    .map(p -> playerRepository.findById(p.getId())
                            .orElseThrow(() -> new RecursoNaoEncontradoException("Jogador com ID " + p.getId() + " nao encontrado")))
                    .collect(Collectors.toList());
            match.setPlayers(players);
        }

        if (match.getChampions() != null) {
            List<Champion> champions = match.getChampions().stream()
                    .filter(c -> c != null && c.getId() != null)
                    .map(c -> championRepository.findById(c.getId())
                            .orElseThrow(() -> new RecursoNaoEncontradoException("Campeao com ID " + c.getId() + " nao encontrado")))
                    .collect(Collectors.toList());
            match.setChampions(champions);
        }
    }

    private void validarTimesDaPartida(MatchGame match) {
        if (match.getTimeA() != null && match.getTimeB() != null
                && match.getTimeA().getId().equals(match.getTimeB().getId())) {
            throw new IllegalArgumentException("Time A e Time B devem ser diferentes.");
        }

        if (match.getVencedor() != null && match.getTimeA() != null && match.getTimeB() != null
                && !match.getVencedor().getId().equals(match.getTimeA().getId())
                && !match.getVencedor().getId().equals(match.getTimeB().getId())) {
            throw new IllegalArgumentException("O vencedor deve ser Time A ou Time B.");
        }
    }
}
