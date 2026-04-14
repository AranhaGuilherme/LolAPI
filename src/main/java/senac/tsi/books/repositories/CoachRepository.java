package senac.tsi.books.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import senac.tsi.books.entities.Coach;

import java.util.List;

public interface CoachRepository extends JpaRepository<Coach, Long> {

    // ✅ usado no endpoint /coaches/buscar
    List<Coach> findByNomeContaining(String nome);
}