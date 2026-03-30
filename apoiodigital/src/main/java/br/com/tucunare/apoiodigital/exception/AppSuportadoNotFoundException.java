package br.com.tucunare.apoiodigital.exception;

public class AppSuportadoNotFoundException extends RuntimeException{
    public AppSuportadoNotFoundException(String message) {
        super(message);
    }
}
