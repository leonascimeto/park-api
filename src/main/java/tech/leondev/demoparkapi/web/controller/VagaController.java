package tech.leondev.demoparkapi.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.leondev.demoparkapi.entity.Vaga;
import tech.leondev.demoparkapi.service.VagaService;
import tech.leondev.demoparkapi.web.dto.UsuarioResponseDTO;
import tech.leondev.demoparkapi.web.dto.VagaCreateDTO;
import tech.leondev.demoparkapi.web.dto.VagaResponseDTO;
import tech.leondev.demoparkapi.web.dto.mapper.VagaMapper;
import tech.leondev.demoparkapi.web.exception.ErrorMessage;

import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/vagas")
public class VagaController {
    private final VagaService vagaService;

    @Operation(summary = "Criar uma nova vaga", description = "Recurso para criar uma nova vaga",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso",
                            headers = @Header(name = HttpHeaders.LOCATION, description = "URL do recurso criado")
                    ),
                    @ApiResponse(responseCode = "409", description = "Código da vaga já cadastrado no sistema",
                            content = @Content(mediaType = "Application/json", schema = @Schema(implementation = ErrorMessage.class))
                    ),
                    @ApiResponse(responseCode = "422", description = "Recurso não processado devido a entrada de dados inválidos",
                            content = @Content(mediaType = "Application/json", schema = @Schema(implementation = ErrorMessage.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil diferente de ADMIN",
                            content = @Content(mediaType = "Application/json", schema = @Schema(implementation = ErrorMessage.class))
                    ),
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> create(@RequestBody @Valid VagaCreateDTO vagaCreateDTO){
        log.info("[start] VagaController - create");
        Vaga vaga = VagaMapper.toVaga(vagaCreateDTO);
        vagaService.salvar(vaga);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri().path("/{codigo}")
                .buildAndExpand(vaga.getCodigo())
                        .toUri();
        log.info("[end] VagaController - create");
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Localizar uma vaga", description = "Recurso para localizar uma vaga pelo código",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso encontrado com sucesso",
                            headers = @Header(name = HttpHeaders.LOCATION, description = "URL do recurso criado")
                    ),
                    @ApiResponse(responseCode = "404", description = "Código da vaga nao encontrado",
                            content = @Content(mediaType = "Application/json", schema = @Schema(implementation = ErrorMessage.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil diferente de ADMIN",
                            content = @Content(mediaType = "Application/json", schema = @Schema(implementation = ErrorMessage.class))
                    ),
            }
    )
    @GetMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VagaResponseDTO> getByCodigo(@PathVariable String codigo){
        log.info("[start] VagaController - create");
        Vaga vaga = vagaService.buscarPorCodigo(codigo);
        log.info("[end] VagaController - create");
        return ResponseEntity.ok(VagaMapper.toVagaResponseDTO(vaga));
    }
}
