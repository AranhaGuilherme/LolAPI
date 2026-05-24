package senac.tsi.books.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Coach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do coach nao pode estar vazio")
    @Size(min = 2, max = 100, message = "Nome do coach deve ter entre 2 e 100 caracteres")
    private String nome;

    @Min(value = 0, message = "Experiencia nao pode ser negativa")
    @Max(value = 60, message = "Experiencia nao pode ser maior que 60 anos")
    private int experiencia;

    @OneToOne(mappedBy = "coach")
    @JsonIgnoreProperties({"coach", "players"})
    private Team team;

    public Coach() {}

    public Coach(String nome, int experiencia) {
        this.nome = nome;
        this.experiencia = experiencia;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public int getExperiencia() { return experiencia; }
    public Team getTeam() { return team; }

    public void setId(Long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setExperiencia(int experiencia) { this.experiencia = experiencia; }
    public void setTeam(Team team) { this.team = team; }
}
