package br.com.tucunare.apoiodigital.dto.findbestapp;

import br.com.tucunare.apoiodigital.dto.AppRequestDTO;
import br.com.tucunare.apoiodigital.enums.IAAgent2ModoEnum;

import java.util.List;

public record IAAgent2RequestDTO (String prompt_limpo, List<AppRequestDTO> lista_apps_instalados, IAAgent2ModoEnum modo) {
}
