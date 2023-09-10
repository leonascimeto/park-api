package tech.leondev.demoparkapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.leondev.demoparkapi.entity.ClienteVaga;
import tech.leondev.demoparkapi.exception.EntityNotFoundException;
import tech.leondev.demoparkapi.repository.ClienteVagaRepository;

@Service
@RequiredArgsConstructor
public class ClienteVagaService {
    private final ClienteVagaRepository clienteVagaRepository;

    @Transactional
    public ClienteVaga salvar(ClienteVaga clienteVaga){
        return clienteVagaRepository.save(clienteVaga);
    }

    @Transactional(readOnly = true)
    public ClienteVaga getByRecibo(String recibo) {
        return clienteVagaRepository.findByReciboAndDataSaidaIsNull(recibo)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Recibo '%s' não encontrado ou checkout já realizado", recibo)));
    }

    @Transactional(readOnly = true)
    public long getTotalVezesEstacionamentoCompleto(String cpf) {
        return clienteVagaRepository.countByClienteCpfAndDataSaidaIsNotNull(cpf);
    }
}
