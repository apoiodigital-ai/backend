package br.com.tucunare.apoiodigital.requisicao.data;

import java.util.UUID;

public record RequisicaoInputDTO(String prompt, UUID id_usuario) {
}
