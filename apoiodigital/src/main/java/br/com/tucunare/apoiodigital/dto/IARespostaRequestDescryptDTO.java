package br.com.tucunare.apoiodigital.dto;

import java.util.List;
import java.util.UUID;

public record IARespostaRequestDescryptDTO(String contexto, String prompt, List<AndroidComponentDTO> elementos, UUID id_requisicao) {
}
