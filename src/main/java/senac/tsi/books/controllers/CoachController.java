package senac.tsi.books.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import senac.tsi.books.config.PagedModelBuilder;
import senac.tsi.books.entities.Coach;
import senac.tsi.books.exceptions.RecursoNaoEncontradoException;
import senac.tsi.books.repositories.CoachRepository;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/coaches")
public class CoachController {

    @Autowired
    private CoachRepository repository;

    @Operation(summary = "Listar coaches", description = "Retorna coaches cadastrados com paginacao e HATEOAS")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public PagedModel<EntityModel<Coach>> listar(Pageable pageable) {
        return PagedModelBuilder.from(repository.findAll(pageable), this::montarModelo);
    }

    @Operation(summary = "Buscar coach por ID", description = "Retorna um coach especifico pelo ID")
    @ApiResponse(responseCode = "200", description = "Coach encontrado")
    @ApiResponse(responseCode = "404", description = "Coach nao encontrado")
    @GetMapping("/{id}")
    public EntityModel<Coach> buscarPorId(@PathVariable Long id) {
        return montarModelo(buscarCoach(id));
    }

    @Operation(summary = "Criar coach", description = "Cria um novo coach no sistema")
    @ApiResponse(responseCode = "201", description = "Coach criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados invalidos")
    @PostMapping
    public ResponseEntity<Coach> criar(@Valid @RequestBody Coach coach) {
        Coach salvo = repository.save(coach);
        URI location = linkTo(methodOn(CoachController.class).buscarPorId(salvo.getId())).toUri();
        return ResponseEntity.created(location).body(salvo);
    }

    @Operation(summary = "Atualizar coach", description = "Atualiza os dados de um coach existente")
    @ApiResponse(responseCode = "200", description = "Coach atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Coach nao encontrado")
    @ApiResponse(responseCode = "400", description = "Dados invalidos")
    @PutMapping("/{id}")
    public ResponseEntity<Coach> atualizar(@PathVariable Long id, @Valid @RequestBody Coach coach) {
        Coach existente = buscarCoach(id);
        existente.setNome(coach.getNome());
        existente.setExperiencia(coach.getExperiencia());
        return ResponseEntity.ok(repository.save(existente));
    }

    @Operation(summary = "Deletar coach", description = "Remove um coach do sistema")
    @ApiResponse(responseCode = "204", description = "Coach removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Coach nao encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        buscarCoach(id);
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar coach por nome", description = "Retorna coaches que contenham o nome informado com paginacao")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    @GetMapping("/buscar")
    public PagedModel<EntityModel<Coach>> buscarPorNome(@RequestParam String nome, Pageable pageable) {
        return PagedModelBuilder.from(repository.findByNomeContainingIgnoreCase(nome, pageable), this::montarModelo);
    }

    private EntityModel<Coach> montarModelo(Coach coach) {
        Long id = coach.getId();
        return EntityModel.of(coach,
                linkTo(methodOn(CoachController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(CoachController.class).atualizar(id, null)).withRel("update"),
                linkTo(methodOn(CoachController.class).deletar(id)).withRel("delete"),
                linkTo(methodOn(CoachController.class).listar(Pageable.unpaged())).withRel("lista-coaches")
        );
    }

    private Coach buscarCoach(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Coach com ID " + id + " nao encontrado"));
    }
}
