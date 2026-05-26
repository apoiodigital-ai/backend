package br.com.tucunare.apoiodigital.auth.service;

import br.com.tucunare.apoiodigital.auth.data.RefreshToken;
import br.com.tucunare.apoiodigital.auth.repository.RefreshTokenRepository;
import br.com.tucunare.apoiodigital.usuario.data.Usuario;
import br.com.tucunare.apoiodigital.usuario.repository.UsuarioRepository;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final CleanerTokenService cleanerTokenService;
    private final UsuarioRepository usuarioRepository;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            CleanerTokenService cleanerTokenService,
            UsuarioRepository usuarioRepository
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.cleanerTokenService = cleanerTokenService;
        this.usuarioRepository = usuarioRepository;
    }

    public RefreshToken createRefreshToken(Usuario usuario) {

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsuario(usuario);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiracao(
                Instant.now().plus(7, ChronoUnit.DAYS)
        );

        return refreshTokenRepository.save(refreshToken);
    }

    public boolean validateRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(rt ->
                        !rt.isRevogado()
                                && rt.getExpiracao().isAfter(Instant.now())
                )
                .orElse(false);
    }

    public Usuario findUserByToken(String refreshTokenString) {

        Optional<RefreshToken> tokenOpt =
                refreshTokenRepository.findByToken(refreshTokenString);

        if (tokenOpt.isEmpty() || tokenOpt.get().isRevogado()) {
            throw new IllegalStateException(
                    "Refresh token inválido ou expirado"
            );
        }

        return usuarioRepository.findById(
                        tokenOpt.get().getUsuario().getId()
                )
                .orElseThrow(() ->
                        new IllegalStateException("Usuário não encontrado")
                );
    }

}
