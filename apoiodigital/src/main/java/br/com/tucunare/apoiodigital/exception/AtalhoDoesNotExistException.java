package br.com.tucunare.apoiodigital.exception;

public class AtalhoDoesNotExistException extends RuntimeException {
    public AtalhoDoesNotExistException() {
        super("Atalho nao encontrado");
    }
}
