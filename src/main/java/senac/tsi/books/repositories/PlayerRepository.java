package senac.tsi.books.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.tsi.books.entities.Player;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    // ✅ usado no endpoint /players/buscar
    List<Player> findByNomeContaining(String nome);
}