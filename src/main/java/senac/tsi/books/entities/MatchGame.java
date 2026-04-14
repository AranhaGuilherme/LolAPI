package senac.tsi.books.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
public class MatchGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Duração da partida não pode estar vazia")
    @Size(min = 1, max = 10, message = "Duração deve ter no máximo 10 caracteres (ex: 35:00)")
    private String duracao;

    @NotNull(message = "Time A é obrigatório")
    @ManyToOne
    @JoinColumn(name = "time_a_id")
    private Team timeA;

    @NotNull(message = "Time B é obrigatório")
    @ManyToOne
    @JoinColumn(name = "time_b_id")
    private Team timeB;

    @ManyToOne
    @JoinColumn(name = "vencedor_id")
    private Team vencedor;

    @ManyToMany
    @JoinTable(
            name = "match_player",
            joinColumns = @JoinColumn(name = "match_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    @JsonIgnore
    private List<Player> players;

    @ManyToMany
    @JoinTable(
            name = "match_champion",
            joinColumns = @JoinColumn(name = "match_id"),
            inverseJoinColumns = @JoinColumn(name = "champion_id")
    )
    @JsonIgnore
    private List<Champion> champions;

    public MatchGame() {}

    public MatchGame(String duracao) {
        this.duracao = duracao;
    }

    // GETTERS
    public Long getId() { return id; }
    public String getDuracao() { return duracao; }
    public Team getTimeA() { return timeA; }
    public Team getTimeB() { return timeB; }
    public Team getVencedor() { return vencedor; }
    @JsonIgnore
    public List<Player> getPlayers() { return players; }
    @JsonIgnore
    public List<Champion> getChampions() { return champions; }

    // SETTERS
    public void setId(Long id) { this.id = id; }
    public void setDuracao(String duracao) { this.duracao = duracao; }
    public void setTimeA(Team timeA) { this.timeA = timeA; }
    public void setTimeB(Team timeB) { this.timeB = timeB; }
    public void setVencedor(Team vencedor) { this.vencedor = vencedor; }
    public void setPlayers(List<Player> players) { this.players = players; }
    public void setChampions(List<Champion> champions) { this.champions = champions; }
}
