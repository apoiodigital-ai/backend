package br.com.tucunare.apoiodigital.tts;

import java.util.Optional;

/**
 * Turns the ScreenContextDefiner agent's (Agente Z) "mensagem_voz" text into a playable audio
 * file. Synthesis is treated as best-effort: any failure (network, quota, disabled) yields
 * {@link Optional#empty()} rather than propagating, so a TTS outage never breaks the
 * achar-resposta endpoint's primary job of returning viewID + written instruction.
 */
public interface TtsService {

    /**
     * @return the stored audio file's name (to be served from {@code /audio/<filename>}), or
     * empty when synthesis is disabled or failed.
     */
    Optional<String> synthesize(String texto);
}
