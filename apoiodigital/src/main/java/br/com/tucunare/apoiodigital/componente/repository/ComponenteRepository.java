package br.com.tucunare.apoiodigital.componente.repository;

import br.com.tucunare.apoiodigital.componente.data.Componente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComponenteRepository extends JpaRepository<Componente, Long> {
}
