package br.com.tucunare.apoiodigital.exception;

public class RequisicaoDoesNotExistException extends RuntimeException {
    public RequisicaoDoesNotExistException() {
        super("Requisicao nao encontrada");
    }
}
