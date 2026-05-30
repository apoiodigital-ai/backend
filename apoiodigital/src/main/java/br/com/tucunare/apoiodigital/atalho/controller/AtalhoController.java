package br.com.tucunare.apoiodigital.atalho.controller;

import br.com.tucunare.apoiodigital.atalho.data.Atalho;
import br.com.tucunare.apoiodigital.atalho.service.AtalhoService;
import br.com.tucunare.apoiodigital.requisicao.data.Requisicao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/atalhos")
public class AtalhoController {

    @Autowired
    private AtalhoService atalhoService;

    @PostMapping("/iniciar")
    public ResponseEntity<Requisicao> iniciarAtalho(@RequestParam UUID id_atalho) {
        return ResponseEntity.ok(atalhoService.iniciarAtalho(id_atalho));
    }

    @GetMapping("/carregar")
    public ResponseEntity<List<Atalho>> carregarAtalhos(@RequestParam UUID id_usuario) {
        return ResponseEntity.ok(atalhoService.carregarAtalhos(id_usuario));
    }

}
