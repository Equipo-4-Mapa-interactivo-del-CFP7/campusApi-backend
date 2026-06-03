package com.cfp.mapa.exception;

import com.cfp.mapa.dto.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {

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
