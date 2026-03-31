package br.com.tucunare.apoiodigital.dto;

import java.util.UUID;

public record RequisicaoComparasionResponseDTO(UUID matched_id, UUID origin_id, String prompt) {
}
