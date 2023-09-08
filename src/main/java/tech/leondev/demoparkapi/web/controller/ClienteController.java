package tech.leondev.demoparkapi.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.leondev.demoparkapi.entity.Cliente;
import tech.leondev.demoparkapi.jwt.JwtUserDetails;
import tech.leondev.demoparkapi.service.ClienteService;
import tech.leondev.demoparkapi.service.UsuarioService;
import tech.leondev.demoparkapi.web.dto.ClienteCreateDTO;
import tech.leondev.demoparkapi.web.dto.ClienteResponseDTO;
import tech.leondev.demoparkapi.web.dto.mapper.ClienteMapper;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/clientes")
@Slf4j
public class ClienteController {

    private final ClienteService clienteService;
    private final UsuarioService usuarioService;

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
}
