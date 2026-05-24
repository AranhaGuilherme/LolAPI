package senac.tsi.books.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import senac.tsi.books.entities.Champion;

public interface ChampionRepository extends JpaRepository<Champion, Long> {

    Page<Champion> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
