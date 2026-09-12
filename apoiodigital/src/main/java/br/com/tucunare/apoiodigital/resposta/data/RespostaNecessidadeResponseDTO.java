package br.com.tucunare.apoiodigital.resposta.data;

import java.util.UUID;

public record RespostaNecessidadeResponseDTO(boolean interromper, PerguntaDTO pergunta, UUID idPedido) {
}
