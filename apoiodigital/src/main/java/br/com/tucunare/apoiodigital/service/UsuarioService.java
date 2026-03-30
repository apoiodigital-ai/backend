package br.com.tucunare.apoiodigital.service;

import br.com.tucunare.apoiodigital.exception.TelefoneAlreayExistsException;
import br.com.tucunare.apoiodigital.exception.UsuarioDoesNotExistException;
import br.com.tucunare.apoiodigital.model.RefreshToken;
import br.com.tucunare.apoiodigital.model.Usuario;
import br.com.tucunare.apoiodigital.repository.RefreshTokenRepository;
import br.com.tucunare.apoiodigital.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    private String criptografarSenha(String senha) {
        return passwordEncoder.encode(senha);
    }

    public Usuario salvarUsuario(Usuario usuario) {

        var usuarioExiste =
                usuarioRepository.findByTelefone(usuario.getTelefone()).isPresent();

        if (usuarioRepository.findByTelefone(usuario.getTelefone()).isPresent()) {
            throw new TelefoneAlreayExistsException();
        }

        usuario.validarSenha();
        usuario.setSenha(criptografarSenha(usuario.getSenha()));

        return usuarioRepository.save(usuario);
    }

    public Map<String, String> validarLogin(String telefone, String senha) {

        Usuario usuario = usuarioRepository.findByTelefone(telefone)
                .orElseThrow(UsuarioDoesNotExistException::new);

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
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
