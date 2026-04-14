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
import senac.tsi.books.exceptions.RecursoNaoEncontradoException;
import senac.tsi.books.repositories.CoachRepository;

import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/coaches")
public class CoachController {

    @Autowired
    private CoachRepository repository;

    @Operation(summary = "Listar coaches", description = "Retorna todos os coaches cadastrados com paginação")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public Page<Coach> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Operation(summary = "Buscar coach por ID", description = "Retorna um coach específico pelo ID")
    @ApiResponse(responseCode = "200", description = "Coach encontrado")
    @ApiResponse(responseCode = "404", description = "Coach não encontrado")
    @GetMapping("/{id}")
    public EntityModel<Coach> buscarPorId(@PathVariable Long id) {
        Coach coach = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Coach com ID " + id + " não encontrado"));

        return EntityModel.of(coach,
                linkTo(methodOn(CoachController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(CoachController.class).listar(PageRequest.of(0, 10))).withRel("lista-coaches")
        );
    }

    @Operation(summary = "Criar coach", description = "Cria um novo coach no sistema")
    @ApiResponse(responseCode = "201", description = "Coach criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @PostMapping
    public ResponseEntity<Coach> criar(@Valid @RequestBody Coach coach) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(coach));
    }

    @Operation(summary = "Atualizar coach", description = "Atualiza os dados de um coach existente")
    @ApiResponse(responseCode = "200", description = "Coach atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Coach não encontrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @PutMapping("/{id}")
    public ResponseEntity<Coach> atualizar(@PathVariable Long id, @Valid @RequestBody Coach coach) {
        Coach existente = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Coach com ID " + id + " não encontrado"));

        existente.setNome(coach.getNome());
        existente.setExperiencia(coach.getExperiencia());

        return ResponseEntity.ok(repository.save(existente));
    }

    @Operation(summary = "Deletar coach", description = "Remove um coach do sistema")
    @ApiResponse(responseCode = "204", description = "Coach removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Coach não encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Coach com ID " + id + " não encontrado"));
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar coach por nome", description = "Retorna coaches que contenham o nome informado")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    @GetMapping("/buscar")
    public List<Coach> buscarPorNome(@RequestParam String nome) {
        return repository.findByNomeContaining(nome);
    }
}
