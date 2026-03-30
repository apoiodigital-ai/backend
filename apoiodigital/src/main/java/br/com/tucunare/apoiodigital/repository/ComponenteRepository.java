package br.com.tucunare.apoiodigital.repository;

import br.com.tucunare.apoiodigital.model.Componente;
import br.com.tucunare.apoiodigital.model.Resposta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComponenteRepository extends JpaRepository<Componente, Long> {
}
