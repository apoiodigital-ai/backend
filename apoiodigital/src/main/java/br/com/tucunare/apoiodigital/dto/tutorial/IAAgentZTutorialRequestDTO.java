package br.com.tucunare.apoiodigital.dto.tutorial;

import br.com.tucunare.apoiodigital.dto.AndroidComponentDTO;

public record IAAgentZTutorialRequestDTO(String contexto, String raciocinio, AndroidComponentDTO elemento) {
}
