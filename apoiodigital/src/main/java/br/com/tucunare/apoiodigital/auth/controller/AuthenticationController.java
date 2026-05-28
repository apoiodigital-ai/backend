package br.com.tucunare.apoiodigital.auth.controller;

import br.com.tucunare.apoiodigital.auth.service.JwtService;
import br.com.tucunare.apoiodigital.auth.data.RefreshDTO;
import br.com.tucunare.apoiodigital.auth.service.RefreshTokenService;
import br.com.tucunare.apoiodigital.usuario.data.Usuario;
import br.com.tucunare.apoiodigital.usuario.service.UsuarioService;

import br.com.tucunare.apoiodigital.usuario.service.UsuarioTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private UsuarioTokenService usuarioTokenService;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam String telefone, @RequestParam String senha) {
        Map<String, String> tokens = usuarioService.validarLogin(telefone, senha);

        return ResponseEntity.status(HttpStatus.OK).body(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshDTO request) {
        String refreshToken = request.refreshToken();

        if (!refreshTokenService.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Refresh token inválido ou expirado");
        }

        Usuario usuario = usuarioTokenService.findUserByRefreshToken(refreshToken);

        String newAccessToken = jwtService.gerarToken(usuario);

        return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken
        ));
    }
}
