package br.com.tucunare.apoiodigital.tutorial.InformationNeeds;

import br.com.tucunare.apoiodigital.tutorial.TiposPendencia;

import java.util.List;

public record ChecksInformationNeedsResponseDTO(String contexto, String pergunta,
                                                List<String> opcoes, TiposPendencia tipo_pendencia, String
                                                descricao_duvida) {
}
