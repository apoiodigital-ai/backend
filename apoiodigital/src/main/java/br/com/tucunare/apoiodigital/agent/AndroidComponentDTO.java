package br.com.tucunare.apoiodigital.agent;

/**
 * One element of the UI tree captured by the SDK on the partner's screen ("elementos" in every
 * request body). Shared by the whole agent pipeline (ElementSelector picks one by viewID,
 * ScreenContextDefiner describes the chosen one, Componente hashes the whole list into a
 * cache-key signature).
 */
public record AndroidComponentDTO(Integer viewID, String className, String additionalInfo) {
}
