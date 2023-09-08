package tech.leondev.demoparkapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.leondev.demoparkapi.entity.Cliente;
import tech.leondev.demoparkapi.exception.CpfUniqueValidationException;
import tech.leondev.demoparkapi.repository.ClienteRepository;

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
}
