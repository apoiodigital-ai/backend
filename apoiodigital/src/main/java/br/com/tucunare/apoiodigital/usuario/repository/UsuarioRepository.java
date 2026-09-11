package br.com.tucunare.apoiodigital.usuario.repository;

import br.com.tucunare.apoiodigital.usuario.data.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    /**
     * The only safe way to look up an end user by client-supplied id: it is scoped to the
     * authenticated tenant, so a UUID belonging to another partner's user never resolves here
     * (this is the fix for the IDOR pattern where any UUID would return that user's data).
     */
    Optional<Usuario> findByIdAndClienteId(UUID id, UUID clienteId);
}
