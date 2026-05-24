package br.com.tucunare.apoiodigital.tutorial.agents.PendingValidator;

import br.com.tucunare.apoiodigital.dto.tutorial.AndroidComponentDTO;

import java.util.List;

public record PendingValidatorRequestDTO(String prompt, String contexto, List<AndroidComponentDTO> elementos) {
}
