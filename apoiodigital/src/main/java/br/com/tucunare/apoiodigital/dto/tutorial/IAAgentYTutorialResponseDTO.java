package br.com.tucunare.apoiodigital.dto.tutorial;

import br.com.tucunare.apoiodigital.enums.TiposPendencia;

public record IAAgentYTutorialResponseDTO(boolean interromper,
                                          TiposPendencia tipo_pendencia, String descricao_duvida) {
}
