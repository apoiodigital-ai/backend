package br.com.tucunare.apoiodigital.controller;

import br.com.tucunare.apoiodigital.model.Componente;
import br.com.tucunare.apoiodigital.service.ComponenteService;
import br.com.tucunare.apoiodigital.dto.ComparacaoRequestDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/componentes")
public class ComponenteController {

    private final ComponenteService componenteService;

    public ComponenteController(ComponenteService componenteService) {
        this.componenteService = componenteService;
    }

    @PostMapping("/salvar-todos")
    public ResponseEntity<Void> salvarTodos(@RequestBody List<Componente> componentes) {
        componenteService.salvar(componentes);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comparar")
    public ResponseEntity<Boolean> comparar(
            @RequestParam UUID idResposta,
            @RequestBody ComparacaoRequestDTO request
    ) {
        boolean resultado = componenteService.comparar(
                request.componentesCriptografados(),
                request.key(),
                idResposta
        );

        return ResponseEntity.ok(resultado);
    }
}
