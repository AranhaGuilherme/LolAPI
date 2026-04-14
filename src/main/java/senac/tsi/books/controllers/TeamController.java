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

import senac.tsi.books.entities.Coach;
import senac.tsi.books.entities.Team;
import senac.tsi.books.exceptions.RecursoNaoEncontradoException;
import senac.tsi.books.repositories.CoachRepository;
import senac.tsi.books.repositories.TeamRepository;

import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/teams")
public class TeamController {

    @Autowired
    private TeamRepository repository;

    @Autowired
    private CoachRepository coachRepository;

    @Operation(summary = "Listar times", description = "Retorna todos os times cadastrados com paginação")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public Page<Team> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Operation(summary = "Buscar time por ID", description = "Retorna um time específico pelo ID")
    @ApiResponse(responseCode = "200", description = "Time encontrado")
    @ApiResponse(responseCode = "404", description = "Time não encontrado")
    @GetMapping("/{id}")
    public EntityModel<Team> buscarPorId(@PathVariable Long id) {
        Team team = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Time com ID " + id + " não encontrado"));

        return EntityModel.of(team,
                linkTo(methodOn(TeamController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(TeamController.class).listar(PageRequest.of(0, 10))).withRel("lista-times")
        );
    }

    @Operation(summary = "Criar time", description = "Cria um novo time no sistema")
    @ApiResponse(responseCode = "201", description = "Time criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @PostMapping
    public ResponseEntity<Team> criar(@Valid @RequestBody Team team) {
        team.setCoach(resolverCoach(team.getCoach()));
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(team));
    }

    @Operation(summary = "Atualizar time", description = "Atualiza os dados de um time existente")
    @ApiResponse(responseCode = "200", description = "Time atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Time não encontrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @PutMapping("/{id}")
    public ResponseEntity<Team> atualizar(@PathVariable Long id, @Valid @RequestBody Team team) {
        Team existente = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Time com ID " + id + " não encontrado"));

        existente.setNome(team.getNome());
        existente.setRegiao(team.getRegiao());
        existente.setCoach(resolverCoach(team.getCoach()));

        return ResponseEntity.ok(repository.save(existente));
    }

    @Operation(summary = "Deletar time", description = "Remove um time do sistema")
    @ApiResponse(responseCode = "204", description = "Time removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Time não encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Time com ID " + id + " não encontrado"));
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar time por nome", description = "Busca times que contenham o nome informado")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    @GetMapping("/buscar")
    public List<Team> buscarPorNome(@RequestParam String nome) {
        return repository.findByNomeContaining(nome);
    }

    private Coach resolverCoach(Coach coach) {
        if (coach == null || coach.getId() == null) {
            return null;
        }
        return coachRepository.findById(coach.getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Coach com ID " + coach.getId() + " não encontrado"));
    }
}
