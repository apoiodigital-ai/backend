package br.com.tucunare.apoiodigital.requisicao;

import br.com.tucunare.apoiodigital.exception.AppSuportadoNotFoundException;
import br.com.tucunare.apoiodigital.exception.UsuarioDoesNotExistException;
import br.com.tucunare.apoiodigital.model.AppSuportado;
import br.com.tucunare.apoiodigital.model.Requisicao;
import br.com.tucunare.apoiodigital.model.Usuario;
import br.com.tucunare.apoiodigital.repository.AppSuportadoRepository;
import br.com.tucunare.apoiodigital.repository.UsuarioRepository;
import br.com.tucunare.apoiodigital.requisicao.repository.RequisicaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class InitialRequisicaoService {

    private final UsuarioRepository usuarioRepository;
    private final AppSuportadoRepository appSuportadoRepository;
    private final RequisicaoRepository requisicaoRepository;

    public InitialRequisicaoService(UsuarioRepository usuarioRepository, AppSuportadoRepository appSuportadoRepository, RequisicaoRepository requisicaoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.appSuportadoRepository = appSuportadoRepository;
        this.requisicaoRepository = requisicaoRepository;
    }

    public List<Requisicao> salvarRequisicoesIniciais(UUID usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(UsuarioDoesNotExistException::new);

        AppSuportado ifood = appSuportadoRepository.findById(12L)
                .orElseThrow(() -> new AppSuportadoNotFoundException("App iFood não encontrado"));

        AppSuportado whatsapp = appSuportadoRepository.findById(1L)
                .orElseThrow(() -> new AppSuportadoNotFoundException("App WhatsApp não encontrado"));

        AppSuportado uber = appSuportadoRepository.findById(15L)
                .orElseThrow(() -> new AppSuportadoNotFoundException("App Uber não encontrado"));

        List<Requisicao> requisicoes = List.of(
                new Requisicao (usuario,"Quero pedir comida", ifood),
                new Requisicao (usuario, "Quero mandar uma mensagem", whatsapp),
                new Requisicao (usuario, "Quero pedir um motorista", uber)
        );

        return requisicaoRepository.saveAll(requisicoes);
    }

}
