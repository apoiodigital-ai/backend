package br.com.tucunare.apoiodigital.pedido.exception;

public class PedidoDoesNotExistException extends RuntimeException {
    public PedidoDoesNotExistException() {
        super("Pedido não encontrado");
    }
}
