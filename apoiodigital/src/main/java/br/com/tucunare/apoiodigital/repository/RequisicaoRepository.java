package br.com.tucunare.apoiodigital.repository;

import br.com.tucunare.apoiodigital.model.Requisicao;
import br.com.tucunare.apoiodigital.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RequisicaoRepository extends JpaRepository<Requisicao, UUID> {
    List<Requisicao> findByUsuario(Usuario usuario);
    Optional<Requisicao> findFirstByPromptAndUsuarioOrderByCriacaoDesc(String prompt, Usuario usuario);
}
