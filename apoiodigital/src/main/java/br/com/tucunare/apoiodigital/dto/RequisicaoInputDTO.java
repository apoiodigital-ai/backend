package br.com.tucunare.apoiodigital.dto;

import java.util.List;
import java.util.UUID;

public record RequisicaoInputDTO(String prompt, UUID id_usuario, List<AppRequestDTO> lista_apps_instalados) {
}
