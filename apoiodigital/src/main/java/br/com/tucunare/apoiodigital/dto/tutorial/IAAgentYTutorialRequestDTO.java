package br.com.tucunare.apoiodigital.dto.tutorial;

import br.com.tucunare.apoiodigital.enums.TiposPendencia;

public record IAAgentYTutorialRequestDTO(String contexto, String pergunta, String resposta_escrita, TiposPendencia tipo_dependencia, String descricao_duvida) {
}
