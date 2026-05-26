package senac.tsi.books.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import senac.tsi.books.entities.Role;

public class ChampionRequest {

    @Schema(description = "Ignorado no POST. Envie 0 ou omita para o banco gerar automaticamente.", example = "0")
    private Long id;

    @NotBlank(message = "Nome do campeao nao pode estar vazio")
    @Size(min = 2, max = 100, message = "Nome do campeao deve ter entre 2 e 100 caracteres")
    private String nome;

    @NotNull(message = "Role do campeao e obrigatoria (TOP, JUNGLE, MID, ADC, SUPPORT)")
    private Role role;

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public Role getRole() { return role; }

    public void setId(Long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setRole(Role role) { this.role = role; }
}
