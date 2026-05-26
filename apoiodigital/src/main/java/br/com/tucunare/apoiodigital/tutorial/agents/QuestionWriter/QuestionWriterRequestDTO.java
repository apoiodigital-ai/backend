package br.com.tucunare.apoiodigital.tutorial.agents.QuestionWriter;

import br.com.tucunare.apoiodigital.tutorial.AndroidComponentDTO;
import br.com.tucunare.apoiodigital.tutorial.TiposPendencia;

import java.util.List;

public record QuestionWriterRequestDTO(TiposPendencia tipo_dependencia, String descricao_duvida, List<AndroidComponentDTO> elementos) {
}
