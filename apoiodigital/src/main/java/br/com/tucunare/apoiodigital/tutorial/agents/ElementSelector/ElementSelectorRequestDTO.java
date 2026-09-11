package br.com.tucunare.apoiodigital.tutorial.agents.ElementSelector;

import br.com.tucunare.apoiodigital.agent.AndroidComponentDTO;

import java.util.List;

public record ElementSelectorRequestDTO(String prompt, List<AndroidComponentDTO> elementos) {
}
