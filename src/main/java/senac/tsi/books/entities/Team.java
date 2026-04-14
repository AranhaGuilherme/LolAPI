package senac.tsi.books.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do time não pode estar vazio")
    @Size(min = 2, max = 100, message = "Nome do time deve ter entre 2 e 100 caracteres")
    private String nome;

    @NotBlank(message = "Região do time não pode estar vazia")
    @Size(min = 2, max = 100, message = "Região do time deve ter entre 2 e 100 caracteres")
    private String regiao;

    @OneToMany(mappedBy = "team")
    @JsonIgnore
    private List<Player> players;

    @OneToOne
    @JoinColumn(name = "coach_id")
    private Coach coach;

    public Team() {}

    public Team(String nome, String regiao) {
        this.nome = nome;
        this.regiao = regiao;
    }

    // GETTERS
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getRegiao() { return regiao; }
    @JsonIgnore
    public List<Player> getPlayers() { return players; }
    public Coach getCoach() { return coach; }

    // SETTERS
    public void setId(Long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setRegiao(String regiao) { this.regiao = regiao; }
    public void setPlayers(List<Player> players) { this.players = players; }
    public void setCoach(Coach coach) { this.coach = coach; }
}
