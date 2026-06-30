package com.cfp.mapa.exception;

import com.cfp.mapa.dto.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {

        return buildErrorResponse(
            HttpStatus.UNAUTHORIZED,
            "Credenciales incorrectas (DNI o contraseña inválidos)");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {

        return buildErrorResponse(
            HttpStatus.UNAUTHORIZED,
            "Token inválido o revocado"
        );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabledException(DisabledException ex) {

        return buildErrorResponse(
            HttpStatus.UNAUTHORIZED,
            "Tu cuenta se encuentra temporalmente desactivada"
        );
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationCredentialsNotFoundException(
        org.springframework.security.authentication.AuthenticationCredentialsNotFoundException ex
    ) {

        return buildErrorResponse(
            HttpStatus.UNAUTHORIZED,
            "No se encontraron credenciales de autenticación"
        );
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

    @ExceptionHandler(PasswordIncorrectaException.class)
    public ResponseEntity<ErrorResponse> handlePasswordIncorrectaException(PasswordIncorrectaException ex) {

        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            ex.getMessage()
        );
    }

    @ExceptionHandler(ParametroAccionInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleParametroAccionInvalidoException(ParametroAccionInvalidoException ex) {

        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
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

    //------------------------
    //----- Conexiones -------
    //------------------------

    @ExceptionHandler(ConexionNotFoundException.class)
    public ResponseEntity<?> conexionNotFound(ConexionNotFoundException ex){

        log.error(ex.getMessage());

        return buildErrorResponse(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException ex
    ) {

        String mensajeError = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .filter(msg -> msg != null && !msg.isBlank())
            .findFirst()
            .orElse("Error de validación en los datos enviados");

        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            mensajeError
        );
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(
        HandlerMethodValidationException ex
    ) {

        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Error de validación en los parámetros de la petición"
        );
    }

    @ExceptionHandler(NombreInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleNombreInvalidoException(NombreInvalidoException ex) {

        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            ex.getMessage()
        );
    }

    @ExceptionHandler(AccionNoPermitidaException.class)
    public ResponseEntity<ErrorResponse> handleAccionNoPermitidaException(
        AccionNoPermitidaException ex
    ) {

        ErrorResponse response = new ErrorResponse(
            HttpStatus.FORBIDDEN,
            ex.getMessage(),
            "SESSION_INVALIDATED"
        );

        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(response);
    }

    @ExceptionHandler(AccionInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleAccionInvalidaException(AccionInvalidaException ex) {

        return buildErrorResponse(
            HttpStatus.FORBIDDEN,
            ex.getMessage()
        );
    }

    @ExceptionHandler(RolInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleRolInvalidoException(RolInvalidoException ex) {

        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            ex.getMessage()
        );
    }

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioNotFoundException(UsuarioNotFoundException ex) {

        return buildErrorResponse(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {

        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Uno de los parámetros proporcionados contiene un valor inválido o no reconocido."
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
        MethodArgumentTypeMismatchException ex
    ) {

        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            String.format("El parámetro '%s' debe ser de tipo '%s'",
                ex.getName(), ex.getRequiredType().getSimpleName())
        );
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public  ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(
        AuthorizationDeniedException ex
    ) {

        return buildErrorResponse(
            HttpStatus.FORBIDDEN,
            "No tienes los permisos necesarios para acceder a este recurso."
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

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReporteNotFoundException(ResourceNotFoundException ex) {

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    // ======================================
    // FUNCIONES PRIVADAS
    // ======================================

    private ResponseEntity<ErrorResponse> buildErrorResponse(
        HttpStatus status, String message)
    {
        ErrorResponse response = new ErrorResponse(status, message);

        return ResponseEntity
            .status(status)
            .body(response);
    }
}
