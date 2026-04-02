package br.com.tucunare.apoiodigital.dto;

import java.util.List;

public record FindBestAppResponseDTO(String contexto, Long id_app_banco, Long id_app_instalado, String titulo){
}
