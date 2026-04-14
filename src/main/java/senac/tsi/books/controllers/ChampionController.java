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
import senac.tsi.books.exceptions.RecursoNaoEncontradoException;
import senac.tsi.books.repositories.ChampionRepository;

import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/champions")
public class ChampionController {

    @Autowired
    private ChampionRepository repository;

    @Operation(summary = "Listar campeões", description = "Retorna uma lista paginada de todos os campeões")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public Page<Champion> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Operation(summary = "Buscar campeão por ID", description = "Retorna um campeão pelo seu ID")
    @ApiResponse(responseCode = "200", description = "Campeão encontrado")
    @ApiResponse(responseCode = "404", description = "Campeão não encontrado")
    @GetMapping("/{id}")
    public EntityModel<Champion> buscarPorId(@PathVariable Long id) {
        Champion champion = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Campeão com ID " + id + " não encontrado"));

        return EntityModel.of(champion,
                linkTo(methodOn(ChampionController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(ChampionController.class).listar(PageRequest.of(0, 10))).withRel("lista-campeoes")
        );
    }

    @Operation(summary = "Criar campeão", description = "Cria um novo campeão")
    @ApiResponse(responseCode = "201", description = "Campeão criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @PostMapping
    public ResponseEntity<Champion> criar(@Valid @RequestBody Champion champion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(champion));
    }

    @Operation(summary = "Atualizar campeão", description = "Atualiza os dados de um campeão existente pelo ID")
    @ApiResponse(responseCode = "200", description = "Campeão atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Campeão não encontrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @PutMapping("/{id}")
    public ResponseEntity<Champion> atualizar(@PathVariable Long id, @Valid @RequestBody Champion champion) {
        Champion existente = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Campeão com ID " + id + " não encontrado"));

        existente.setNome(champion.getNome());
        existente.setRole(champion.getRole());

        return ResponseEntity.ok(repository.save(existente));
    }

    @Operation(summary = "Deletar campeão", description = "Remove um campeão do sistema pelo ID")
    @ApiResponse(responseCode = "204", description = "Campeão removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Campeão não encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Campeão com ID " + id + " não encontrado"));
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar campeões por nome", description = "Busca campeões que contenham o nome informado")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    @GetMapping("/buscar")
    public List<Champion> buscarPorNome(@RequestParam String nome) {
        return repository.findByNomeContaining(nome);
    }
}
