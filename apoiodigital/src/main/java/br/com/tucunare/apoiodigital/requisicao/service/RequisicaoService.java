package br.com.tucunare.apoiodigital.requisicao.service;

import br.com.tucunare.apoiodigital.requisicao.data.RequisicaoInputDTO;
import br.com.tucunare.apoiodigital.findbestapp.FirstTry.FindBestAppResponseDTO;
import br.com.tucunare.apoiodigital.findbestapp.FirstTry.FindBestAppService;
import br.com.tucunare.apoiodigital.appsuportado.data.AppSuportado;
import br.com.tucunare.apoiodigital.requisicao.data.Requisicao;
import br.com.tucunare.apoiodigital.requisicao.data.SaveRequisicaoResponseDTO;
import br.com.tucunare.apoiodigital.usuario.data.Usuario;
import br.com.tucunare.apoiodigital.appsuportado.repository.AppSuportadoRepository;
import br.com.tucunare.apoiodigital.usuario.repository.UsuarioRepository;
import br.com.tucunare.apoiodigital.usuario.exception.UsuarioDoesNotExistException;

import br.com.tucunare.apoiodigital.requisicao.repository.RequisicaoRepository;
import br.com.tucunare.apoiodigital.usuario.service.UsuarioService;
import br.com.tucunare.apoiodigital.usuario.service.UsuarioTokenService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RequisicaoService {

    private final RequisicaoRepository requisicaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AppSuportadoRepository appSuportadoRepository;
    private final UsuarioService usuarioService;
    private final FindBestAppService findBestAppService;
    private final CompareRequisicaoService compareRequisicaoService;
    private final UsuarioTokenService usuarioTokenService;

    public RequisicaoService(
            RequisicaoRepository requisicaoRepository,
            UsuarioRepository usuarioRepository,
            AppSuportadoRepository appSuportadoRepository,
            UsuarioService usuarioService, FindBestAppService findBestAppService, CompareRequisicaoService compareRequisicaoService, UsuarioTokenService usuarioTokenService
    ) {
        this.requisicaoRepository = requisicaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.appSuportadoRepository = appSuportadoRepository;
        this.usuarioService = usuarioService;
        this.findBestAppService = findBestAppService;
        this.compareRequisicaoService = compareRequisicaoService;
        this.usuarioTokenService = usuarioTokenService;
    }

    public SaveRequisicaoResponseDTO salvarRequisicao(RequisicaoInputDTO dto){
        Usuario usuario = usuarioRepository.findById(dto.id_usuario())
                .orElseThrow(UsuarioDoesNotExistException::new);


        //RequestInputToGeminiDTO geminiDto = new RequestInputToGeminiDTO(dto.prompt(), apps_banco, dto.lista_apps_instalados());

        Optional<Requisicao> p = compareRequisicaoService.compararRequisicoes(dto.prompt(), usuario);

        if(p.isPresent()){ // achou req semelhante
            Requisicao req = new Requisicao(usuario, dto.prompt(), p.get().getAppSuportado());
            Requisicao reqPersistida = requisicaoRepository.save(req);

            System.out.println("PROMPT: " + req.getPrompt());
            System.out.println("APP: " + req.getAppSuportado());

            return new SaveRequisicaoResponseDTO(reqPersistida, p.get());
        }

        List<AppSuportado> apps_banco = appSuportadoRepository.findAll();

        // nao achou req semelhante

        FindBestAppResponseDTO bestAppResponse = findBestAppService.findBestApp(dto.prompt(), apps_banco);

        Optional<AppSuportado> appSuportado = appSuportadoRepository.findById(bestAppResponse.id_app_banco());

        if(appSuportado.isPresent()){
            Requisicao req = new Requisicao(usuario, dto.prompt(), appSuportado.get());
            Requisicao reqPersistida = requisicaoRepository.save(req);

            System.out.println("PROMPT: " + req.getPrompt());
            System.out.println("APP: " + req.getAppSuportado());

            return new SaveRequisicaoResponseDTO(reqPersistida, null);
        }else {
            throw new RuntimeException("Gemini Falhou!!!!!! AppSuportado não existe!");
        }

    }

    public List<Requisicao> carregarRequisicaoPeloAccessTokenUsuario(String token) {
        Usuario usuario = usuarioTokenService.getUsuarioByAccessToken(token);
        return requisicaoRepository.findByUsuario(usuario);
    }

}