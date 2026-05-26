package br.com.tucunare.apoiodigital.usuario.repository;

import br.com.tucunare.apoiodigital.usuario.data.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByTelefone(String telefone);
}
