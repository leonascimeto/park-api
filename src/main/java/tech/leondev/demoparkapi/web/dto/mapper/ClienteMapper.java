package tech.leondev.demoparkapi.web.dto.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import tech.leondev.demoparkapi.entity.Cliente;
import tech.leondev.demoparkapi.web.dto.ClienteCreateDTO;
import tech.leondev.demoparkapi.web.dto.ClienteResponseDTO;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClienteMapper {
    public static Cliente toCliente(ClienteCreateDTO clienteCreateDTO){
        return new ModelMapper().map(clienteCreateDTO, Cliente.class);
    }

    public static ClienteResponseDTO toClienteResponseDTO(Cliente cliente){
        return new ModelMapper().map(cliente, ClienteResponseDTO.class);
    }
}
