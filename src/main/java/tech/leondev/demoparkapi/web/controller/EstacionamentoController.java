package tech.leondev.demoparkapi.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.leondev.demoparkapi.entity.ClienteVaga;
import tech.leondev.demoparkapi.jwt.JwtUserDetails;
import tech.leondev.demoparkapi.repository.projection.ClienteVagaProjection;
import tech.leondev.demoparkapi.service.ClienteVagaService;
import tech.leondev.demoparkapi.service.EstacionamentoService;
import tech.leondev.demoparkapi.web.dto.EstacionamentoCreateDTO;
import tech.leondev.demoparkapi.web.dto.EstacionamentoResponseDTO;
import tech.leondev.demoparkapi.web.dto.PageableDTO;
import tech.leondev.demoparkapi.web.dto.mapper.ClienteVagaMapper;
import tech.leondev.demoparkapi.web.dto.mapper.PageableMapper;
import tech.leondev.demoparkapi.web.exception.ErrorMessage;

import java.net.URI;

@Tag(name = "Estacionamentos", description = "Contém todas as operações relativas ao recurso do estacionamento")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/estacionamentos")
public class EstacionamentoController {
    private final EstacionamentoService estacionamentoService;
    private final ClienteVagaService clienteVagaService;

    @Operation(summary = "operação de check-in", description = "Recurso para dar entrada de um veiculo no estacionamento. " +
            "Requisição exige uso de um Bearer token. Acesso restrito a role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso",
                            headers = @Header(name = HttpHeaders.LOCATION, description = "URL do recurso criado"),
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = EstacionamentoResponseDTO.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "CPF do cliente ou Vaga livre não encontrada",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    ),
                    @ApiResponse(responseCode = "422", description = "Recurso não processado devido a entrada de dados inválidos",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil diferente de ADMIN",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    ),
            }
    )
    @PostMapping("/checkin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstacionamentoResponseDTO> checkin(@RequestBody @Valid EstacionamentoCreateDTO estacionamentoCreateDTO){
        log.info("[start] EstacionamentoController - checkin");
        ClienteVaga clienteVaga = ClienteVagaMapper.toClienteVaga(estacionamentoCreateDTO);
        estacionamentoService.checkin(clienteVaga);
        EstacionamentoResponseDTO response = ClienteVagaMapper.toClienteVagaDTO(clienteVaga);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri().path("/{recibo}")
                .buildAndExpand(clienteVaga.getRecibo())
                .toUri();
        log.info("[end] EstacionamentoController - checkin");
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Localizar um veículo estacionado", description = "Recurso para retornar um veículo estacionado " +
            "pelo nº do recibo. Requisição exige uso de um bearer token.",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "recibo", description = "Número do rebibo gerado pelo check-in")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = EstacionamentoResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Número do recibo não encontrado.",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping("/checkin/{recibo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<EstacionamentoResponseDTO> getByRecibo(@PathVariable String recibo){
        log.info("[start] EstacionamentoController - getByRecibo");
        ClienteVaga clienteVaga = clienteVagaService.getByRecibo(recibo);
        EstacionamentoResponseDTO estacionamentoResponseDTO = ClienteVagaMapper.toClienteVagaDTO(clienteVaga);
        log.info("[end] EstacionamentoController - getByRecibo");
        return ResponseEntity.ok(estacionamentoResponseDTO);
    }

    @Operation(summary = "Operação de check-out", description = "Recurso para dar saída de um veículo do estacionamento. " +
            "Requisição exige uso de um bearer token. Acesso restrito a Role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            parameters = { @Parameter(in = ParameterIn.PATH, name = "recibo", description = "Número do rebibo gerado pelo check-in",
                    required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso atualzado com sucesso",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = EstacionamentoResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Número do recibo inexistente ou " +
                            "o veículo já passou pelo check-out.",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permito ao perfil de CLIENTE",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PutMapping("/checkout/{recibo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstacionamentoResponseDTO> checkout(@PathVariable String recibo) {
        log.info("[start] EstacionamentoController - checkout");
        ClienteVaga clienteVaga = estacionamentoService.checkOut(recibo);
        EstacionamentoResponseDTO estacionamentoResponseDTO = ClienteVagaMapper.toClienteVagaDTO(clienteVaga);
        log.info("[end] EstacionamentoController - checkout");
        return ResponseEntity.ok(estacionamentoResponseDTO);
    }

    @Operation(summary = "Localizar os registros de estacionamentos do cliente por CPF", description = "Localizar os " +
            "registros de estacionamentos do cliente por CPF. Requisição exige uso de um bearer token.",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "cpf", description = "Nº do CPF referente ao cliente a ser consultado",
                            required = true
                    ),
                    @Parameter(in = ParameterIn.QUERY, name = "page", description = "Representa a página retornada",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0"))
                    ),
                    @Parameter(in = ParameterIn.QUERY, name = "size", description = "Representa o total de elementos por página",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "5"))
                    ),
                    @Parameter(in = ParameterIn.QUERY, name = "sort", description = "Campo padrão de ordenação 'dataEntrada,asc'. ",
                            array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "dataEntrada,asc")),
                            hidden = true
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = PageableDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permito ao perfil de CLIENTE",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping("/cpf/{cpf}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageableDTO> getAllByCpf(@PathVariable String cpf, @Parameter(hidden = true)
    @PageableDefault(size = 5, sort = "dataEntrada",
            direction = Sort.Direction.ASC) Pageable pageable){
        log.info("[start] EstacionamentoController - getAllByCpf");
        Page<ClienteVagaProjection> projection = clienteVagaService.buscarTodosPorCpf(cpf, pageable);
        PageableDTO dto = PageableMapper.toDto(projection);
        log.info("[end] EstacionamentoController - getAllByCpf");
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Localizar os registros de estacionamentos do cliente logado",
            description = "Localizar os registros de estacionamentos do cliente logado. " +
                    "Requisição exige uso de um bearer token.",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = ParameterIn.QUERY, name = "page",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                            description = "Representa a página retornada"
                    ),
                    @Parameter(in = ParameterIn.QUERY, name = "size",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "5")),
                            description = "Representa o total de elementos por página"
                    ),
                    @Parameter(in = ParameterIn.QUERY, name = "sort", hidden = true,
                            array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "dataEntrada,asc")),
                            description = "Campo padrão de ordenação 'dataEntrada,asc'. ")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = EstacionamentoResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permito ao perfil de ADMIN",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<PageableDTO> getAllEstacionamentosDoCliente(@AuthenticationPrincipal JwtUserDetails user,
                                                                      @Parameter(hidden = true) @PageableDefault(
                                                                              size = 5, sort = "dataEntrada",
                                                                              direction = Sort.Direction.ASC) Pageable pageable) {

        Page<ClienteVagaProjection> projection = clienteVagaService.buscarTodosPorUsuarioId(user.getId(), pageable);
        PageableDTO dto = PageableMapper.toDto(projection);
        return ResponseEntity.ok(dto);
    }
}
