package br.com.tucunare.apoiodigital.exception;

public class TelefoneAlreayExistsException extends RuntimeException {
    public TelefoneAlreayExistsException() {
        super("Este telefone ja esta cadastrado!");
    }
}
