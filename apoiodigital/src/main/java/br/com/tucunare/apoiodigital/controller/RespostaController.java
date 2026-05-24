package br.com.tucunare.apoiodigital.controller;

import br.com.tucunare.apoiodigital.dto.tutorial.*;
import br.com.tucunare.apoiodigital.service.impl.FindBestAnswerService;
import br.com.tucunare.apoiodigital.service.impl.RespostaService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/resposta")
public class RespostaController {

    private final RespostaService respostaService;
    private final FindBestAnswerService findBestAnswerService;


    public RespostaController(
            RespostaService respostaService,
            FindBestAnswerService findBestAnswerService
    ) {
        this.respostaService = respostaService;
        this.findBestAnswerService = findBestAnswerService;
    }


    @PostMapping("/achar-resposta")
    public ResponseEntity<FindBestAnswerResponseDTO> acharMelhorResposta(
            @RequestBody IAAgentXTutorialRequestDTO request
    ) {
        FindBestAnswerResponseDTO response = findBestAnswerService.findBestAnswer(request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/listar/{idReq}")
    public ResponseEntity<List<Map<String, String>>> carregarRespostas(
            @PathVariable UUID idReq
    ) {
        return ResponseEntity.ok(
                respostaService.listarRespostaPorRequisicao(idReq)
        );
    }


    @PostMapping("/validar/necessidade-informacoes")
    public ResponseEntity<ChecksInformationNeedsResponseDTO> checksInformationNeeds(
            @RequestBody ChecksInformationNeedsRequestDTO requestDTO
    ) {
        ChecksInformationNeedsResponseDTO response = findBestAnswerService.checksInformationNeeds(requestDTO);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/validar/resposta-necessidade")
    public ResponseEntity<IAAgentYTutorialResponseDTO> checksQuestionReturns(
            @RequestBody IAAgentYTutorialRequestDTO request
    ) {
        IAAgentYTutorialResponseDTO response = findBestAnswerService.checksQuestionReturns(request);
        if (!response.satisfaz()) return ResponseEntity.badRequest().body(response);
        return ResponseEntity.ok(response);
    }

}