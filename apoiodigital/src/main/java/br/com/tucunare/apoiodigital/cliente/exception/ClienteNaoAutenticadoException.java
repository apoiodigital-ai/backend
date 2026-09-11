package br.com.tucunare.apoiodigital.cliente.exception;

/**
 * Thrown when application code asks for the current tenant but the security filter chain has
 * not resolved one (should not normally happen, since every request path requires
 * authentication — this is a defensive backstop, not the 401 path itself).
 */
public class ClienteNaoAutenticadoException extends RuntimeException {
    public ClienteNaoAutenticadoException() {
        super("Nenhum cliente autenticado para esta requisição");
    }
}
