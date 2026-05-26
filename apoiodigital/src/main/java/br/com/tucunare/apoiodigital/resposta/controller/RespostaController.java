package br.com.tucunare.apoiodigital.resposta.controller;

import br.com.tucunare.apoiodigital.resposta.service.RespostaService;
import br.com.tucunare.apoiodigital.tutorial.*;

import br.com.tucunare.apoiodigital.tutorial.InformationNeeds.ChecksInformationNeedsRequestDTO;
import br.com.tucunare.apoiodigital.tutorial.InformationNeeds.ChecksInformationNeedsResponseDTO;
import br.com.tucunare.apoiodigital.tutorial.InformationNeeds.InformationNeedsService;
import br.com.tucunare.apoiodigital.tutorial.InformationNeeds.InformationResponseService;
import br.com.tucunare.apoiodigital.tutorial.agents.ElementSelector.ElementSelectorRequestDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.UserAnswerValidator.UserAnswerValidatorRequestDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.UserAnswerValidator.UserAnswerValidatorResponseDTO;
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
    private final InformationNeedsService informationNeedsService;
    private final InformationResponseService informationResponseService;


    public RespostaController(
            RespostaService respostaService,
            FindBestAnswerService findBestAnswerService, InformationNeedsService informationNeedsService, InformationResponseService informationResponseService
    ) {
        this.respostaService = respostaService;
        this.findBestAnswerService = findBestAnswerService;
        this.informationNeedsService = informationNeedsService;
        this.informationResponseService = informationResponseService;
    }


    @PostMapping("/achar-resposta")
    public ResponseEntity<FindBestAnswerResponseDTO> acharMelhorResposta(
            @RequestBody ElementSelectorRequestDTO request
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
        ChecksInformationNeedsResponseDTO response = informationNeedsService.checksInformationNeeds(requestDTO);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/validar/resposta-necessidade")
    public ResponseEntity<UserAnswerValidatorResponseDTO> checksQuestionReturns(
            @RequestBody UserAnswerValidatorRequestDTO request
    ) {
        UserAnswerValidatorResponseDTO response = informationResponseService.checksQuestionReturns(request);
        if (!response.satisfaz()) return ResponseEntity.badRequest().body(response);
        return ResponseEntity.ok(response);
    }

}