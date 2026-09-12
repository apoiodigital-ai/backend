package br.com.tucunare.apoiodigital.resposta.data;

import java.util.UUID;

/** Body of {@code POST /resposta/validar/resposta-necessidade}. */
public record RespostaNecessidadeRequestDTO(UUID userId, UUID idPedido, String resposta) {
}
