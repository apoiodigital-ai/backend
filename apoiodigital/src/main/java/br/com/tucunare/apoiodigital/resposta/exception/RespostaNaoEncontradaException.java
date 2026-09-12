package br.com.tucunare.apoiodigital.resposta.exception;

public class RespostaNaoEncontradaException extends RuntimeException {
    public RespostaNaoEncontradaException() {
        super("Resposta não encontrada");
    }
}
