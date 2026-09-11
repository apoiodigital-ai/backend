package br.com.tucunare.apoiodigital.tutorial.agents.ScreenContextDefiner;

import br.com.tucunare.apoiodigital.agent.AndroidComponentDTO;

public record ScreenContextDefinerRequestDTO(String prompt, String raciocinio, AndroidComponentDTO elemento) {
}
