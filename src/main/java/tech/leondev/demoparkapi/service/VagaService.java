package tech.leondev.demoparkapi.service;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.leondev.demoparkapi.entity.Vaga;
import tech.leondev.demoparkapi.exception.CodigoUniqueViolationException;
import tech.leondev.demoparkapi.exception.EntityNotFoundException;
import tech.leondev.demoparkapi.repository.VagaRepository;

@RequiredArgsConstructor
@Service
public class VagaService {
    private final VagaRepository vagaRepository;

    @Transactional
    public Vaga salvar(Vaga vaga){
        try{
            return vagaRepository.save(vaga);
        } catch (DataIntegrityViolationException ex){
            throw new CodigoUniqueViolationException(String.format("Com com código '%s' já cadastrada", vaga.getCodigo()));
        }
    }

    @Transactional(readOnly = true)
    public Vaga buscarPorCodigo(String codigo){
        return vagaRepository.findByCodigo(codigo)
                .orElseThrow(
                        () -> new EntityNotFoundException(String.format("Vaga com código '%s' não foi encontrada", codigo))
                );
    }
}
