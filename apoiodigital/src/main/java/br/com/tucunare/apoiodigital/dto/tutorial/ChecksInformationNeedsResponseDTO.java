package br.com.tucunare.apoiodigital.dto.tutorial;

import br.com.tucunare.apoiodigital.enums.TiposPendencia;

import java.util.List;

public record ChecksInformationNeedsResponseDTO(String pergunta,
                                                List<String> opcoes, TiposPendencia tipo_pendencia, String
                                                descricao_duvida) {
}
