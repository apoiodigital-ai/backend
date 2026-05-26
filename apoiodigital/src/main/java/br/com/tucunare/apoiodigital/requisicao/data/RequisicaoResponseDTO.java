package br.com.tucunare.apoiodigital.requisicao.data;

import java.util.List;

public record RequisicaoResponseDTO(String criacao, List<Requisicao> requisicoes) {
}
