package br.com.tucunare.apoiodigital.dto;

import br.com.tucunare.apoiodigital.enums.TiposDependecia;

import java.util.List;

public record IAAgentXTutorialRequestDTO(String contexto, String prompt, String pergunta_especificacao, String resposta_especificacao, List<AndroidComponentDTO> elementos) {
}
