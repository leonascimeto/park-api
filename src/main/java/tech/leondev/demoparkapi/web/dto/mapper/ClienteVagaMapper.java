package tech.leondev.demoparkapi.web.dto.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import tech.leondev.demoparkapi.entity.ClienteVaga;
import tech.leondev.demoparkapi.web.dto.EstacionamentoDTO;
import tech.leondev.demoparkapi.web.dto.EstacionamentoResponseDTO;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClienteVagaMapper {

    public static ClienteVaga toClienteVaga(EstacionamentoDTO estacionamentoDTO){
        return new ModelMapper().map(estacionamentoDTO, ClienteVaga.class);
    }

    public static EstacionamentoResponseDTO toClienteVagaDTO(ClienteVaga clienteVaga){
        return new ModelMapper().map(clienteVaga, EstacionamentoResponseDTO.class);
    }
}
