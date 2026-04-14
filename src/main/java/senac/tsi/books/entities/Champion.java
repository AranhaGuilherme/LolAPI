package senac.tsi.books.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
public class Champion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do campeão não pode estar vazio")
    @Size(min = 2, max = 100, message = "Nome do campeão deve ter entre 2 e 100 caracteres")
    private String nome;

    @NotNull(message = "Role do campeão é obrigatório (TOP, JUNGLE, MID, ADC, SUPPORT)")
    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToMany(mappedBy = "champions")
    @JsonIgnore
    private List<Player> players;

    public Champion() {}

    public Champion(String nome, Role role) {
        this.nome = nome;
        this.role = role;
    }

    // GETTERS
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public Role getRole() { return role; }
    @JsonIgnore
    public List<Player> getPlayers() { return players; }

    // SETTERS
    public void setId(Long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setRole(Role role) { this.role = role; }
    public void setPlayers(List<Player> players) { this.players = players; }
}
