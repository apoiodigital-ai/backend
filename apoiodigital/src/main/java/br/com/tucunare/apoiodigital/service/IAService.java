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
    private final ComponenteRepository componenteRepository;
    private final ObjectMapper objectMapper;
    @Value("$(ia.base-url")
    private String iaBaseUrl;
    public IAService(
            RespostaRepository respostaRepository,
            RequisicaoRepository requisicaoRepository,
            ComponenteRepository componenteRepository,
            ObjectMapper objectMapper
    ) {
        this.respostaRepository = respostaRepository;
        this.requisicaoRepository = requisicaoRepository;
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
                        componenteDTO.additionalInfo()
                );

        try {
            String dtoJson = objectMapper.writeValueAsString(dto);
            Componente c = new Componente(dtoJson, resposta);
            componenteRepository.save(c);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public IARespostaRawDTO acharMelhorResposta(
            IARespostaRequestDescryptDTO requestDTO
    ) {
        Requisicao requisicao = requisicaoRepository
                .findById(requestDTO.id_requisicao())
                .orElseThrow(RequisicaoDoesNotExistException::new);

        try {
            String body = objectMapper.writeValueAsString(requestDTO);
            String respostaIA =
                    executarChamadaIA(body, 2);

            IARespostaRawDTO rawResponse =
                    objectMapper.readValue(respostaIA, IARespostaRawDTO.class);

            Resposta respostaPersistida =
                    salvarResposta(rawResponse.mensagem(), requisicao);

            salvarComponente(
                    requestDTO.elementos(),
                    respostaPersistida,
                    rawResponse.viewID()
            );

            return rawResponse;

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
