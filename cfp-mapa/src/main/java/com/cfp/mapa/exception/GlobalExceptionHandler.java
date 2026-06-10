package com.cfp.mapa.exception;

import com.cfp.mapa.dto.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
        UsernameNotFoundException.class,
        BadCredentialsException.class,
        DisabledException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationException(Exception ex) {

        return buildErrorResponse(
            HttpStatus.UNAUTHORIZED,
            "Credenciales incorrectas (DNI o contraseña inválidos)");
    }

    @ExceptionHandler(DniDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleDniDuplicadoException(DniDuplicadoException ex) {

        return buildErrorResponse(
            HttpStatus.CONFLICT,
            ex.getMessage()
        );
    }

    @ExceptionHandler(DniNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDniNotFoundException(DniNotFoundException ex) {

        return buildErrorResponse(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );
    }

    @ExceptionHandler(EspacioNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEspacioNotFound(EspacioNotFoundException ex) {

        return buildErrorResponse(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {

        log.error("Ocurrió una excepción no controlada en la API: ", ex);

        return buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Error interno en el servidor"
        );
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
        HttpStatus status, String message)
    {
        ErrorResponse response = new ErrorResponse(status, message);

        return ResponseEntity
            .status(status)
            .body(response);
    }
}
