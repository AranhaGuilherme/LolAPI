package senac.tsi.books.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Coach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do coach não pode estar vazio")
    @Size(min = 2, max = 100, message = "Nome do coach deve ter entre 2 e 100 caracteres")
    private String nome;

    @Min(value = 0, message = "Experiência não pode ser negativa")
    @Max(value = 60, message = "Experiência não pode ser maior que 60 anos")
    private int experiencia;

    @OneToOne(mappedBy = "coach")
    @JsonIgnore
    private Team team;

    public Coach() {}

    public Coach(String nome, int experiencia) {
        this.nome = nome;
        this.experiencia = experiencia;
    }

    // GETTERS
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public int getExperiencia() { return experiencia; }
    @JsonIgnore
    public Team getTeam() { return team; }

    // SETTERS
    public void setId(Long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setExperiencia(int experiencia) { this.experiencia = experiencia; }
    public void setTeam(Team team) { this.team = team; }
}
