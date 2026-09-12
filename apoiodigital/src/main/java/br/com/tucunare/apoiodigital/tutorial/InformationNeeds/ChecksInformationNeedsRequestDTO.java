package br.com.tucunare.apoiodigital.tutorial.InformationNeeds;

import br.com.tucunare.apoiodigital.tutorial.AndroidComponentDTO;

import java.util.List;


public record ChecksInformationNeedsRequestDTO(List<AndroidComponentDTO>
                                               elementos, String contexto, String prompt) {
}
