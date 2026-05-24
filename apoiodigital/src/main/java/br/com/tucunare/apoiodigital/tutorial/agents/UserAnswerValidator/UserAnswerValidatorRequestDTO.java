package br.com.tucunare.apoiodigital.tutorial.agents.UserAnswerValidator;

import br.com.tucunare.apoiodigital.enums.TiposPendencia;

public record UserAnswerValidatorRequestDTO(String contexto, String pergunta, String resposta_escrita, TiposPendencia tipo_dependencia, String descricao_duvida) {
}
