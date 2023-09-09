package tech.leondev.demoparkapi.web.dto.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import tech.leondev.demoparkapi.web.dto.PageableDTO;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageableMapper {

    public static PageableDTO toDto(Page page){
        return new ModelMapper().map(page, PageableDTO.class);
    }
}
