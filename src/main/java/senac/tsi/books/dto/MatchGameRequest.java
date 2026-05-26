package senac.tsi.books.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public class MatchGameRequest {

    @Schema(description = "Ignorado no POST. Envie 0 ou omita para o banco gerar automaticamente.", example = "0")
    private Long id;

    @NotBlank(message = "Duracao da partida nao pode estar vazia")
    @Pattern(regexp = "^([0-9]{2,3}):[0-5][0-9]$", message = "Duracao deve estar no formato MM:SS, como 35:00 ou 115:30")
    private String duracao;

    @NotNull(message = "timeAId e obrigatorio")
    @Min(value = 1, message = "timeAId deve apontar para um time existente")
    @Schema(description = "ID do Time A. Precisa ser um ID existente.", example = "1")
    private Long timeAId;

    @NotNull(message = "timeBId e obrigatorio")
    @Min(value = 1, message = "timeBId deve apontar para um time existente")
    @Schema(description = "ID do Time B. Precisa ser um ID existente.", example = "2")
    private Long timeBId;

    @Min(value = 0, message = "vencedorId deve ser 0 ou maior")
    @Schema(description = "ID do time vencedor. Use 0 ou omita para deixar sem vencedor.", example = "1")
    private Long vencedorId;

    @Schema(description = "IDs dos jogadores relacionados. IDs 0 sao ignorados.", example = "[1, 2]")
    private List<Long> playerIds;

    @Schema(description = "IDs dos campeoes relacionados. IDs 0 sao ignorados.", example = "[1, 2]")
    private List<Long> championIds;

    public Long getId() { return id; }
    public String getDuracao() { return duracao; }
    public Long getTimeAId() { return timeAId; }
    public Long getTimeBId() { return timeBId; }
    public Long getVencedorId() { return vencedorId; }
    public List<Long> getPlayerIds() { return playerIds; }
    public List<Long> getChampionIds() { return championIds; }

    public void setId(Long id) { this.id = id; }
    public void setDuracao(String duracao) { this.duracao = duracao; }
    public void setTimeAId(Long timeAId) { this.timeAId = timeAId; }
    public void setTimeBId(Long timeBId) { this.timeBId = timeBId; }
    public void setVencedorId(Long vencedorId) { this.vencedorId = vencedorId; }
    public void setPlayerIds(List<Long> playerIds) { this.playerIds = playerIds; }
    public void setChampionIds(List<Long> championIds) { this.championIds = championIds; }
}
