package br.com.tucunare.apoiodigital.repository;

import br.com.tucunare.apoiodigital.model.RefreshToken;
import br.com.tucunare.apoiodigital.model.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    @Query("SELECT r FROM RefreshToken r WHERE r.expiracao < :now AND r.revogado = false")
    List<RefreshToken> findAllByExpiracaoBeforeAndRevogadoFalse(Instant now);

    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken r WHERE r.revogado = true")
    void deleteByRevogadoTrue();

}
