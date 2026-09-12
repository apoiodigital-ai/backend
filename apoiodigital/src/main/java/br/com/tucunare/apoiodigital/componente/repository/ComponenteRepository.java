package br.com.tucunare.apoiodigital.componente.repository;

import br.com.tucunare.apoiodigital.componente.data.Componente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ComponenteRepository extends JpaRepository<Componente, UUID> {
    Optional<Componente> findFirstByRespostaIdOrderByIdDesc(UUID respostaId);
}
