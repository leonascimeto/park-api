package tech.leondev.demoparkapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.leondev.demoparkapi.entity.Cliente;
import tech.leondev.demoparkapi.entity.ClienteVaga;
import tech.leondev.demoparkapi.entity.Vaga;
import tech.leondev.demoparkapi.util.EstacionamentoUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EstacionamentoService {
    private final ClienteVagaService clienteVagaService;
    private final ClienteService clienteService;
    private final VagaService vagaService;

    @Transactional
    public ClienteVaga checkin(ClienteVaga clienteVaga){
        Cliente cliente = clienteService.buscarPorCpf(clienteVaga.getCliente().getCpf());
        clienteVaga.setCliente(cliente);
        Vaga vaga = vagaService.buscarPorVagaLivre();
        vaga.setStatus(Vaga.StatusVaga.OCUPADA);
        clienteVaga.setVaga(vaga);
        clienteVaga.setDataEntrada(LocalDateTime.now());
        clienteVaga.setRecibo(EstacionamentoUtils.gerarRecibo());
        return clienteVagaService.salvar(clienteVaga);
    }
}
