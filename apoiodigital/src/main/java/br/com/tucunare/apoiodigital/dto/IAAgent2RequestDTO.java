package br.com.tucunare.apoiodigital.dto;

import br.com.tucunare.apoiodigital.enums.IAAgent2ModoEnum;
import br.com.tucunare.apoiodigital.model.AppSuportado;

import java.util.List;

public record IAAgent2RequestDTO (String prompt_limpo, List<AppRequestDTO> lista_apps_instalados, IAAgent2ModoEnum modo) {
}
