package senac.tsi.books.dto;

import senac.tsi.books.entities.Player;
import senac.tsi.books.entities.Role;

public class PlayerDTOV2 {

    private Long id;
    private String nome;
    private String nick;
    private Role role;
    private String teamNome;

    public PlayerDTOV2(Player player) {
        this.id = player.getId();
        this.nome = player.getNome();
        this.nick = player.getNick();
        this.role = player.getRole();
        this.teamNome = player.getTeam() == null ? null : player.getTeam().getNome();
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getNick() { return nick; }
    public Role getRole() { return role; }
    public String getTeamNome() { return teamNome; }
}
