package br.com.tucunare.apoiodigital.tutorial.agents.PendingValidator;

import br.com.tucunare.apoiodigital.agent.AndroidComponentDTO;

import java.util.List;

public record PendingValidatorRequestDTO(String prompt, List<AndroidComponentDTO> elementos) {
}
