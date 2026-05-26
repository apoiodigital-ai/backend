package br.com.tucunare.apoiodigital.requisicao.controller;

import br.com.tucunare.apoiodigital.dto.requisicao.ErroResponseDTO;
import br.com.tucunare.apoiodigital.dto.requisicao.RequisicaoInputDTO;
import br.com.tucunare.apoiodigital.dto.requisicao.RequisicaoResponseDTO;
import br.com.tucunare.apoiodigital.model.Requisicao;
import br.com.tucunare.apoiodigital.requisicao.RequisicaoService;
import br.com.tucunare.apoiodigital.service.impl.AtalhoService;
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
    public ResponseEntity<Requisicao> enviarRequisicao(
            @RequestBody RequisicaoInputDTO dto
    ) {
        try{
            Requisicao requisicaoResponse = requisicaoService.salvarRequisicao(dto);
//            atalhoService.criarAtalho(requisicaoResponse.requisicao(), requisicaoResponse.id_req_match()); TODO: ANALISAR CASO PARA SER ASSINCRONO
            return ResponseEntity.status(HttpStatus.CREATED).body(requisicaoResponse);
       }catch(RuntimeException e){
            throw new RuntimeException(e.getMessage());
        }

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
