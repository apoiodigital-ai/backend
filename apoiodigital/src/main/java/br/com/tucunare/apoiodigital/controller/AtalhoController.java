package br.com.tucunare.apoiodigital.controller;

import br.com.tucunare.apoiodigital.model.Atalho;
import br.com.tucunare.apoiodigital.model.Requisicao;
import br.com.tucunare.apoiodigital.service.AtalhoService;
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
