package br.com.tucunare.apoiodigital.usuario.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Credenciais invalidas!");
    }
}
