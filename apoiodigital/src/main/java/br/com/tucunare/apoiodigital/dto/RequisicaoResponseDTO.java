package br.com.tucunare.apoiodigital.dto;

import br.com.tucunare.apoiodigital.model.Requisicao;

import java.util.List;

public record RequisicaoResponseDTO(String criacao, List<Requisicao> requisicoes) {
}
