package senac.tsi.books.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CoachRequest {

    @Schema(description = "Ignorado no POST. Envie 0 ou omita para o banco gerar automaticamente.", example = "0")
    private Long id;

    @NotBlank(message = "Nome do coach nao pode estar vazio")
    @Size(min = 2, max = 100, message = "Nome do coach deve ter entre 2 e 100 caracteres")
    private String nome;

    @Min(value = 0, message = "Experiencia nao pode ser negativa")
    @Max(value = 60, message = "Experiencia nao pode ser maior que 60 anos")
    private int experiencia;

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public int getExperiencia() { return experiencia; }

    public void setId(Long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setExperiencia(int experiencia) { this.experiencia = experiencia; }
}
