package senac.tsi.books.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import senac.tsi.books.entities.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Page<Team> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
