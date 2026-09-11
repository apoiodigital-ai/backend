package br.com.tucunare.apoiodigital.resposta.service;

import br.com.tucunare.apoiodigital.agent.AndroidComponentDTO;
import br.com.tucunare.apoiodigital.cliente.data.Cliente;
import br.com.tucunare.apoiodigital.componente.service.ComponenteService;
import br.com.tucunare.apoiodigital.pedido.data.Pedido;
import br.com.tucunare.apoiodigital.pedido.repository.PedidoRepository;
import br.com.tucunare.apoiodigital.resposta.data.AcharRespostaRequestDTO;
import br.com.tucunare.apoiodigital.resposta.data.AcharRespostaResponseDTO;
import br.com.tucunare.apoiodigital.resposta.data.Resposta;
import br.com.tucunare.apoiodigital.resposta.repository.RespostaRepository;
import br.com.tucunare.apoiodigital.tts.TtsService;
import br.com.tucunare.apoiodigital.tutorial.agents.ElementSelector.ElementSelectorRequestDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.ElementSelector.ElementSelectorResponseDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.ElementSelector.ElementSelectorService;
import br.com.tucunare.apoiodigital.tutorial.agents.ScreenContextDefiner.ScreenContextDefinerRequestDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.ScreenContextDefiner.ScreenContextDefinerResponseDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.ScreenContextDefiner.ScreenContextDefinerService;
import br.com.tucunare.apoiodigital.usuario.data.Usuario;
import br.com.tucunare.apoiodigital.usuario.service.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Backs {@code POST /resposta/achar-resposta}: runs the ElementSelector (Agente X) to pick which
 * captured element to highlight, then the ScreenContextDefiner (Agente Z) to write the
 * user-facing instruction and (via {@link TtsService}) synthesize it to speech. Persists the
 * Pedido/Resposta/Componente audit trail for this step — Resposta.raciocinio is exactly the
 * ElementSelector's own explanation for its choice, which is what makes this step traceable
 * rather than an opaque model call.
 */
@Service
public class AcharRespostaService {

    private final UsuarioService usuarioService;
    private final PedidoRepository pedidoRepository;
    private final RespostaRepository respostaRepository;
    private final ComponenteService componenteService;
    private final ElementSelectorService elementSelectorService;
    private final ScreenContextDefinerService screenContextDefinerService;
    private final TtsService ttsService;

    public AcharRespostaService(
            UsuarioService usuarioService,
            PedidoRepository pedidoRepository,
            RespostaRepository respostaRepository,
            ComponenteService componenteService,
            ElementSelectorService elementSelectorService,
            ScreenContextDefinerService screenContextDefinerService,
            TtsService ttsService
    ) {
        this.usuarioService = usuarioService;
        this.pedidoRepository = pedidoRepository;
        this.respostaRepository = respostaRepository;
        this.componenteService = componenteService;
        this.elementSelectorService = elementSelectorService;
        this.screenContextDefinerService = screenContextDefinerService;
        this.ttsService = ttsService;
    }

    public AcharRespostaResponseDTO acharResposta(AcharRespostaRequestDTO request, Cliente cliente) {
        Usuario usuario = usuarioService.buscarPorIdEValidarTenant(request.userId(), cliente);

        Pedido pedido = pedidoRepository.save(new Pedido(usuario, request.prompt()));

        ElementSelectorResponseDTO selecao = elementSelectorService.executeTask(
                new ElementSelectorRequestDTO(request.prompt(), request.elementos())
        );

        AndroidComponentDTO elementoEscolhido = request.elementos().stream()
                .filter(e -> selecao.viewID().equals(e.viewID()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "ElementSelector retornou um viewID que não está na lista de elementos enviada"
                ));

        ScreenContextDefinerResponseDTO textoGuia = screenContextDefinerService.executeTask(
                new ScreenContextDefinerRequestDTO(request.prompt(), selecao.raciocinio(), elementoEscolhido)
        );

        Resposta resposta = respostaRepository.save(
                new Resposta(pedido, textoGuia.mensagem_escrita(), selecao.raciocinio())
        );

        componenteService.salvar(resposta, request.elementos());

        String mensagemVozUrl = ttsService.synthesize(textoGuia.mensagem_voz())
                .map(this::buildAudioUrl)
                .orElse(null);

        return new AcharRespostaResponseDTO(
                String.valueOf(selecao.viewID()),
                textoGuia.mensagem_escrita(),
                mensagemVozUrl,
                selecao.precisao()
        );
    }

    private String buildAudioUrl(String filename) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/audio/{filename}")
                .buildAndExpand(filename)
                .toUriString();
    }
}
