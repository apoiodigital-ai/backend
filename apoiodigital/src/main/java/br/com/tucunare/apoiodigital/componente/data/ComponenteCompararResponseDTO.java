package br.com.tucunare.apoiodigital.componente.data;

/**
 * @param valido true when the freshly-hashed screen matches the assinatura stored for the given
 *               Resposta — the cached instruction is still safe to reuse without a fresh AI call.
 */
public record ComponenteCompararResponseDTO(boolean valido) {
}
