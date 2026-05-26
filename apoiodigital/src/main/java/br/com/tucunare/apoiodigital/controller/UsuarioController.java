package br.com.tucunare.apoiodigital.controller;

import br.com.tucunare.apoiodigital.model.Usuario;
import br.com.tucunare.apoiodigital.requisicao.InitialRequisicaoService;
import br.com.tucunare.apoiodigital.service.impl.AtalhoService;
import br.com.tucunare.apoiodigital.requisicao.RequisicaoService;
import br.com.tucunare.apoiodigital.service.impl.UsuarioService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final InitialRequisicaoService initialRequisicaoService;
    private final AtalhoService atalhoService;

    public UsuarioController(
            UsuarioService usuarioService,
            InitialRequisicaoService initialRequisicaoService,
            AtalhoService atalhoService
    ) {
        this.usuarioService = usuarioService;
        this.initialRequisicaoService = initialRequisicaoService;
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
                initialRequisicaoService.salvarRequisicoesIniciais(usuarioPersistido.getId());

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
