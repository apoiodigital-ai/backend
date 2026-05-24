package br.com.tucunare.apoiodigital.tutorial.agents.QuestionWriter;

import br.com.tucunare.apoiodigital.dto.tutorial.AndroidComponentDTO;
import br.com.tucunare.apoiodigital.enums.TiposPendencia;

import java.util.List;

public record QuestionWriterRequestDTO(TiposPendencia tipo_dependencia, String descricao_duvida, List<AndroidComponentDTO> elementos) {
}
