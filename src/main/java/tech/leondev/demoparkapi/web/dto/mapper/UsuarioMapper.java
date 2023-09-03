package tech.leondev.demoparkapi.web.dto.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import tech.leondev.demoparkapi.entity.Usuario;
import tech.leondev.demoparkapi.web.dto.UsuarioCreateDTO;
import tech.leondev.demoparkapi.web.dto.UsuarioResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class UsuarioMapper {

    public static Usuario toUsuario(UsuarioCreateDTO usuarioCreateDTO){
        return new ModelMapper().map(usuarioCreateDTO, Usuario.class);
    }

    public static UsuarioResponseDTO toResponseDTO(Usuario usuario){
        String role = usuario.getRole().name().substring("ROLE_".length());
        PropertyMap<Usuario, UsuarioResponseDTO> props = new PropertyMap<Usuario, UsuarioResponseDTO>() {
            @Override
            protected void configure() {
                map().setRole(role);
            }
        };
        ModelMapper mapper = new ModelMapper();
        mapper.addMappings(props);
        return mapper.map(usuario, UsuarioResponseDTO.class);
    }

    public static List<UsuarioResponseDTO> toUsuarioList(List<Usuario> usuarios){
        return usuarios.stream().map(UsuarioMapper::toResponseDTO).collect(Collectors.toList());
    }

}
