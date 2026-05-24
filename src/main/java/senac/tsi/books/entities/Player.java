package senac.tsi.books.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do jogador nao pode estar vazio")
    @Size(min = 2, max = 100, message = "Nome do jogador deve ter entre 2 e 100 caracteres")
    private String nome;

    @NotBlank(message = "Nick do jogador nao pode estar vazio")
    @Size(min = 2, max = 50, message = "Nick do jogador deve ter entre 2 e 50 caracteres")
    private String nick;

    @NotNull(message = "Role do jogador e obrigatoria (TOP, JUNGLE, MID, ADC, SUPPORT)")
    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "team_id")
    @JsonIgnoreProperties({"players"})
    private Team team;

    @ManyToMany
    @JoinTable(
            name = "player_champion",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "champion_id")
    )
    @JsonIgnoreProperties({"players"})
    private List<Champion> champions;

    public Player() {}

    public Player(String nome, String nick, Role role) {
        this.nome = nome;
        this.nick = nick;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getNick() { return nick; }
    public Role getRole() { return role; }
    public Team getTeam() { return team; }
    public List<Champion> getChampions() { return champions; }

    public void setId(Long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setNick(String nick) { this.nick = nick; }
    public void setRole(Role role) { this.role = role; }
    public void setTeam(Team team) { this.team = team; }
    public void setChampions(List<Champion> champions) { this.champions = champions; }
}
