package br.com.tucunare.apoiodigital.service;

import br.com.tucunare.apoiodigital.model.RefreshToken;
import br.com.tucunare.apoiodigital.repository.RefreshTokenRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class CleanerTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public CleanerTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Scheduled(cron = "0 58 23 * * ?")
    public void verificarExpiracao() {

        List<RefreshToken> expirados =
                refreshTokenRepository
                        .findAllByExpiracaoBeforeAndRevogadoFalse(Instant.now());

        expirados.forEach(token -> token.setRevogado(true));

        refreshTokenRepository.saveAll(expirados);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void deletarRefreshExpirado() {
        refreshTokenRepository.deleteByRevogadoTrue();
    }
}
