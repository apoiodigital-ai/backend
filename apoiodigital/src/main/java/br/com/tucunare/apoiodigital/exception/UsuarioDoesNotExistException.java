package br.com.tucunare.apoiodigital.exception;

public class UsuarioDoesNotExistException extends RuntimeException {
    public UsuarioDoesNotExistException() {
        super("Usuario nao encontrado!");
    }
}
