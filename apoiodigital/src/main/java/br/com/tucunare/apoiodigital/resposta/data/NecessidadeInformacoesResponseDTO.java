package br.com.tucunare.apoiodigital.resposta.data;

import java.util.UUID;

/**
 * @param idPedido Not part of the literal spec'd response shape ({@code interromper}/{@code
 *                 pergunta} are), but is added because {@code idPedido} is required by
 *                 {@code POST /resposta/validar/resposta-necessidade}'s request body and this is
 *                 the only place a Pedido id is ever produced — see the refactor report for why.
 */
public record NecessidadeInformacoesResponseDTO(boolean interromper, PerguntaDTO pergunta, UUID idPedido) {
}
