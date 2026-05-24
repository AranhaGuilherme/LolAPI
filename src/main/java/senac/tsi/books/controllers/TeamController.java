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
import senac.tsi.books.entities.Coach;
import senac.tsi.books.entities.Team;
import senac.tsi.books.exceptions.RecursoNaoEncontradoException;
import senac.tsi.books.repositories.CoachRepository;
import senac.tsi.books.repositories.TeamRepository;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping({"/teams", "/api/v1/teams"})
@Tag(name = "Times")
@DefaultApiResponses
public class TeamController {

    @Autowired
    private TeamRepository repository;

    @Autowired
    private CoachRepository coachRepository;

    @Operation(summary = "Listar times", description = "Retorna times cadastrados com paginacao e HATEOAS")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public PagedModel<EntityModel<Team>> listar(Pageable pageable) {
        return PagedModelBuilder.from(repository.findAll(pageable), this::montarModelo);
    }

    @Operation(summary = "Buscar time por ID", description = "Retorna um time especifico pelo ID")
    @ApiResponse(responseCode = "200", description = "Time encontrado")
    @ApiResponse(responseCode = "404", description = "Time nao encontrado")
    @GetMapping("/{id}")
    public EntityModel<Team> buscarPorId(@PathVariable Long id) {
        return montarModelo(buscarTeam(id));
    }

    @Operation(summary = "Criar time", description = "Cria um novo time no sistema")
    @ApiResponse(responseCode = "201", description = "Time criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados invalidos")
    @PostMapping
    public ResponseEntity<Team> criar(@Valid @RequestBody Team team) {
        team.setCoach(resolverCoach(team.getCoach()));
        Team salvo = repository.save(team);
        URI location = linkTo(methodOn(TeamController.class).buscarPorId(salvo.getId())).toUri();
        return ResponseEntity.created(location).body(salvo);
    }

    @Operation(summary = "Atualizar time", description = "Atualiza os dados de um time existente")
    @ApiResponse(responseCode = "200", description = "Time atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Time nao encontrado")
    @ApiResponse(responseCode = "400", description = "Dados invalidos")
    @PutMapping("/{id}")
    public ResponseEntity<Team> atualizar(@PathVariable Long id, @Valid @RequestBody Team team) {
        Team existente = buscarTeam(id);
        existente.setNome(team.getNome());
        existente.setRegiao(team.getRegiao());
        existente.setCoach(resolverCoach(team.getCoach()));
        return ResponseEntity.ok(repository.save(existente));
    }

    @Operation(summary = "Deletar time", description = "Remove um time do sistema")
    @ApiResponse(responseCode = "204", description = "Time removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Time nao encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        buscarTeam(id);
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar time por nome", description = "Busca times que contenham o nome informado com paginacao")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    @GetMapping("/buscar")
    public PagedModel<EntityModel<Team>> buscarPorNome(@RequestParam String nome, Pageable pageable) {
        return PagedModelBuilder.from(repository.findByNomeContainingIgnoreCase(nome, pageable), this::montarModelo);
    }

    private EntityModel<Team> montarModelo(Team team) {
        Long id = team.getId();
        return EntityModel.of(team,
                linkTo(methodOn(TeamController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(TeamController.class).atualizar(id, null)).withRel("update"),
                linkTo(methodOn(TeamController.class).deletar(id)).withRel("delete"),
                linkTo(methodOn(TeamController.class).listar(Pageable.unpaged())).withRel("lista-times")
        );
    }

    private Team buscarTeam(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Time com ID " + id + " nao encontrado"));
    }

    private Coach resolverCoach(Coach coach) {
        if (coach == null || coach.getId() == null) {
            return null;
        }
        return coachRepository.findById(coach.getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Coach com ID " + coach.getId() + " nao encontrado"));
    }
}
