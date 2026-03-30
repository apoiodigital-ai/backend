package br.com.tucunare.apoiodigital.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Credenciais invalidas!");
    }
}
