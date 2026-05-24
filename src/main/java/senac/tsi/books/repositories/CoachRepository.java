package senac.tsi.books.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import senac.tsi.books.entities.Coach;

public interface CoachRepository extends JpaRepository<Coach, Long> {

    Page<Coach> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
