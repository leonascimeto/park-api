package tech.leondev.demoparkapi.web.dto.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import tech.leondev.demoparkapi.entity.ClienteVaga;
import tech.leondev.demoparkapi.web.dto.EstacionamentoCreateDTO;
import tech.leondev.demoparkapi.web.dto.EstacionamentoResponseDTO;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClienteVagaMapper {

    public static ClienteVaga toClienteVaga(EstacionamentoCreateDTO estacionamentoCreateDTO){
        return new ModelMapper().map(estacionamentoCreateDTO, ClienteVaga.class);
    }

    public static EstacionamentoResponseDTO toClienteVagaDTO(ClienteVaga clienteVaga){
        return new ModelMapper().map(clienteVaga, EstacionamentoResponseDTO.class);
    }
}
