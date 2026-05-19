package br.com.tucunare.apoiodigital.dto.tutorial;

import br.com.tucunare.apoiodigital.dto.AndroidComponentDTO;

import java.util.List;

public record IAAgentXTutorialRequestDTO(String contexto, String prompt, String pergunta_especificacao, String resposta_especificacao, List<AndroidComponentDTO> elementos) {
}
