package br.com.tucunare.apoiodigital.dto;

import java.util.List;

public record FindBestAppResponseDTO(String context, Long id_app_banco, Integer id_app_lista, String title, List<RelatedAppsCheckDTO> _related_apps_check ){
}
