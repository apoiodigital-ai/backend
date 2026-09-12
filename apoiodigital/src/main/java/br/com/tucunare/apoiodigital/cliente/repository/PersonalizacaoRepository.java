package br.com.tucunare.apoiodigital.cliente.repository;

import br.com.tucunare.apoiodigital.cliente.data.Personalizacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PersonalizacaoRepository extends JpaRepository<Personalizacao, UUID> {
    List<Personalizacao> findByClienteId(UUID clienteId);
}
