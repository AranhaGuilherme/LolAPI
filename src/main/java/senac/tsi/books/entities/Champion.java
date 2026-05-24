package senac.tsi.books.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
public class Champion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do campeao nao pode estar vazio")
    @Size(min = 2, max = 100, message = "Nome do campeao deve ter entre 2 e 100 caracteres")
    private String nome;

    @NotNull(message = "Role do campeao e obrigatoria (TOP, JUNGLE, MID, ADC, SUPPORT)")
    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToMany(mappedBy = "champions")
    @JsonIgnoreProperties({"champions", "team"})
    private List<Player> players;

    public Champion() {}

    public Champion(String nome, Role role) {
        this.nome = nome;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public Role getRole() { return role; }
    public List<Player> getPlayers() { return players; }

    public void setId(Long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setRole(Role role) { this.role = role; }
    public void setPlayers(List<Player> players) { this.players = players; }
}
