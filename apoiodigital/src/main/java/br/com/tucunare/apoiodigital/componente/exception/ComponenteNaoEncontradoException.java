package br.com.tucunare.apoiodigital.componente.exception;

public class ComponenteNaoEncontradoException extends RuntimeException {
    public ComponenteNaoEncontradoException() {
        super("Nenhuma assinatura de componente encontrada para essa resposta");
    }
}
