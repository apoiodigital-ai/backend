package br.com.tucunare.apoiodigital.dto;

import java.util.UUID;

public record RequisicaoComparasionRequestDTO(String prompt, UUID id_usuario, UUID id_requisicao) {
}
