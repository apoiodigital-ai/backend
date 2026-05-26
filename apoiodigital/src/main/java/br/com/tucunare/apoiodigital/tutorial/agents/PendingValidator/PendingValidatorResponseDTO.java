package br.com.tucunare.apoiodigital.tutorial.agents.PendingValidator;

import br.com.tucunare.apoiodigital.tutorial.TiposPendencia;

public record PendingValidatorResponseDTO(boolean interromper,
                                          TiposPendencia tipo_pendencia, String descricao_duvida) {
}
