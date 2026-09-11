package br.com.tucunare.apoiodigital.usuario.controller;

import br.com.tucunare.apoiodigital.cliente.data.Cliente;
import br.com.tucunare.apoiodigital.security.TenantContext;
import br.com.tucunare.apoiodigital.usuario.data.RegistrarUsuarioRequestDTO;
import br.com.tucunare.apoiodigital.usuario.data.Usuario;
import br.com.tucunare.apoiodigital.usuario.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final TenantContext tenantContext;

    public UsuarioController(UsuarioService usuarioService, TenantContext tenantContext) {
        this.usuarioService = usuarioService;
        this.tenantContext = tenantContext;
    }

    /**
     * Called by CaneSDK.registerUser() on the partner's side. The partner has already
     * anonymized whatever identifies the end user on their side; we just mint an id for them,
     * scoped to the authenticated Cliente (resolved from x-api-key), and hand it back so the
     * SDK can use it as userId in every subsequent call.
     */
    @PostMapping("/registrar")
    public ResponseEntity<Usuario> registrar(@RequestBody RegistrarUsuarioRequestDTO request) {
        Cliente cliente = tenantContext.getClienteAtual();
        Usuario usuario = usuarioService.registrar(request.nome(), cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }
}
