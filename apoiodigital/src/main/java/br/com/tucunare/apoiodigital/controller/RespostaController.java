package br.com.tucunare.apoiodigital.controller;

import br.com.tucunare.apoiodigital.dto.IARespostaCryptDTO;
import br.com.tucunare.apoiodigital.dto.IARespostaRequestDTO;
import br.com.tucunare.apoiodigital.service.IAService;
import br.com.tucunare.apoiodigital.service.RespostaService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/resposta")
public class RespostaController {

    private final RespostaService respostaService;
    private final IAService iaService;

    public RespostaController(
            RespostaService respostaService,
            IAService iaService
    ) {
        this.respostaService = respostaService;
        this.iaService = iaService;
    }

    @PostMapping("/exigir")
    public ResponseEntity<IARespostaCryptDTO> exigirRespostaDaIA(
            @RequestBody IARespostaRequestDTO request
    ) {
        IARespostaCryptDTO resposta = iaService.acharMelhorResposta(request);
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/listar/{idReq}")
    public ResponseEntity<List<Map<String, String>>> carregarRespostas(
            @PathVariable UUID idReq
    ) {
        return ResponseEntity.ok(
                respostaService.listarRespostaPorRequisicao(idReq)
        );
    }
}
