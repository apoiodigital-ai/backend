package br.com.tucunare.apoiodigital.tutorial.agents.ElementSelector;

/**
 * @param precisao Model's own confidence (0.0-1.0) in the chosen viewID, surfaced to the SDK
 *                 as-is so the partner app can decide whether to trust the highlight or fall
 *                 back to something safer (e.g. asking again) when confidence is low.
 */
public record ElementSelectorResponseDTO(Integer viewID, String raciocinio, Double precisao) {
}
