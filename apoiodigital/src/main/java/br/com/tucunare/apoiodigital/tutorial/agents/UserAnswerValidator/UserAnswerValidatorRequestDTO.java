package br.com.tucunare.apoiodigital.tutorial.agents.UserAnswerValidator;

import br.com.tucunare.apoiodigital.agent.TiposPendencia;

public record UserAnswerValidatorRequestDTO(String pergunta, String resposta_escrita, TiposPendencia tipo_dependencia, String descricao_duvida) {
}
