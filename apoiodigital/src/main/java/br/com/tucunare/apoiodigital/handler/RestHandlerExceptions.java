package br.com.tucunare.apoiodigital.handler;

import br.com.tucunare.apoiodigital.dto.ExceptionDTO;
import br.com.tucunare.apoiodigital.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestHandlerExceptions {

//  USUARIO ----------------------------------------------
    @ExceptionHandler(InvalidPasswordLengthException.class)
    public ResponseEntity<ExceptionDTO> InvalidPasswordLengthHandler(InvalidPasswordLengthException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDTO(HttpStatus.BAD_REQUEST.value(), "InvalidPasswordLength", ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ExceptionDTO> InvalidCredentialsHandler(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ExceptionDTO(HttpStatus.UNAUTHORIZED.value(), "InvalidCredentials", ex.getMessage()));
    }

    @ExceptionHandler(TelefoneAlreayExistsException.class)
    public ResponseEntity<ExceptionDTO> TelefoneAlreadyExistsHandler(TelefoneAlreayExistsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDTO(HttpStatus.BAD_REQUEST.value(), "TelefoneAlreadyExists", ex.getMessage()));
    }

    @ExceptionHandler(UsuarioDoesNotExistException.class)
    public ResponseEntity<ExceptionDTO> UsuarioDoesNotExistHandler(UsuarioDoesNotExistException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionDTO(HttpStatus.NOT_FOUND.value(), "UsuarioDoesNotExist", ex.getMessage()));
    }

    // REQUISICAO ---------------------------------------
    @ExceptionHandler(RequisicaoDoesNotExistException.class)
    public ResponseEntity<ExceptionDTO> RequisicaoDoesNotExistHandler(RequisicaoDoesNotExistException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionDTO(HttpStatus.NOT_FOUND.value(), "RequisicaoDoesNotExist", ex.getMessage()));
    }

    // ATALHO --------------------------------------------
    @ExceptionHandler(AtalhoDoesNotExistException.class)
    public ResponseEntity<ExceptionDTO> AtalhoDoesNotExistHandler(AtalhoDoesNotExistException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionDTO(HttpStatus.NOT_FOUND.value(), "AtalhoDoesNotExist", ex.getMessage()));
    }

}
