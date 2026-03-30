package br.com.tucunare.apoiodigital.service;

import br.com.tucunare.apoiodigital.dto.*;
import br.com.tucunare.apoiodigital.model.Componente;
import br.com.tucunare.apoiodigital.model.Requisicao;
import br.com.tucunare.apoiodigital.model.Resposta;
import br.com.tucunare.apoiodigital.repository.ComponenteRepository;
import br.com.tucunare.apoiodigital.repository.RequisicaoRepository;
import br.com.tucunare.apoiodigital.repository.RespostaRepository;
import br.com.tucunare.apoiodigital.exception.RequisicaoDoesNotExistException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class IAService {

    private static final String IA_BASE_URL = "http://localhost:5000/api/assist";

    private final RespostaRepository respostaRepository;
    private final RequisicaoRepository requisicaoRepository;
    private final CryptService cryptService;
    private final ComponenteRepository componenteRepository;
    private final ObjectMapper objectMapper;
    @Value("$(ia.base-url")
    private String iaBaseUrl;
    public IAService(
            RespostaRepository respostaRepository,
            RequisicaoRepository requisicaoRepository,
            CryptService cryptService,
            ComponenteRepository componenteRepository,
            ObjectMapper objectMapper
    ) {
        this.respostaRepository = respostaRepository;
        this.requisicaoRepository = requisicaoRepository;
        this.cryptService = cryptService;
        this.componenteRepository = componenteRepository;
        this.objectMapper = objectMapper;
    }

    public Resposta salvarResposta(String mensagem, Requisicao requisicao) {
        Resposta resposta = new Resposta(requisicao, mensagem);
        return respostaRepository.save(resposta);
    }

    public void salvarComponente(
            List<AndroidComponentDTO> componentesDTO,
            Resposta resposta,
            Integer viewId
    ) {
        AndroidComponentDTO componenteDTO = componentesDTO.stream()
                .filter(dto -> dto.viewID().equals(viewId))
                .findFirst()
                .orElseThrow();

        AndroidComponentWithoutViewIdDTO dto =
                new AndroidComponentWithoutViewIdDTO(
                        componenteDTO.className(),
                        componenteDTO.adicionalInfo()
                );

        try {
            String dtoJson = objectMapper.writeValueAsString(dto);
            String chavePublica = cryptService.getPublicKey();
            String conteudoCriptografado =
                    cryptService.encripty(dtoJson, chavePublica);

            Componente componente =
                    new Componente(conteudoCriptografado, chavePublica, resposta);

            componenteRepository.save(componente);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public IARespostaCryptDTO acharMelhorResposta(
            IARespostaRequestDTO iaRequestDTO
    ) {
        Requisicao requisicao = requisicaoRepository
                .findById(iaRequestDTO.id_requisicao())
                .orElseThrow(RequisicaoDoesNotExistException::new);

        try {
            String conteudoDescriptografado =
                    cryptService.descrypt(
                            iaRequestDTO.textCrypted(),
                            iaRequestDTO.key()
                    );

            IARespostaRequestDescryptDTO requestDTO =
                    objectMapper.readValue(
                            conteudoDescriptografado,
                            IARespostaRequestDescryptDTO.class
                    );

            String respostaIA =
                    executarChamadaIA(conteudoDescriptografado, 2);

            IARespostaRawDTO rawResponse =
                    objectMapper.readValue(respostaIA, IARespostaRawDTO.class);

            Resposta respostaPersistida =
                    salvarResposta(rawResponse.mensagem(), requisicao);

            salvarComponente(
                    requestDTO.elementos(),
                    respostaPersistida,
                    rawResponse.viewID()
            );

            String novaChave = cryptService.getPublicKey();
            String respostaCriptografada =
                    cryptService.encripty(respostaIA, novaChave);

            return new IARespostaCryptDTO(novaChave, respostaCriptografada);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public FindBestAppResponseDTO acharMelhorApp(
            RequisicaoInputDTO requisicaoInputDTO
    ) {
        try {
            String body = objectMapper.writeValueAsString(requisicaoInputDTO);
            String respostaIA = executarChamadaIA(body, 1);

            return objectMapper.readValue(
                    respostaIA,
                    FindBestAppResponseDTO.class
            );

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String executarChamadaIA(String body, int requestId)
            throws IOException, InterruptedException {

        String uri = iaBaseUrl + "?request_id=" + requestId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}
