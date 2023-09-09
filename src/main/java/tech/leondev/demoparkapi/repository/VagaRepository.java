package tech.leondev.demoparkapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.leondev.demoparkapi.entity.Vaga;

import java.util.Optional;

public interface VagaRepository extends JpaRepository<Vaga, Long> {
    Optional<Vaga> findByCodigo(String codigo);
}
