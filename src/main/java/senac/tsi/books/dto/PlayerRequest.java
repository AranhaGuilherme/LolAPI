package senac.tsi.books.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import senac.tsi.books.entities.Role;

import java.util.List;

public class PlayerRequest {

    @Schema(description = "Ignorado no POST. Envie 0 ou omita para o banco gerar automaticamente.", example = "0")
    private Long id;

    @NotBlank(message = "Nome do jogador nao pode estar vazio")
    @Size(min = 2, max = 100, message = "Nome do jogador deve ter entre 2 e 100 caracteres")
    private String nome;

    @NotBlank(message = "Nick do jogador nao pode estar vazio")
    @Size(min = 2, max = 50, message = "Nick do jogador deve ter entre 2 e 50 caracteres")
    private String nick;

    @NotNull(message = "Role do jogador e obrigatoria (TOP, JUNGLE, MID, ADC, SUPPORT)")
    private Role role;

    @Min(value = 0, message = "teamId deve ser 0 ou maior")
    @Schema(description = "ID do time relacionado. Use 0 ou omita para deixar sem time.", example = "0")
    private Long teamId;

    @Schema(description = "IDs de campeoes relacionados. IDs 0 sao ignorados.", example = "[1, 2]")
    private List<Long> championIds;

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getNick() { return nick; }
    public Role getRole() { return role; }
    public Long getTeamId() { return teamId; }
    public List<Long> getChampionIds() { return championIds; }

    public void setId(Long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setNick(String nick) { this.nick = nick; }
    public void setRole(Role role) { this.role = role; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
    public void setChampionIds(List<Long> championIds) { this.championIds = championIds; }
}
