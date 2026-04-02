package br.com.tucunare.apoiodigital.dto;

import java.util.List;

public record RequestInputToGeminiDTO(String prompt, List<AppSuportadoToGeminiDTO> lista_apps_banco , List<AppRequestDTO> lista_apps_instalados) {
}
