package tech.leondev.demoparkapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.leondev.demoparkapi.entity.Cliente;
import tech.leondev.demoparkapi.exception.CpfUniqueValidationException;
import tech.leondev.demoparkapi.exception.EntityNotFoundException;
import tech.leondev.demoparkapi.repository.ClienteRepository;
import tech.leondev.demoparkapi.repository.projection.ClienteProjection;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;

    @Transactional
    public Cliente salvar(Cliente cliente){
        try{
            return clienteRepository.save(cliente);
        } catch (DataIntegrityViolationException ex){
            throw  new CpfUniqueValidationException(String.format("CPF '%s' não pode ser cadastrado, já existe no sistema", cliente.getCpf()));
        }
    }

    @Transactional
    public Cliente buscarPorId(Long idCliente) {
        return clienteRepository.findById(idCliente)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Cliente com id='%s' não encontrado", idCliente)));
    }

    @Transactional(readOnly = true)
    public Page<ClienteProjection> buscarTodosClientes(Pageable pageable) {
        return clienteRepository.findAllPageable(pageable);
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorUsuarioId(Long id) {
        return clienteRepository.findByUsuarioId(id);
    }
}
