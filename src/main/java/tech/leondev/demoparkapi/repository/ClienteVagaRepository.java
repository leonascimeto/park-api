package tech.leondev.demoparkapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.leondev.demoparkapi.entity.ClienteVaga;

import java.util.Optional;

public interface ClienteVagaRepository extends JpaRepository<ClienteVaga, Long> {
    Optional<ClienteVaga> findByReciboAndDataSaidaIsNull(String recibo);

    long countByClienteCpfAndDataSaidaIsNotNull(String cpf);
}
