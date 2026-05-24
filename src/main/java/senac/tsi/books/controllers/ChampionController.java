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
import senac.tsi.books.entities.Champion;
import senac.tsi.books.exceptions.RecursoNaoEncontradoException;
import senac.tsi.books.repositories.ChampionRepository;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping({"/champions", "/api/v1/champions"})
@Tag(name = "Campeoes")
@DefaultApiResponses
public class ChampionController {

    @Autowired
    private ChampionRepository repository;

    @Operation(summary = "Listar campeoes", description = "Retorna uma lista paginada de campeoes com HATEOAS")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public PagedModel<EntityModel<Champion>> listar(Pageable pageable) {
        return PagedModelBuilder.from(repository.findAll(pageable), this::montarModelo);
    }

    @Operation(summary = "Buscar campeao por ID", description = "Retorna um campeao pelo seu ID")
    @ApiResponse(responseCode = "200", description = "Campeao encontrado")
    @ApiResponse(responseCode = "404", description = "Campeao nao encontrado")
    @GetMapping("/{id}")
    public EntityModel<Champion> buscarPorId(@PathVariable Long id) {
        return montarModelo(buscarChampion(id));
    }

    @Operation(summary = "Criar campeao", description = "Cria um novo campeao")
    @ApiResponse(responseCode = "201", description = "Campeao criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados invalidos")
    @PostMapping
    public ResponseEntity<Champion> criar(@Valid @RequestBody Champion champion) {
        Champion salvo = repository.save(champion);
        URI location = linkTo(methodOn(ChampionController.class).buscarPorId(salvo.getId())).toUri();
        return ResponseEntity.created(location).body(salvo);
    }

    @Operation(summary = "Atualizar campeao", description = "Atualiza os dados de um campeao existente pelo ID")
    @ApiResponse(responseCode = "200", description = "Campeao atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Campeao nao encontrado")
    @ApiResponse(responseCode = "400", description = "Dados invalidos")
    @PutMapping("/{id}")
    public ResponseEntity<Champion> atualizar(@PathVariable Long id, @Valid @RequestBody Champion champion) {
        Champion existente = buscarChampion(id);
        existente.setNome(champion.getNome());
        existente.setRole(champion.getRole());
        return ResponseEntity.ok(repository.save(existente));
    }

    @Operation(summary = "Deletar campeao", description = "Remove um campeao do sistema pelo ID")
    @ApiResponse(responseCode = "204", description = "Campeao removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Campeao nao encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        buscarChampion(id);
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar campeoes por nome", description = "Busca campeoes que contenham o nome informado com paginacao")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    @GetMapping("/buscar")
    public PagedModel<EntityModel<Champion>> buscarPorNome(@RequestParam String nome, Pageable pageable) {
        return PagedModelBuilder.from(repository.findByNomeContainingIgnoreCase(nome, pageable), this::montarModelo);
    }

    private EntityModel<Champion> montarModelo(Champion champion) {
        Long id = champion.getId();
        return EntityModel.of(champion,
                linkTo(methodOn(ChampionController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(ChampionController.class).atualizar(id, null)).withRel("update"),
                linkTo(methodOn(ChampionController.class).deletar(id)).withRel("delete"),
                linkTo(methodOn(ChampionController.class).listar(Pageable.unpaged())).withRel("lista-campeoes")
        );
    }

    private Champion buscarChampion(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Campeao com ID " + id + " nao encontrado"));
    }
}
