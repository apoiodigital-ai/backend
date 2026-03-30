package br.com.tucunare.apoiodigital.repository;

import br.com.tucunare.apoiodigital.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByTelefone(String telefone);
}
