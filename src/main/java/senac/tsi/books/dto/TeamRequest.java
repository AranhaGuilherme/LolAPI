package senac.tsi.books.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TeamRequest {

    @Schema(description = "Ignorado no POST. Envie 0 ou omita para o banco gerar automaticamente.", example = "0")
    private Long id;

    @NotBlank(message = "Nome do time nao pode estar vazio")
    @Size(min = 2, max = 100, message = "Nome do time deve ter entre 2 e 100 caracteres")
    private String nome;

    @NotBlank(message = "Regiao do time nao pode estar vazia")
    @Size(min = 2, max = 100, message = "Regiao do time deve ter entre 2 e 100 caracteres")
    private String regiao;

    @Min(value = 0, message = "coachId deve ser 0 ou maior")
    @Schema(description = "ID do coach relacionado. Use 0 ou omita para deixar sem coach.", example = "0")
    private Long coachId;

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getRegiao() { return regiao; }
    public Long getCoachId() { return coachId; }

    public void setId(Long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setRegiao(String regiao) { this.regiao = regiao; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }
}
