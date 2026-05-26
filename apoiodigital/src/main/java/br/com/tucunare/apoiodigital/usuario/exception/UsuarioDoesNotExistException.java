package br.com.tucunare.apoiodigital.usuario.exception;

public class UsuarioDoesNotExistException extends RuntimeException {
    public UsuarioDoesNotExistException() {
        super("Usuario nao encontrado!");
    }
}
