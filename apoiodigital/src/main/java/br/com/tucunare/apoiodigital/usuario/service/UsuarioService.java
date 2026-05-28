package br.com.tucunare.apoiodigital.usuario.service;

import br.com.tucunare.apoiodigital.auth.service.JwtService;
import br.com.tucunare.apoiodigital.auth.service.RefreshTokenService;
import br.com.tucunare.apoiodigital.auth.data.RefreshToken;
import br.com.tucunare.apoiodigital.usuario.data.Usuario;
import br.com.tucunare.apoiodigital.usuario.exception.InvalidPasswordLengthException;
import br.com.tucunare.apoiodigital.usuario.exception.TelefoneAlreayExistsException;
import br.com.tucunare.apoiodigital.usuario.exception.UsuarioDoesNotExistException;
import br.com.tucunare.apoiodigital.usuario.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncryptionService passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordValidationService passwordValidationService;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncryptionService passwordEncoder,
            JwtService jwtService,
            RefreshTokenService refreshTokenService, PasswordValidationService passWordValidationService

    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.passwordValidationService = passWordValidationService;
    }

    public Usuario salvarUsuario(Usuario usuario) {

        if (usuarioRepository.findByTelefone(usuario.getTelefone()).isPresent()) {
            throw new TelefoneAlreayExistsException();
        }

        if(!passwordValidationService.validar(usuario.getSenha())) throw new InvalidPasswordLengthException();
        usuario.setSenha(passwordEncoder.criptografar(usuario.getSenha()));

        return usuarioRepository.save(usuario);
    }

    public Map<String, String> validarLogin(String telefone, String senha) {

        Usuario usuario = usuarioRepository.findByTelefone(telefone)
                .orElseThrow(UsuarioDoesNotExistException::new);

        if (!passwordEncoder.validar(senha, usuario.getSenha())) {
            throw new UsuarioDoesNotExistException();
        }

        String accessToken = jwtService.gerarToken(usuario);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(usuario);

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken.getToken()
        );
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
