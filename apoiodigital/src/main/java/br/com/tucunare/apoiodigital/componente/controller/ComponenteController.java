package br.com.tucunare.apoiodigital.componente.controller;

import br.com.tucunare.apoiodigital.cliente.data.Cliente;
import br.com.tucunare.apoiodigital.componente.data.ComponenteCompararRequestDTO;
import br.com.tucunare.apoiodigital.componente.data.ComponenteCompararResponseDTO;
import br.com.tucunare.apoiodigital.componente.service.ComponenteService;
import br.com.tucunare.apoiodigital.security.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Saving a Componente's assinatura happens automatically as part of
 * {@code POST /resposta/achar-resposta} (see AcharRespostaService) — there is no separate
 * "save" endpoint anymore, since the SDK always has a fresh element list on hand exactly when
 * that response is produced. This controller only exposes the cache-check itself: "is the
 * instruction you already have for this Resposta still valid for the screen in front of you
 * right now?", so the SDK can skip a fresh AI call when the answer is yes.
 */
@RestController
@RequestMapping("/componentes")
public class ComponenteController {

    private final ComponenteService componenteService;
    private final TenantContext tenantContext;

    public ComponenteController(ComponenteService componenteService, TenantContext tenantContext) {
        this.componenteService = componenteService;
        this.tenantContext = tenantContext;
    }

    @PostMapping("/comparar")
    public ResponseEntity<ComponenteCompararResponseDTO> comparar(
            @RequestBody ComponenteCompararRequestDTO request
    ) {
        Cliente cliente = tenantContext.getClienteAtual();
        boolean valido = componenteService.comparar(request.idResposta(), request.elementos(), cliente);
        return ResponseEntity.ok(new ComponenteCompararResponseDTO(valido));
    }
}
