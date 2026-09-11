package br.com.tucunare.apoiodigital.resposta.repository;

import br.com.tucunare.apoiodigital.resposta.data.Resposta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RespostaRepository extends JpaRepository<Resposta, UUID> {

    @Query("SELECT r.mensagem, p.id " +
            "FROM Resposta r " +
            "JOIN r.pedido p " +
            "WHERE p.id = :id order by r.timestamp asc "
    )
    List<Object[]> listarRespostaPorIdPedido(@Param("id") UUID pedidoId);

    Optional<Resposta> findFirstByPedidoIdOrderByTimestampDesc(UUID pedidoId);

    /**
     * Scoped to the authenticated tenant via Resposta -> Pedido -> Usuario -> Cliente, so a
     * caller can never use idResposta to reach another partner's data.
     */
    Optional<Resposta> findByIdAndPedido_Usuario_Cliente_Id(UUID id, UUID clienteId);
}
