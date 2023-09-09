package tech.leondev.demoparkapi.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tech.leondev.demoparkapi.entity.Cliente;
import tech.leondev.demoparkapi.jwt.JwtUserDetails;
import tech.leondev.demoparkapi.repository.projection.ClienteProjection;
import tech.leondev.demoparkapi.service.ClienteService;
import tech.leondev.demoparkapi.service.UsuarioService;
import tech.leondev.demoparkapi.web.dto.ClienteCreateDTO;
import tech.leondev.demoparkapi.web.dto.ClienteResponseDTO;
import tech.leondev.demoparkapi.web.dto.PageableDTO;
import tech.leondev.demoparkapi.web.dto.UsuarioResponseDTO;
import tech.leondev.demoparkapi.web.dto.mapper.ClienteMapper;
import tech.leondev.demoparkapi.web.dto.mapper.PageableMapper;
import tech.leondev.demoparkapi.web.exception.ErrorMessage;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

@Tag(name = "Clientes", description = "Contém todas as operações relativas ao recurso de um cliente")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/clientes")
@Slf4j
public class ClienteController {

    private final ClienteService clienteService;
    private final UsuarioService usuarioService;

    @Operation(summary = "Criar um novo cliente", description = "Recurso para criar um novo cliente",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso",
                            content = @Content(mediaType = "Application/json",
                                    schema = @Schema(implementation = UsuarioResponseDTO.class))
                    ),
                    @ApiResponse(responseCode = "409", description = "Cliente com CPF já cadastrado no sistema",
                            content = @Content(mediaType = "Application/json", schema = @Schema(implementation = ErrorMessage.class))
                    ),
                    @ApiResponse(responseCode = "422", description = "Recurso não processado devido a entrada de dados inválidos",
                            content = @Content(mediaType = "Application/json", schema = @Schema(implementation = ErrorMessage.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil ADMIN",
                            content = @Content(mediaType = "Application/json", schema = @Schema(implementation = ErrorMessage.class))
                    ),
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ClienteResponseDTO> create(@RequestBody @Valid ClienteCreateDTO clienteCreateDTO,
                                                     @AuthenticationPrincipal JwtUserDetails userDetails){
        log.info("[start] ClienteController - create");
        Cliente cliente = ClienteMapper.toCliente(clienteCreateDTO);
        cliente.setUsuario(usuarioService.buscarPorId(userDetails.getId()));
        clienteService.salvar(cliente);
        log.info("[end] ClienteController - create");
        return ResponseEntity.status(201).body(ClienteMapper.toClienteResponseDTO(cliente));
    }

    @Operation(summary = "Buscar cliente", description = "Recurso para localizar cliente pelo ID",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = "Application/json",
                                    schema = @Schema(implementation = UsuarioResponseDTO.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                            content = @Content(mediaType = "Application/json", schema = @Schema(implementation = ErrorMessage.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil que não é ADMIN",
                            content = @Content(mediaType = "Application/json", schema = @Schema(implementation = ErrorMessage.class))
                    ),
            }
    )
    @GetMapping("/{idCliente}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteResponseDTO> getById(@PathVariable Long idCliente){
        log.info("[start] ClienteController - getById");
        Cliente cliente = clienteService.buscarPorId(idCliente);
        log.info("[end] ClienteController - getById");
        return ResponseEntity.status(200).body(ClienteMapper.toClienteResponseDTO(cliente));
    }

    @Operation(summary = "Buscar lista de clientes", description = "Requisição exige uso de bearer token. Acesso restrito a role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = QUERY, name = "page",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                            description = "Representa o págna retornada"
                    ),
                    @Parameter(in = QUERY, name = "size",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "20")),
                            description = "Representa o total de elementos por página"
                    ),
                    @Parameter(in = QUERY, name = "sort", hidden = true,
                            array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "id,asc")),
                            description = "Representa a ordenação do resultado"
                    ),
            },
            responses = {

                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = "Application/json",
                                    schema = @Schema(implementation = UsuarioResponseDTO.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil que não é ADMIN",
                            content = @Content(mediaType = "Application/json", schema = @Schema(implementation = ErrorMessage.class))
                    ),
            }
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageableDTO> getAll(@Parameter(hidden = true) @PageableDefault(size = 5) Pageable pageable){
        log.info("[start] ClienteController - getAll");
        Page<ClienteProjection> clientes = clienteService.buscarTodosClientes(pageable);
        log.info("[end] ClienteController - getAll");
        return ResponseEntity.ok().body(PageableMapper.toDto(clientes));
    }
}
