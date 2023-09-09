package tech.leondev.demoparkapi.web.dto.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import tech.leondev.demoparkapi.entity.Vaga;
import tech.leondev.demoparkapi.web.dto.VagaCreateDTO;
import tech.leondev.demoparkapi.web.dto.VagaResponseDTO;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VagaMapper {
    public static Vaga toVaga(VagaCreateDTO vagaCreateDTO){
        return new ModelMapper().map(vagaCreateDTO, Vaga.class);
    }

    public static VagaResponseDTO toVagaResponseDTO(Vaga vaga){
        return new ModelMapper().map(vaga, VagaResponseDTO.class);
    }
}
