package br.com.tucunare.apoiodigital.pedido.repository;

import br.com.tucunare.apoiodigital.pedido.data.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    /**
     * Scoped to the Usuario it belongs to, so idPedido can never be used to reach another
     * user's (and therefore another tenant's) pending clarification state.
     */
    Optional<Pedido> findByIdAndUsuarioId(UUID id, UUID usuarioId);
}
