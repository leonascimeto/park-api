package tech.leondev.demoparkapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.leondev.demoparkapi.entity.Usuario;
import tech.leondev.demoparkapi.exception.EntityNotFoundException;
import tech.leondev.demoparkapi.exception.PasswordInvalidException;
import tech.leondev.demoparkapi.exception.UsernameUniqueViolationException;
import tech.leondev.demoparkapi.repository.UsuarioRepository;

import java.util.List;
@RequiredArgsConstructor
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario salvar(Usuario usuario) throws UsernameUniqueViolationException {
        try {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            return usuarioRepository.save(usuario);
        } catch (org.springframework.dao.DataIntegrityViolationException ex){
            throw  new UsernameUniqueViolationException(String.format("Username '%s' já cadatrado", usuario.getUsername()));
        }
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Usuario com id='%s' não encontrado", id)));
    }

    @Transactional
    public Usuario editarSenha(Long id, String senhaAtual, String novaSenha, String confirmaSenha) {
        if(!novaSenha.equals(confirmaSenha)) {
            throw new PasswordInvalidException("Nova senha não confere com a confirmação de senha.");
        }
        Usuario usuario = this.buscarPorId(id);
        if(!passwordEncoder.matches(senhaAtual, usuario.getPassword())){
            throw new PasswordInvalidException("Sua senha não confere.");
        }
         usuario.setPassword(passwordEncoder.encode(novaSenha));
        return usuario;
    }
    @Transactional(readOnly = true)
    public List<Usuario> busrcarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Usuario com id='%s' não encontrado", username)));

    }

    @Transactional(readOnly = true)
    public Usuario.Role buscarRolePorUsername(String username) {
        return usuarioRepository.findRoleByUsername(username);
    }
}
