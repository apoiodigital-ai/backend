package br.com.tucunare.apoiodigital.tutorial.agents.ScreenContextDefiner;

import br.com.tucunare.apoiodigital.tutorial.AndroidComponentDTO;

public record ScreenContextDefinerRequestDTO(String contexto, String raciocinio, AndroidComponentDTO elemento) {
}
