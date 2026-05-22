package br.com.tucunare.apoiodigital.dto.tutorial;

import br.com.tucunare.apoiodigital.enums.TiposPendencia;

public record IAAgent0TutorialResponseDTO(boolean interromper,
                                          TiposPendencia tipo_pendencia, String descricao_duvida) {
}
