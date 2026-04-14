package senac.tsi.books.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.tsi.books.entities.Team;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    // ✅ usado no endpoint /teams/buscar
    List<Team> findByNomeContaining(String nome);
}