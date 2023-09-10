package tech.leondev.demoparkapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.leondev.demoparkapi.entity.ClienteVaga;

public interface ClienteVagaRepository extends JpaRepository<ClienteVaga, Long> {
}
