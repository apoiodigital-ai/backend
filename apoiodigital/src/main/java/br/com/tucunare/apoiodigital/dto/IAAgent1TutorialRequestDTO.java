package br.com.tucunare.apoiodigital.dto;

import br.com.tucunare.apoiodigital.enums.TiposDependecia;

import java.util.List;

public record IAAgent1TutorialRequestDTO(TiposDependecia tipo_dependencia, String descricao_duvida, List<AndroidComponentDTO> elementos) {
}
