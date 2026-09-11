package br.com.tucunare.apoiodigital.resposta.data;

import br.com.tucunare.apoiodigital.agent.AndroidComponentDTO;

import java.util.List;
import java.util.UUID;

/** Body of {@code POST /resposta/validar/necessidade-informacoes} (the Gatekeeper entry point). */
public record NecessidadeInformacoesRequestDTO(UUID userId, String prompt, List<AndroidComponentDTO> elementos) {
}
