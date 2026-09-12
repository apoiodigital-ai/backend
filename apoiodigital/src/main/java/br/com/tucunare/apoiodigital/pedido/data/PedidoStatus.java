package br.com.tucunare.apoiodigital.pedido.data;

/**
 * Where a Pedido is in the clarification loop driven by
 * POST /resposta/validar/necessidade-informacoes and POST /resposta/validar/resposta-necessidade.
 */
public enum PedidoStatus {
    /** The Gatekeeper (Agente 0) raised a pendency and is waiting on the user's answer. */
    AGUARDANDO_INFORMACAO,
    /** No pendency, or the pendency has been resolved — safe to act on. */
    PRONTO
}
