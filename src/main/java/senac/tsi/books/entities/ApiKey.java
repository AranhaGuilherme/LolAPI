package senac.tsi.books.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do usuario nao pode estar vazio")
    @Size(min = 2, max = 100, message = "Nome do usuario deve ter entre 2 e 100 caracteres")
    private String username;

    @Column(nullable = false, unique = true, length = 80)
    private String keyValue;

    @NotNull(message = "Role da chave e obrigatoria")
    @Enumerated(EnumType.STRING)
    private ApiKeyRole role;

    private boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ApiKey() {}

    public ApiKey(String username, String keyValue, ApiKeyRole role) {
        this.username = username;
        this.keyValue = keyValue;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getKeyValue() { return keyValue; }
    public ApiKeyRole getRole() { return role; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setKeyValue(String keyValue) { this.keyValue = keyValue; }
    public void setRole(ApiKeyRole role) { this.role = role; }
    public void setActive(boolean active) { this.active = active; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
