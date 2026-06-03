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

//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
//                "timestamp", LocalDateTime.now().toString(),
//                "status", 404,
//                "error", ex.getMessage()
//        ));
//    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
//                "timestamp", LocalDateTime.now().toString(),
//                "status", 500,
//                "error", "Error interno del servidor"
//        ));
//    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
        HttpStatus status, String message)
    {
        ErrorResponse response = new ErrorResponse(status, message);

        return ResponseEntity
            .status(status)
            .body(response);
    }
}
