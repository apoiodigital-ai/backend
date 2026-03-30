package br.com.tucunare.apoiodigital.dto;

import java.util.List;

public record IARespostaRequestDescryptDTO(String contexto, String pergunta, List<AndroidComponentDTO> elementos) {
}
