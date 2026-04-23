package br.com.tucunare.apoiodigital.repository;

import br.com.tucunare.apoiodigital.dto.AppSuportadoToGeminiDTO;
import br.com.tucunare.apoiodigital.model.AppSuportado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AppSuportadoRepository extends JpaRepository<AppSuportado, Long> {

    @Query("SELECT new br.com.tucunare.apoiodigital.dto.AppSuportadoToGeminiDTO(a.id, a.nome) FROM AppSuportado a")
    List<AppSuportadoToGeminiDTO> findAllApps();

    Optional<AppSuportado> findByPacote();
}
