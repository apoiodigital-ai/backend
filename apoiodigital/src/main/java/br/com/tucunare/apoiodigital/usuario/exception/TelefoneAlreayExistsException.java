package br.com.tucunare.apoiodigital.usuario.exception;

public class TelefoneAlreayExistsException extends RuntimeException {
    public TelefoneAlreayExistsException() {
        super("Este telefone ja esta cadastrado!");
    }
}
