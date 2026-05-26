package br.com.tucunare.apoiodigital.dto.requisicao;

import br.com.tucunare.apoiodigital.dto.AppRequestDTO;

import java.util.List;
import java.util.UUID;

public record RequisicaoInputDTO(String prompt, UUID id_usuario) {
}
