package br.com.tucunare.apoiodigital.findbestapp.agents.ChooseLocalApp;

import br.com.tucunare.apoiodigital.appsuportado.data.AppSuportado;

import java.util.List;

public record ChooseLocalAppRequestDTO(String prompt_limpo, List<AppSuportado> lista_app_banco) {
}
