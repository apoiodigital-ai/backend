package br.com.tucunare.apoiodigital.agent;

/**
 * Kinds of pending decision the Gatekeeper agent (Agente 0) can flag when it decides a request
 * needs to be interrupted for clarification. Shared across the agent pipeline (and persisted on
 * Pedido while a clarification loop is in progress), so it lives here rather than under a single
 * feature package.
 */
public enum TiposPendencia {
    ambiguidade,
    dado_faltante,
    decisao_usuario,
    nenhuma
}
