package br.com.tucunare.apoiodigital.requisicao.exception;

public class RequisicaoDoesNotExistException extends RuntimeException {
    public RequisicaoDoesNotExistException() {
        super("Requisicao nao encontrada");
    }
}
