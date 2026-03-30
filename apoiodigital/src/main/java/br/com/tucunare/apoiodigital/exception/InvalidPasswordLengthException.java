package br.com.tucunare.apoiodigital.exception;

public class InvalidPasswordLengthException extends RuntimeException {
    public InvalidPasswordLengthException() {
        super("A senha deve ter no minimo 8 caracteres!");
    }
}
