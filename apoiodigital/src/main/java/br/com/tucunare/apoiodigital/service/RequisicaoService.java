package br.com.tucunare.apoiodigital.service;

import br.com.tucunare.apoiodigital.dto.*;
import br.com.tucunare.apoiodigital.exception.AppSuportadoNotFoundException;
import br.com.tucunare.apoiodigital.model.AppSuportado;
import br.com.tucunare.apoiodigital.model.Requisicao;
import br.com.tucunare.apoiodigital.model.Usuario;
import br.com.tucunare.apoiodigital.repository.AppSuportadoRepository;
import br.com.tucunare.apoiodigital.repository.AtalhoRepository;
import br.com.tucunare.apoiodigital.repository.RequisicaoRepository;
import br.com.tucunare.apoiodigital.repository.UsuarioRepository;
import br.com.tucunare.apoiodigital.exception.UsuarioDoesNotExistException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RequisicaoService {

    private final RequisicaoRepository requisicaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final GeminiService geminiService;
    private final AppSuportadoRepository appSuportadoRepository;
    private final AtalhoRepository atalhoRepository;
    private final UsuarioService usuarioService;


    public RequisicaoService(
            RequisicaoRepository requisicaoRepository,
            UsuarioRepository usuarioRepository,
            GeminiService geminiService,
            AppSuportadoRepository appSuportadoRepository, AtalhoRepository atalhoRepository,
            UsuarioService usuarioService
    ) {
        this.requisicaoRepository = requisicaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.geminiService = geminiService;
        this.appSuportadoRepository = appSuportadoRepository;
        this.atalhoRepository = atalhoRepository;
        this.usuarioService = usuarioService;
    }
    private Requisicao criarRequisicao(Usuario u, String prompt, AppSuportado app){
        return new Requisicao(u,prompt,app);
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
                criarRequisicao(usuario,"Quero pedir comida", ifood),
                criarRequisicao(usuario, "Quero mandar uma mensagem", whatsapp),
                criarRequisicao(usuario, "Quero pedir um motorista", uber)
        );

        return requisicaoRepository.saveAll(requisicoes);
    }

    public SaveRequisicaoResponseDTO salvarRequisicao(RequisicaoInputDTO dto) {

        Usuario usuario = usuarioRepository.findById(dto.id_usuario())
                .orElseThrow(UsuarioDoesNotExistException::new);

        List<AppSuportadoToGeminiDTO> apps_banco = appSuportadoRepository.findAllApps();
        RequestInputToGeminiDTO geminiDto = new RequestInputToGeminiDTO(dto.prompt(), apps_banco, dto.lista_apps_instalados());
        FindBestAppResponseDTO bestApp = geminiService.acharMelhorApp(geminiDto);

        Optional<Requisicao> p = compararRequisicoes(dto.prompt(), usuario);
        if(p.isPresent() && Objects.equals(p.get().getAppSuportado().getId(), bestApp.id_app_instalado())){ // achou req semelhante
            Requisicao requisicao = new Requisicao (usuario, dto.prompt(), p.get().getAppSuportado());
            Requisicao requisicaoDb = requisicaoRepository.save(requisicao);

            return new SaveRequisicaoResponseDTO(requisicaoDb, bestApp.contexto(), bestApp.id_app_instalado(), p.get().getId());

        }else{ // IA precisa definir titulo de atalho

            Optional<AppSuportado> appSuportado = appSuportadoRepository.findById(bestApp.id_app_banco());

            if(appSuportado.isPresent()){
                Requisicao requisicao = new Requisicao(usuario, dto.prompt(), appSuportado.get());
                Requisicao requisicaoDb = requisicaoRepository.save(requisicao);

                return new SaveRequisicaoResponseDTO(requisicaoDb, bestApp.contexto(), bestApp.id_app_instalado(), null);
            }else {
                throw new RuntimeException("Gemini Falhou!!!!!! AppSuportado não existe!");
            }


        }

    }

    public List<Requisicao> carregarRequisicaoPeloAccessTokenUsuario(String token) {
        Usuario usuario = usuarioService.getUsuarioByAccessToken(token);
        return requisicaoRepository.findByUsuario(usuario);
    }

    public Optional<Requisicao> compararRequisicoes(
            String prompt, Usuario usuario
    ) {
        return requisicaoRepository.findFirstByPromptAndUsuarioOrderByCriacaoDesc(prompt, usuario);
    }










//
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        try {
//            String json = objectMapper.writeValueAsString(dto);
//
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create("http://localhost:5000/requisicao/comparar"))
//                    .header("Content-Type", "application/json")
//                    .POST(HttpRequest.BodyPublishers.ofString(json))
//                    .build();
//
//            HttpClient client = HttpClient.newHttpClient();
//            HttpResponse<String> response =
//                    client.send(request, HttpResponse.BodyHandlers.ofString());
//
//            return objectMapper.readValue(
//                    response.body(),
//                    RequisicaoComparasionResponseDTO.class
//            );
//
//        } catch (IOException | InterruptedException e) {
//            throw new IllegalStateException("Erro ao comparar requisições", e);
}