package senac.tsi.books.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import senac.tsi.books.entities.MatchGame;

public interface MatchGameRepository extends JpaRepository<MatchGame, Long> {

    Page<MatchGame> findByDuracaoContaining(String duracao, Pageable pageable);
}
