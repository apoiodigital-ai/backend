package br.com.tucunare.apoiodigital.controller;

import br.com.tucunare.apoiodigital.model.Usuario;
import br.com.tucunare.apoiodigital.service.AtalhoService;
import br.com.tucunare.apoiodigital.service.RequisicaoService;
import br.com.tucunare.apoiodigital.service.UsuarioService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RequisicaoService requisicaoService;
    private final AtalhoService atalhoService;

    public UsuarioController(
            UsuarioService usuarioService,
            RequisicaoService requisicaoService,
            AtalhoService atalhoService
    ) {
        this.usuarioService = usuarioService;
        this.requisicaoService = requisicaoService;
        this.atalhoService = atalhoService;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @PostMapping("/salvar")
    public ResponseEntity<Usuario> criarConta(@RequestBody Usuario usuario) {
        Usuario usuarioPersistido = usuarioService.salvarUsuario(usuario);

        var requisicoesIniciais =
                requisicaoService.salvarRequisicoesIniciais(usuarioPersistido.getId());

        atalhoService.salvarAtalhosIniciais(requisicoesIniciais);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioPersistido);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> buscarUsuarioPorToken(
            @RequestParam String token
    ) {
        return ResponseEntity.ok(
                usuarioService.getUsuarioIdByAccessToken(token)
        );
    }
}
