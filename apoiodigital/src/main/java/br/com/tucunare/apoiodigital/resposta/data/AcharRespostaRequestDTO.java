package br.com.tucunare.apoiodigital.resposta.data;

import br.com.tucunare.apoiodigital.agent.AndroidComponentDTO;

import java.util.List;
import java.util.UUID;

/**
 * Body of {@code POST /resposta/achar-resposta}. {@code prompt} is expected to already carry
 * any clarification the user gave in the validar/* loop (that loop only threads state through
 * idPedido between its own two endpoints — this endpoint takes no idPedido, matching the SDK
 * contract exactly, so it is the caller's responsibility to fold the resolved answer into the
 * prompt it sends here).
 */
public record AcharRespostaRequestDTO(UUID userId, String prompt, List<AndroidComponentDTO> elementos) {
}
