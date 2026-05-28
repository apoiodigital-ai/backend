package br.com.tucunare.apoiodigital.usuario.service;

import br.com.tucunare.apoiodigital.auth.service.JwtService;
import br.com.tucunare.apoiodigital.auth.service.RefreshTokenService;
import br.com.tucunare.apoiodigital.usuario.data.Usuario;
import br.com.tucunare.apoiodigital.usuario.exception.UsuarioDoesNotExistException;
import br.com.tucunare.apoiodigital.usuario.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UsuarioTokenService {

    private final RefreshTokenService refreshTokenService;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public UsuarioTokenService(RefreshTokenService refreshTokenService, UsuarioRepository usuarioRepository, JwtService jwtService) {
        this.refreshTokenService = refreshTokenService;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    public Usuario findUserByRefreshToken(String refreshToken) {
        return refreshTokenService.findUserByToken(refreshToken);
    }

    public Usuario getUsuarioByAccessToken(String accessToken) {

        UUID usuarioId = jwtService.getUsuarioIdByToken(accessToken);

        return usuarioRepository.findById(usuarioId)
                .orElseThrow(UsuarioDoesNotExistException::new);
    }

    public Map<String, Object> getUsuarioIdByAccessToken(String accessToken) {

        Map<String, Object> response = new HashMap<>();
        response.put("userId", jwtService.getUsuarioIdByToken(accessToken));

        return response;
    }

}
