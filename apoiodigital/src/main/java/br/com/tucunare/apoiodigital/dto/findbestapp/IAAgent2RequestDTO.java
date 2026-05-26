package br.com.tucunare.apoiodigital.dto.findbestapp;

import br.com.tucunare.apoiodigital.dto.AppRequestDTO;
import br.com.tucunare.apoiodigital.enums.IAAgent2ModoEnum;
import br.com.tucunare.apoiodigital.model.AppSuportado;

import java.util.List;

public record IAAgent2RequestDTO (String prompt_limpo, List<AppSuportado> lista_app_banco) {
}
