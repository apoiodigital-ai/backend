package br.com.tucunare.apoiodigital.resposta.data;

/**
 * @param viewID Sent as a string per the SDK contract, even though internally it is the
 *               integer viewID of the chosen element (matched back against the elementos the
 *               SDK itself sent in the request).
 * @param mensagemVozUrl Absolute URL to the synthesized audio for mensagem_voz, or null when TTS
 *                       synthesis failed or is disabled — the written instruction still stands
 *                       on its own in that case.
 */
public record AcharRespostaResponseDTO(String viewID, String mensagem_escrita, String mensagem_voz_url, Double precisao) {
}
