package br.com.tucunare.apoiodigital.dto.tutorial;

import br.com.tucunare.apoiodigital.enums.TiposPendencia;

import java.util.List;

public record IAAgent1TutorialRequestDTO(TiposPendencia tipo_dependencia, String descricao_duvida, List<AndroidComponentDTO> elementos) {
}
