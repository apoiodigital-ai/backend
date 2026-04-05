package br.com.tucunare.apoiodigital.controller;

import br.com.tucunare.apoiodigital.dto.ErroResponseDTO;
import br.com.tucunare.apoiodigital.dto.RequisicaoInputDTO;
import br.com.tucunare.apoiodigital.dto.RequisicaoResponseDTO;
import br.com.tucunare.apoiodigital.dto.SaveRequisicaoResponseDTO;
import br.com.tucunare.apoiodigital.model.Requisicao;
import br.com.tucunare.apoiodigital.service.AtalhoService;
import br.com.tucunare.apoiodigital.service.RequisicaoService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/requisicao")
public class RequisicaoController {

    private final RequisicaoService requisicaoService;
    private final AtalhoService atalhoService;

    public RequisicaoController(RequisicaoService requisicaoService, AtalhoService atalhoService) {
        this.requisicaoService = requisicaoService;
        this.atalhoService = atalhoService;
    }

    @PostMapping("/enviar")
    public ResponseEntity<SaveRequisicaoResponseDTO> enviarRequisicao(
            @RequestBody RequisicaoInputDTO dto
    ) {
        SaveRequisicaoResponseDTO requisicaoResponse = requisicaoService.salvarRequisicao(dto);
        atalhoService.criarAtalho(requisicaoResponse.requisicao(), requisicaoResponse.id_req_match());
        return ResponseEntity.status(HttpStatus.CREATED).body(requisicaoResponse);
    }

    @GetMapping("/carregar")
    public ResponseEntity<?> carregarListaHistoricoByUserToken(
            @RequestParam String token
    ) {
        try {
            List<Requisicao> requisicoes =
                    requisicaoService.carregarRequisicaoPeloAccessTokenUsuario(token);

            return ResponseEntity.ok(
                    new RequisicaoResponseDTO(
                            Instant.now().toString(),
                            requisicoes
                    )
            );

        } catch (ExpiredJwtException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErroResponseDTO(
                            "ACCESS_TOKEN_EXPIRADO",
                            "Access token expirado"
                    ));

        } catch (JwtException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErroResponseDTO(
                            "ACCESS_TOKEN_INVALIDO",
                            "Access token inválido"
                    ));
        }
    }
}
