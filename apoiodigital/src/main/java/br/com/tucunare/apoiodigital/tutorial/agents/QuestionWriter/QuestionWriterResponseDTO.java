package br.com.tucunare.apoiodigital.tutorial.agents.QuestionWriter;

import java.util.List;

public record QuestionWriterResponseDTO(String pergunta, List<String> opcoes) {
}
