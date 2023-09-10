package tech.leondev.demoparkapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.leondev.demoparkapi.entity.ClienteVaga;
import tech.leondev.demoparkapi.repository.ClienteVagaRepository;

@Service
@RequiredArgsConstructor
public class ClienteVagaService {
    private final ClienteVagaRepository clienteVagaRepository;

    @Transactional
    public ClienteVaga salvar(ClienteVaga clienteVaga){
        return clienteVagaRepository.save(clienteVaga);
    }
}
