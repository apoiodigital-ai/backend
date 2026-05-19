package br.com.tucunare.apoiodigital.dto;

import br.com.tucunare.apoiodigital.enums.TiposDependecia;

import java.util.List;

public record IAAgentYTutorialRequestDTO(String contexto, String perrgunta, String resposta_escrita, TiposDependecia tipo_dependencia, String descricao_duvida) {
}
