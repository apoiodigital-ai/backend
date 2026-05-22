package br.com.tucunare.apoiodigital.dto.tutorial;

import br.com.tucunare.apoiodigital.dto.AndroidComponentDTO;

import java.util.List;


public record ChecksInformationNeedsRequestDTO(List<AndroidComponentDTO>
                                               elementos, String contexto, String prompt) {
}
