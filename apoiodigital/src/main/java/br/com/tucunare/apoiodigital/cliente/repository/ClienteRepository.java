package br.com.tucunare.apoiodigital.cliente.repository;

import br.com.tucunare.apoiodigital.cliente.data.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {
    Optional<Cliente> findByAccessKey(String accessKey);
}
