package br.com.tucunare.apoiodigital.atalho.exception;

public class AtalhoDoesNotExistException extends RuntimeException {
    public AtalhoDoesNotExistException() {
        super("Atalho nao encontrado");
    }
}
