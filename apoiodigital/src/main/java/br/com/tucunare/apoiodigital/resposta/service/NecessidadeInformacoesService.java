package br.com.tucunare.apoiodigital.resposta.service;

import br.com.tucunare.apoiodigital.cliente.data.Cliente;
import br.com.tucunare.apoiodigital.pedido.data.Pedido;
import br.com.tucunare.apoiodigital.pedido.repository.PedidoRepository;
import br.com.tucunare.apoiodigital.resposta.data.NecessidadeInformacoesRequestDTO;
import br.com.tucunare.apoiodigital.resposta.data.NecessidadeInformacoesResponseDTO;
import br.com.tucunare.apoiodigital.resposta.data.PerguntaDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.PendingValidator.PendingValidatorRequestDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.PendingValidator.PendingValidatorResponseDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.PendingValidator.PendingValidatorService;
import br.com.tucunare.apoiodigital.tutorial.agents.QuestionWriter.QuestionWriterRequestDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.QuestionWriter.QuestionWriterResponseDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.QuestionWriter.QuestionWriterService;
import br.com.tucunare.apoiodigital.usuario.data.Usuario;
import br.com.tucunare.apoiodigital.usuario.service.UsuarioService;
import org.springframework.stereotype.Service;

/**
 * Backs {@code POST /resposta/validar/necessidade-informacoes}: the Gatekeeper entry point
 * (Agente 0, {@link PendingValidatorService}) decides whether the request is unambiguous enough
 * to act on directly; when it is not, the QuestionWriter (Agente 1,
 * {@link QuestionWriterService}) drafts the clarifying question. Persists the Pedido this
 * request represents so the (possible) clarification loop in
 * {@code POST /resposta/validar/resposta-necessidade} has somewhere to reload its state from.
 */
@Service
public class NecessidadeInformacoesService {

    private final UsuarioService usuarioService;
    private final PedidoRepository pedidoRepository;
    private final PendingValidatorService pendingValidatorService;
    private final QuestionWriterService questionWriterService;

    public NecessidadeInformacoesService(
            UsuarioService usuarioService,
            PedidoRepository pedidoRepository,
            PendingValidatorService pendingValidatorService,
            QuestionWriterService questionWriterService
    ) {
        this.usuarioService = usuarioService;
        this.pedidoRepository = pedidoRepository;
        this.pendingValidatorService = pendingValidatorService;
        this.questionWriterService = questionWriterService;
    }

    public NecessidadeInformacoesResponseDTO validar(NecessidadeInformacoesRequestDTO request, Cliente cliente) {
        Usuario usuario = usuarioService.buscarPorIdEValidarTenant(request.userId(), cliente);

        Pedido pedido = pedidoRepository.save(new Pedido(usuario, request.prompt()));

        PendingValidatorResponseDTO gatekeeper = pendingValidatorService.executeTask(
                new PendingValidatorRequestDTO(request.prompt(), request.elementos())
        );

        if (!gatekeeper.interromper()) {
            pedido.marcarPronto();
            pedidoRepository.save(pedido);
            return new NecessidadeInformacoesResponseDTO(false, null, pedido.getId());
        }

        QuestionWriterResponseDTO questionWriter = questionWriterService.executeTask(
                new QuestionWriterRequestDTO(gatekeeper.tipo_pendencia(), gatekeeper.descricao_duvida(), request.elementos())
        );

        pedido.marcarAguardandoInformacao(gatekeeper.tipo_pendencia(), gatekeeper.descricao_duvida(), questionWriter.pergunta());
        pedidoRepository.save(pedido);

        return new NecessidadeInformacoesResponseDTO(
                true,
                new PerguntaDTO(questionWriter.pergunta(), questionWriter.opcoes()),
                pedido.getId()
        );
    }
}
