package tech.leondev.demoparkapi.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.leondev.demoparkapi.entity.Usuario;
import tech.leondev.demoparkapi.service.UsuarioService;
import tech.leondev.demoparkapi.web.dto.UsuarioCreateDTO;
import tech.leondev.demoparkapi.web.dto.UsuarioResponseDTO;
import tech.leondev.demoparkapi.web.dto.UsuarioUpdateSenhaDTO;
import tech.leondev.demoparkapi.web.dto.mapper.UsuarioMapper;
import tech.leondev.demoparkapi.web.exception.ErrorMessage;

import java.util.List;

@Tag(name = "Usuarios", description = "Contém todas as operação relativas aos recursos para cadastro, edição e leitura de um usuário.")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(summary = "Criar um novo usuário", description = "Recurso para criar um novo usuário",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso",
                        content = @Content(mediaType = "Application/json",
                        schema = @Schema(implementation = UsuarioResponseDTO.class))
                    ),
                    @ApiResponse(responseCode = "409", description = "Usuário e-mail já cadastrado no sistema",
                        content = @Content(mediaType = "Application/json", schema = @Schema(implementation = ErrorMessage.class))
                    ),
                    @ApiResponse(responseCode = "422", description = "Recurso não processado devido a entrada de dados inválidos",
                            content = @Content(mediaType = "Application/json", schema = @Schema(implementation = ErrorMessage.class))
                    ),
            }
    )
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> create(@Valid @RequestBody UsuarioCreateDTO usuarioCreateDTO){
        log.info("[start] UsuarioController - create");
        Usuario response = usuarioService.salvar(UsuarioMapper.toUsuario(usuarioCreateDTO));
        log.info("[end] UsuarioController - create");
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioMapper.toResponseDTO(response));
    }

    @Operation(summary = "Recuperar um usuário pelo ID", description = "Requisição exige um Bearer Token. Acesso restrito a ADMIN|ClIENTE",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso recuperado com sucesso",
                            content = @Content(mediaType = "Application/json",
                                    schema = @Schema(implementation = UsuarioResponseDTO.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão",
                            content = @Content(mediaType = "Application/json", schema = @Schema(implementation = ErrorMessage.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado",
                            content = @Content(mediaType = "Application/json", schema = @Schema(implementation = ErrorMessage.class))
                    ),
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') OR (hasRole('CLIENTE') AND #id == authentication.principal.id)")
    public ResponseEntity<UsuarioResponseDTO> getById(@PathVariable Long id){
        log.info("[start] UsuarioController - getById");
        Usuario response = usuarioService.buscarPorId(id);
        log.info("[end] UsuarioController - getById");
        return ResponseEntity.status(HttpStatus.OK).body(UsuarioMapper.toResponseDTO(response));
    }

    @Operation(summary = "Atualizar senha", description = "Requisição exige um Bearer Token. Acesso restrito a ADMIN|ClIENTE",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Senha atualizada com sucesso",
                            content = @Content(mediaType = "Application/json",
                                    schema = @Schema(implementation = Void.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Senha inválida",
                            content = @Content(mediaType = "Application/json", schema = @Schema(implementation = ErrorMessage.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão",
                            content = @Content(mediaType = "Application/json", schema = @Schema(implementation = ErrorMessage.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado",
                            content = @Content(mediaType = "Application/json", schema = @Schema(implementation = ErrorMessage.class))
                    ),
                    @ApiResponse(responseCode = "422", description = "Campos inválidos ou mal formatados",
                            content = @Content(mediaType = "Application/json", schema = @Schema(implementation = ErrorMessage.class))
                    ),
            }
    )
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENTE') AND (#id == authentication.principal.id)")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @Valid @RequestBody UsuarioUpdateSenhaDTO usuarioUpdateSenhaDTO){
        log.info("[start] UsuarioController - updatePassword");
        Usuario response = usuarioService.editarSenha(
                id,
                usuarioUpdateSenhaDTO.getSenhaAtual(),
                usuarioUpdateSenhaDTO.getNovaSenha(),
                usuarioUpdateSenhaDTO.getConfirmaSenha()
        );
        log.info("[end] UsuarioController - updatePassword");
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar todos os usuários", description = "Requisição exige um Bearer Token. Acesso restrito a ADMIN",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuários listados",
                            content = @Content(mediaType = "Application/json",
                                    schema = @Schema(implementation = UsuarioResponseDTO.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão",
                            content = @Content(mediaType = "Application/json", schema = @Schema(implementation = ErrorMessage.class))
                    )
            }
    )
    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponseDTO>> getAll(){
        log.info("[start] UsuarioController - getAll");
        List<Usuario> response = usuarioService.busrcarTodos();
        log.info("[end] UsuarioController - getAll");
        return ResponseEntity.status(HttpStatus.OK).body(UsuarioMapper.toUsuarioList(response));
    }
}
