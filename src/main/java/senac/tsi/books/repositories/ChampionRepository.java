package senac.tsi.books.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.tsi.books.entities.Champion;

import java.util.List;

public interface ChampionRepository extends JpaRepository<Champion, Long> {

    // ✅ usado no endpoint /champions/buscar
    List<Champion> findByNomeContaining(String nome);
}