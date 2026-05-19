package br.com.tucunare.apoiodigital.dto.tutorial;

import br.com.tucunare.apoiodigital.enums.TiposDependecia;

public record IAAgentYTutorialRequestDTO(String contexto, String perrgunta, String resposta_escrita, TiposDependecia tipo_dependencia, String descricao_duvida) {
}
