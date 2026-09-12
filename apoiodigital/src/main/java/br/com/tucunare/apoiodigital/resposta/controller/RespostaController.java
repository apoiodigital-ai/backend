package br.com.tucunare.apoiodigital.resposta.controller;

import br.com.tucunare.apoiodigital.cliente.data.Cliente;
import br.com.tucunare.apoiodigital.resposta.data.*;
import br.com.tucunare.apoiodigital.resposta.service.AcharRespostaService;
import br.com.tucunare.apoiodigital.resposta.service.NecessidadeInformacoesService;
import br.com.tucunare.apoiodigital.resposta.service.RespostaNecessidadeService;
import br.com.tucunare.apoiodigital.resposta.service.RespostaService;
import br.com.tucunare.apoiodigital.security.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The three SDK-facing agent-pipeline endpoints, plus a history lookup. Every method resolves
 * the calling tenant via {@link TenantContext} (populated by the x-api-key filter) and passes it
 * down so userId/idPedido/idResposta are validated against that tenant before anything is read
 * or written — see the individual services for where each id is actually checked.
 */
@RestController
@RequestMapping("/resposta")
public class RespostaController {

    private final RespostaService respostaService;
    private final NecessidadeInformacoesService necessidadeInformacoesService;
    private final RespostaNecessidadeService respostaNecessidadeService;
    private final AcharRespostaService acharRespostaService;
    private final TenantContext tenantContext;

    public RespostaController(
            RespostaService respostaService,
            NecessidadeInformacoesService necessidadeInformacoesService,
            RespostaNecessidadeService respostaNecessidadeService,
            AcharRespostaService acharRespostaService,
            TenantContext tenantContext
    ) {
        this.respostaService = respostaService;
        this.necessidadeInformacoesService = necessidadeInformacoesService;
        this.respostaNecessidadeService = respostaNecessidadeService;
        this.acharRespostaService = acharRespostaService;
        this.tenantContext = tenantContext;
    }

    /** Gatekeeper (Agente 0) + QuestionWriter (Agente 1). */
    @PostMapping("/validar/necessidade-informacoes")
    public ResponseEntity<NecessidadeInformacoesResponseDTO> validarNecessidadeInformacoes(
            @RequestBody NecessidadeInformacoesRequestDTO request
    ) {
        Cliente cliente = tenantContext.getClienteAtual();
        return ResponseEntity.ok(necessidadeInformacoesService.validar(request, cliente));
    }

    /** Answer validator (Agente Y), continuing/closing the clarification loop for a Pedido. */
    @PostMapping("/validar/resposta-necessidade")
    public ResponseEntity<RespostaNecessidadeResponseDTO> validarRespostaNecessidade(
            @RequestBody RespostaNecessidadeRequestDTO request
    ) {
        Cliente cliente = tenantContext.getClienteAtual();
        return ResponseEntity.ok(respostaNecessidadeService.validar(request, cliente));
    }

    /** ElementSelector (Agente X) + ScreenContextDefiner (Agente Z) + TTS. */
    @PostMapping("/achar-resposta")
    public ResponseEntity<AcharRespostaResponseDTO> acharResposta(
            @RequestBody AcharRespostaRequestDTO request
    ) {
        Cliente cliente = tenantContext.getClienteAtual();
        return ResponseEntity.ok(acharRespostaService.acharResposta(request, cliente));
    }

    @GetMapping("/listar/{idPedido}")
    public ResponseEntity<List<Map<String, String>>> listarRespostas(@PathVariable UUID idPedido) {
        Cliente cliente = tenantContext.getClienteAtual();
        return ResponseEntity.ok(respostaService.listarRespostaPorPedido(idPedido, cliente));
    }
}
