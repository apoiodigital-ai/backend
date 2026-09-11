package br.com.tucunare.apoiodigital.usuario.service;

import br.com.tucunare.apoiodigital.cliente.data.Cliente;
import br.com.tucunare.apoiodigital.usuario.data.Usuario;
import br.com.tucunare.apoiodigital.usuario.exception.UsuarioDoesNotExistException;
import br.com.tucunare.apoiodigital.usuario.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario registrar(String nome, Cliente cliente) {
        Usuario usuario = new Usuario(nome, cliente);
        return usuarioRepository.save(usuario);
    }

    /**
     * Loads a Usuario by id, scoped to the given tenant. Throws the same 404 whether the id
     * does not exist at all or belongs to a different Cliente, so a caller cannot use the
     * response to enumerate ids that belong to other tenants.
     */
    public Usuario buscarPorIdEValidarTenant(UUID id, Cliente cliente) {
        return usuarioRepository.findByIdAndClienteId(id, cliente.getId())
                .orElseThrow(UsuarioDoesNotExistException::new);
    }
}
