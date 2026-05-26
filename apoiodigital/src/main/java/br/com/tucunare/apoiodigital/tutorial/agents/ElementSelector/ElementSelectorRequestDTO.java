package br.com.tucunare.apoiodigital.tutorial.agents.ElementSelector;

import br.com.tucunare.apoiodigital.tutorial.AndroidComponentDTO;

import java.util.List;

public record ElementSelectorRequestDTO(String contexto, String prompt, String pergunta_especificacao, String resposta_especificacao, List<AndroidComponentDTO> elementos) {
}
