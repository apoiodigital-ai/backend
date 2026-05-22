package br.com.tucunare.apoiodigital.dto.tutorial;

import java.util.List;


public record ChecksInformationNeedsRequestDTO(List<AndroidComponentDTO>
                                               elementos, String contexto, String prompt) {
}
