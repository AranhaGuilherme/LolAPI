package senac.tsi.books.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.tsi.books.entities.MatchGame;

import java.util.List;

public interface MatchGameRepository extends JpaRepository<MatchGame, Long> {

    // ✅ usado no endpoint /matchgames/buscar
    List<MatchGame> findByDuracaoContaining(String duracao);
}