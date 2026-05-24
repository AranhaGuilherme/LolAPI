package senac.tsi.books.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

@Entity
public class MatchGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Duracao da partida nao pode estar vazia")
    @Pattern(regexp = "^([0-9]{2,3}):[0-5][0-9]$", message = "Duracao deve estar no formato MM:SS, como 35:00 ou 115:30")
    private String duracao;

    @NotNull(message = "Time A e obrigatorio")
    @ManyToOne
    @JoinColumn(name = "time_a_id")
    @JsonIgnoreProperties({"players", "coach"})
    private Team timeA;

    @NotNull(message = "Time B e obrigatorio")
    @ManyToOne
    @JoinColumn(name = "time_b_id")
    @JsonIgnoreProperties({"players", "coach"})
    private Team timeB;

    @ManyToOne
    @JoinColumn(name = "vencedor_id")
    @JsonIgnoreProperties({"players", "coach"})
    private Team vencedor;

    @ManyToMany
    @JoinTable(
            name = "match_player",
            joinColumns = @JoinColumn(name = "match_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    @JsonIgnoreProperties({"team", "champions"})
    private List<Player> players;

    @ManyToMany
    @JoinTable(
            name = "match_champion",
            joinColumns = @JoinColumn(name = "match_id"),
            inverseJoinColumns = @JoinColumn(name = "champion_id")
    )
    @JsonIgnoreProperties({"players"})
    private List<Champion> champions;

    public MatchGame() {}

    public MatchGame(String duracao) {
        this.duracao = duracao;
    }

    public Long getId() { return id; }
    public String getDuracao() { return duracao; }
    public Team getTimeA() { return timeA; }
    public Team getTimeB() { return timeB; }
    public Team getVencedor() { return vencedor; }
    public List<Player> getPlayers() { return players; }
    public List<Champion> getChampions() { return champions; }

    public void setId(Long id) { this.id = id; }
    public void setDuracao(String duracao) { this.duracao = duracao; }
    public void setTimeA(Team timeA) { this.timeA = timeA; }
    public void setTimeB(Team timeB) { this.timeB = timeB; }
    public void setVencedor(Team vencedor) { this.vencedor = vencedor; }
    public void setPlayers(List<Player> players) { this.players = players; }
    public void setChampions(List<Champion> champions) { this.champions = champions; }
}
