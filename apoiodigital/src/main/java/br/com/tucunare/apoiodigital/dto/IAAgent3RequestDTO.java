package br.com.tucunare.apoiodigital.dto;

import br.com.tucunare.apoiodigital.model.AppSuportado;

import java.util.List;

public record IAAgent3RequestDTO (String pacote, List<AppSuportado> banco_apps_suportados){
}
