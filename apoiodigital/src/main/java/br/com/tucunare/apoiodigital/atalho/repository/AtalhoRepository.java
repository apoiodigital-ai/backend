package br.com.tucunare.apoiodigital.atalho.repository;

import br.com.tucunare.apoiodigital.atalho.data.Atalho;
import br.com.tucunare.apoiodigital.requisicao.data.Requisicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AtalhoRepository extends JpaRepository<Atalho, UUID> {

    @Query("""
        SELECT a
        FROM Atalho a
        WHERE a.requisicao.usuario.id = :usuarioId
        ORDER BY a.criacao DESC
    """)
    List<Atalho> findByRequisicaoUsuarioId(@Param("usuarioId") UUID usuarioId);

    Optional<Atalho> findByRequisicao(Requisicao requisicao);
}
