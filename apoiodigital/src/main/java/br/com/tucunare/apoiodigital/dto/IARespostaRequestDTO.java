package br.com.tucunare.apoiodigital.dto;

import java.util.UUID;

public record IARespostaRequestDTO(String textCrypted, String key, UUID id_requisicao) {
}
