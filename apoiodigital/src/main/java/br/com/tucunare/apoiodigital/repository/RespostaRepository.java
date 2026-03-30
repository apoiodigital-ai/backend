package br.com.tucunare.apoiodigital.repository;

import br.com.tucunare.apoiodigital.model.Resposta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RespostaRepository extends JpaRepository<Resposta, UUID> {
    @Query("SELECT r.mensagem, rq.id " +
            "FROM Resposta r  " +
            "JOIN r.requisicao rq " +
            "WHERE rq.id = :id order by r.criacao asc "
    )
    List<Object[]> listarRespostaPorIdRequisicao(@Param("id") UUID requisicaoId);
}
