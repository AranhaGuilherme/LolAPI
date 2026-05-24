package senac.tsi.books.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import senac.tsi.books.entities.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    Page<Player> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
