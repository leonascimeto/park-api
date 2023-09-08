package tech.leondev.demoparkapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.leondev.demoparkapi.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
