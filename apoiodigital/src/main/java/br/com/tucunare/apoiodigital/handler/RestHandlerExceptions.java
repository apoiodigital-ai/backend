package br.com.tucunare.apoiodigital.handler;

import br.com.tucunare.apoiodigital.cliente.exception.ClienteNaoAutenticadoException;
import br.com.tucunare.apoiodigital.componente.exception.ComponenteNaoEncontradoException;
import br.com.tucunare.apoiodigital.pedido.exception.PedidoDoesNotExistException;
import br.com.tucunare.apoiodigital.resposta.exception.RespostaNaoEncontradaException;
import br.com.tucunare.apoiodigital.usuario.exception.UsuarioDoesNotExistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Centralized error mapping so every controller returns the same clean JSON shape
 * ({@link ExceptionDTO}) instead of leaking a default Spring error page or a raw stack trace —
 * previously RequisicaoController caught RuntimeException just to rethrow a plain
 * RuntimeException, which fell straight through to Spring's default (and in dev, stack-trace
 * -bearing) error response. The generic handler at the bottom is the backstop for anything not
 * explicitly mapped above it.
 */
@ControllerAdvice
public class RestHandlerExceptions {

    private static final Logger log = LoggerFactory.getLogger(RestHandlerExceptions.class);

    // USUARIO ----------------------------------------------
    @ExceptionHandler(UsuarioDoesNotExistException.class)
    public ResponseEntity<ExceptionDTO> usuarioDoesNotExistHandler(UsuarioDoesNotExistException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionDTO(HttpStatus.NOT_FOUND.value(), "UsuarioDoesNotExist", ex.getMessage()));
    }

    // PEDIDO ---------------------------------------
    @ExceptionHandler(PedidoDoesNotExistException.class)
    public ResponseEntity<ExceptionDTO> pedidoDoesNotExistHandler(PedidoDoesNotExistException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionDTO(HttpStatus.NOT_FOUND.value(), "PedidoDoesNotExist", ex.getMessage()));
    }

    // RESPOSTA / COMPONENTE ---------------------------------------
    @ExceptionHandler(RespostaNaoEncontradaException.class)
    public ResponseEntity<ExceptionDTO> respostaNaoEncontradaHandler(RespostaNaoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionDTO(HttpStatus.NOT_FOUND.value(), "RespostaNaoEncontrada", ex.getMessage()));
    }

    @ExceptionHandler(ComponenteNaoEncontradoException.class)
    public ResponseEntity<ExceptionDTO> componenteNaoEncontradoHandler(ComponenteNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionDTO(HttpStatus.NOT_FOUND.value(), "ComponenteNaoEncontrado", ex.getMessage()));
    }

    // TENANT / SECURITY ---------------------------------------
    @ExceptionHandler(ClienteNaoAutenticadoException.class)
    public ResponseEntity<ExceptionDTO> clienteNaoAutenticadoHandler(ClienteNaoAutenticadoException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ExceptionDTO(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionDTO> accessDeniedHandler(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ExceptionDTO(HttpStatus.FORBIDDEN.value(), "Forbidden", "Acesso negado"));
    }

    // FALLBACK ---------------------------------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDTO> genericHandler(Exception ex) {
        log.error("Erro não tratado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "InternalServerError",
                "Ocorreu um erro interno. Tente novamente mais tarde."
        ));
    }

}
