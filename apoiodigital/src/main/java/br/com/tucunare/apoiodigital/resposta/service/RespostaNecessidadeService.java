package br.com.tucunare.apoiodigital.resposta.service;

import br.com.tucunare.apoiodigital.cliente.data.Cliente;
import br.com.tucunare.apoiodigital.pedido.data.Pedido;
import br.com.tucunare.apoiodigital.pedido.data.PedidoStatus;
import br.com.tucunare.apoiodigital.pedido.exception.PedidoDoesNotExistException;
import br.com.tucunare.apoiodigital.pedido.repository.PedidoRepository;
import br.com.tucunare.apoiodigital.resposta.data.PerguntaDTO;
import br.com.tucunare.apoiodigital.resposta.data.RespostaNecessidadeRequestDTO;
import br.com.tucunare.apoiodigital.resposta.data.RespostaNecessidadeResponseDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.UserAnswerValidator.AgentResponseDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.UserAnswerValidator.UserAnswerValidatorRequestDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.UserAnswerValidator.UserAnswerValidatorService;
import br.com.tucunare.apoiodigital.usuario.data.Usuario;
import br.com.tucunare.apoiodigital.usuario.service.UsuarioService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Backs {@code POST /resposta/validar/resposta-necessidade}. Reloads the pendency the Gatekeeper
 * raised (persisted on Pedido by {@link NecessidadeInformacoesService}) and asks the answer
 * validator agent (Agente Y, {@link UserAnswerValidatorService}) whether the user's free-text
 * answer resolves it. If not, the agent's own follow-up question becomes the new pending
 * question and the loop continues; if so, the Pedido is marked ready and the SDK is expected to
 * proceed to {@code POST /resposta/achar-resposta} with the resolved prompt.
 */
@Service
public class RespostaNecessidadeService {

    private final UsuarioService usuarioService;
    private final PedidoRepository pedidoRepository;
    private final UserAnswerValidatorService userAnswerValidatorService;

    public RespostaNecessidadeService(
            UsuarioService usuarioService,
            PedidoRepository pedidoRepository,
            UserAnswerValidatorService userAnswerValidatorService
    ) {
        this.usuarioService = usuarioService;
        this.pedidoRepository = pedidoRepository;
        this.userAnswerValidatorService = userAnswerValidatorService;
    }

    public RespostaNecessidadeResponseDTO validar(RespostaNecessidadeRequestDTO request, Cliente cliente) {
        Usuario usuario = usuarioService.buscarPorIdEValidarTenant(request.userId(), cliente);

        Pedido pedido = pedidoRepository.findByIdAndUsuarioId(request.idPedido(), usuario.getId())
                .orElseThrow(PedidoDoesNotExistException::new);

        if (pedido.getStatus() != PedidoStatus.AGUARDANDO_INFORMACAO) {
            // Nothing pending on this Pedido (already resolved, or never needed clarification) —
            // idempotently report "no interruption" rather than erroring.
            return new RespostaNecessidadeResponseDTO(false, null, pedido.getId());
        }

        AgentResponseDTO validador = userAnswerValidatorService.executeTask(
                new UserAnswerValidatorRequestDTO(
                        pedido.getPerguntaPendente(),
                        request.resposta(),
                        pedido.getTipoPendencia(),
                        pedido.getDescricaoDuvida()
                )
        );

        if (validador.satisfaz()) {
            pedido.marcarPronto();
            pedidoRepository.save(pedido);
            return new RespostaNecessidadeResponseDTO(false, null, pedido.getId());
        }

        pedido.marcarAguardandoInformacao(pedido.getTipoPendencia(), pedido.getDescricaoDuvida(), validador.pergunta());
        pedidoRepository.save(pedido);

        return new RespostaNecessidadeResponseDTO(
                true,
                new PerguntaDTO(validador.pergunta(), List.of()),
                pedido.getId()
        );
    }
}
