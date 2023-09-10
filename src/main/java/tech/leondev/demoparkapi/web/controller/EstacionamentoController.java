package tech.leondev.demoparkapi.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.leondev.demoparkapi.entity.ClienteVaga;
import tech.leondev.demoparkapi.service.EstacionamentoService;
import tech.leondev.demoparkapi.web.dto.EstacionamentoCreateDTO;
import tech.leondev.demoparkapi.web.dto.EstacionamentoResponseDTO;
import tech.leondev.demoparkapi.web.dto.mapper.ClienteVagaMapper;
import tech.leondev.demoparkapi.web.exception.ErrorMessage;

import java.net.URI;

@Tag(name = "Estacionamentos", description = "Contém todas as operações relativas ao recurso do estacionamento")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/estacionamentos")
public class EstacionamentoController {
    private final EstacionamentoService estacionamentoService;

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
}
