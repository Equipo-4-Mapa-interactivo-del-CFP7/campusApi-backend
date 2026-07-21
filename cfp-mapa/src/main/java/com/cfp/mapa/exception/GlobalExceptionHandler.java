package com.cfp.mapa.exception;

import com.cfp.mapa.dto.error.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
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

    // ======================================
    // SEGURIDAD
    // ======================================

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {

        log.warn("[SECURITY-AUTH] Intento de inicio de sesión fallido: usuario no encontrado. Causa: {} | Detalles: {}",
            ex.getClass().getSimpleName(), ex.getMessage());

        return buildErrorResponse(
            HttpStatus.UNAUTHORIZED,
            "Credenciales incorrectas (DNI o contraseña inválidos)");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {

        log.warn("[SECURITY-AUTH] Intento de acceso fallido: credenciales o token inválidos. Causa: {} | Detalles: {}",
            ex.getClass().getSimpleName(), ex.getMessage());

        return buildErrorResponse(
            HttpStatus.UNAUTHORIZED,
            "Token inválido o revocado"
        );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabledException(DisabledException ex) {

        log.warn("[SECURITY-AUTH] Intento de acceso rechazado: cuenta desactivada. Causa: {} | Detalles: {}",
            ex.getClass().getSimpleName(), ex.getMessage());

        return buildErrorResponse(
            HttpStatus.UNAUTHORIZED,
            "Tu cuenta se encuentra temporalmente desactivada"
        );
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationCredentialsNotFoundException(
        org.springframework.security.authentication.AuthenticationCredentialsNotFoundException ex
    ) {

        log.warn("[SECURITY-AUTH] Acceso rechazado por falta de credenciales en la petición. Causa: {} | Detalles: {}",
            ex.getClass().getSimpleName(), ex.getMessage());

        return buildErrorResponse(
            HttpStatus.UNAUTHORIZED,
            "No se encontraron credenciales de autenticación"
        );
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public  ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(
        AuthorizationDeniedException ex
    ) {

        log.warn("[SECURITY-DENIED] Acceso denegado: el usuario no posee los privilegios requeridos. Causa: {} | Detalles: {}",
            ex.getClass().getSimpleName(), ex.getMessage());

        return buildErrorResponse(
            HttpStatus.FORBIDDEN,
            "No tienes los permisos necesarios para acceder a este recurso."
        );
    }

    // ======================================
    // USUARIOS
    // ======================================

    @ExceptionHandler(DniDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleDniDuplicadoException(DniDuplicadoException ex) {

        log.warn("[BUSINESS-CONFLICT] Conflicto de datos: el DNI ya se encuentra registrado. Causa: {} | Detalles: {}",
            ex.getClass().getSimpleName(), ex.getMessage());

        return buildErrorResponse(
            HttpStatus.CONFLICT,
            ex.getMessage()
        );
    }

    @ExceptionHandler(DniNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDniNotFoundException(DniNotFoundException ex) {

        log.warn("[NOT-FOUND] Búsqueda fallida: DNI no registrado. Causa: {} | Detalles: {}",
            ex.getClass().getSimpleName(), ex.getMessage());

        return buildErrorResponse(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );
    }

    @ExceptionHandler(PasswordIncorrectaException.class)
    public ResponseEntity<ErrorResponse> handlePasswordIncorrectaException(PasswordIncorrectaException ex) {

        log.warn("[AUTH-WARNING] Intento de autenticación fallido por contraseña incorrecta. Causa: {} | Detalles: {}",
            ex.getClass().getSimpleName(), ex.getMessage());

        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            ex.getMessage()
        );
    }

    @ExceptionHandler(NombreInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleNombreInvalidoException(NombreInvalidoException ex) {

        log.warn("[VALIDATION-WARNING] Formato de nombre de usuario no permitido. Causa: {} | Detalles: {}",
            ex.getClass().getSimpleName(), ex.getMessage());

        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            ex.getMessage()
        );
    }

    @ExceptionHandler(RolInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleRolInvalidoException(RolInvalidoException ex) {

        log.warn("[VALIDATION-WARNING] El rol asignado no existe o no es válido en el sistema. Causa: {} | Detalles: {}",
            ex.getClass().getSimpleName(), ex.getMessage());

        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            ex.getMessage()
        );
    }

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioNotFoundException(UsuarioNotFoundException ex) {

        log.warn("[NOT-FOUND] Usuario solicitado no localizado. Causa: {} | Detalles: {}",
            ex.getClass().getSimpleName(), ex.getMessage());

        return buildErrorResponse(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );
    }

    // ======================================
    // REPORTES
    // ======================================

    @ExceptionHandler(ReporteNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReporteNotFoundException(ReporteNotFoundException ex) {

        log.warn("[NOT-FOUND] Reporte solicitado no localizado. Causa: {} | Detalles: {}",
            ex.getClass().getSimpleName(), ex.getMessage());

        return buildErrorResponse(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );
    }

    @ExceptionHandler(ReporteConflictException.class)
    public ResponseEntity<ErrorResponse> handleReporteConflictException(ReporteConflictException ex) {

        log.warn("[BUSINESS-CONFLICT] Conflicto al procesar o generar el reporte. Causa: {} | Detalles: {}",
            ex.getClass().getSimpleName(), ex.getMessage());

        return buildErrorResponse(
            HttpStatus.CONFLICT,
            ex.getMessage()
        );
    }

    // ======================================
    // ESPACIOS
    // ======================================

    @ExceptionHandler(EspacioNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEspacioNotFoundException(EspacioNotFoundException ex) {

        log.warn("[NOT-FOUND] Espacio solicitado no localizado. Causa: {} | Detalles: {}",
            ex.getClass().getSimpleName(), ex.getMessage());

        return buildErrorResponse(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );
    }

    @ExceptionHandler(ConexionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleConexionNotFoundException(ConexionNotFoundException ex){

        log.warn("[NOT-FOUND] Conexión entre espacios no localizada. Causa: {} | Detalles: {}",
            ex.getClass().getSimpleName(), ex.getMessage());

        return buildErrorResponse(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );
    }

    // ======================================
    // AUDITORIA
    // ======================================

    @ExceptionHandler(AuditoriaAnonimizacionException.class)
    public ResponseEntity<ErrorResponse> handleAuditoriaAnonimizacionException(AuditoriaAnonimizacionException ex) {

        log.error("[INTERNAL-ERROR] Fallo crítico durante el proceso de anonimización. Causa: {} | Detalles: {}",
            ex.getCause() != null ? ex.getCause().getClass().getSimpleName() : "Desconocida",
            ex.getMessage());

        return buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Ocurrió un error interno al procesar la baja del usuario"
        );
    }

    // ======================================
    // GENERAL
    // ======================================

    @ExceptionHandler(AccionNoPermitidaException.class)
    public ResponseEntity<ErrorResponse> handleAccionNoPermitidaException(
        AccionNoPermitidaException ex
    ) {

        log.warn("[SECURITY-WARNING] Acción no permitida por el usuario. Causa: {} | Detalles: {}",
            ex.getClass().getSimpleName(), ex.getMessage());

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

        log.warn("[BUSINESS-WARNING] Operación rechazada por reglas de negocio. Causa: {} | Detalles: {}",
            ex.getClass().getSimpleName(), ex.getMessage());

        return buildErrorResponse(
            HttpStatus.FORBIDDEN,
            ex.getMessage()
        );
    }

    @ExceptionHandler(OperacionInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleOperacionInvalidaException(OperacionInvalidaException ex) {

        log.warn("[BUSINESS-VALIDATION] Solicitud rechazada por regla de negocio. Causa: {} | Detalles: {}",
            ex.getClass().getSimpleName(), ex.getMessage());

        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            ex.getMessage()
        );
    }

    @ExceptionHandler(SolicitudIncorrectaException.class)
    public ResponseEntity<ErrorResponse> handleSolicitudIncorrectaException(SolicitudIncorrectaException ex) {

        log.warn("[BAD-REQUEST] Petición mal armada por el cliente. Causa: {} | Detalles: {}",
            ex.getClass().getSimpleName(), ex.getMessage());

        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            ex.getMessage()
        );
    }

    @ExceptionHandler(ParametroAccionInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleParametroAccionInvalidoException(ParametroAccionInvalidoException ex) {

        log.warn("[PARAM-WARNING] Parámetro de acción inválido en la solicitud. Causa: {} | Detalles: {}",
            ex.getClass().getSimpleName(), ex.getMessage());

        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            ex.getMessage()
        );
    }

    @ExceptionHandler(InvalidDataAccessResourceUsageException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDataAccessResourceUsageException(InvalidDataAccessResourceUsageException ex) {

        log.error("[PERSISTENCE-ERROR] Error de sintaxis o recurso de base de datos no encontrado. Causa: {} | Detalles: {}",
            ex.getClass().getSimpleName(), ex.getMessage());

        return buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Ocurrió un error interno al procesar la consulta en la base de datos"
        );
    }

    @ExceptionHandler(SQLGrammarException.class)
    public ResponseEntity<ErrorResponse> handleSQLGrammarException(SQLGrammarException ex) {

        log.error("[PERSISTENCE-ERROR] Error en la gramática SQL generada o ejecutada. Causa: {} | Detalles: {}",
            ex.getClass().getSimpleName(), ex.getMessage());

        return buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Ocurrió un error de sintaxis en la consulta de persistencia"
        );
    }

    @ExceptionHandler(SQLSyntaxErrorException.class)
    public ResponseEntity<ErrorResponse> handleSQLSyntaxErrorException(SQLSyntaxErrorException ex) {

        log.error("[DATABASE-ERROR] Sintaxis SQL inválida recibida por el motor de BD. Causa: {} | Detalles: {}",
            ex.getClass().getSimpleName(), ex.getMessage());

        return buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Error de ejecución en el motor de base de datos"
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

        log.warn("[VALIDATION-WARNING] Datos de entrada del DTO inválidos. Causa: {} | Error detectado: {}",
            ex.getClass().getSimpleName(), mensajeError);

        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            mensajeError
        );
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(
        HandlerMethodValidationException ex
    ) {

        log.warn("[VALIDATION-WARNING] Parámetros del controlador inválidos. Causa: {} | Detalles: {}",
            ex.getClass().getSimpleName(), ex.getMessage());

        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Error de validación en los parámetros de la petición"
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {

        log.warn("[CLIENT-ERROR] Argumento ilegal o inapropiado provisto por el cliente. Causa: {} | Detalles: {}",
            ex.getClass().getSimpleName(), ex.getMessage());

        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Uno de los parámetros proporcionados contiene un valor inválido o no reconocido."
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
        MethodArgumentTypeMismatchException ex
    ) {
        Class<?> requiredType = ex.getRequiredType();
        assert requiredType != null;

        log.warn("[TYPE-MISMATCH] Error de tipo en parámetro de URL. Propiedad: '{}' | Causa: {}",
            ex.getName(), ex.getMessage());

        String mensajeUsuario;

        // Caso 1: El error ocurrió al parsear un Enum (ya sea suelto o dentro de una lista)
        if (requiredType.isEnum() || (
            ex.getCause() instanceof ConversionFailedException cfe && cfe.getTargetType().getType().isEnum())
        ) {

            mensajeUsuario = String.format(
                "El valor proporcionado para el parámetro '%s' no es válido o no corresponde a las opciones permitidas",
                ex.getName()
            );
        }
        // Caso 2: Error comun de conversión (ej: mandar "letras" en un ID Long)
        else {
            mensajeUsuario = String.format(
                "El parámetro '%s' debe ser de tipo '%s'",
                ex.getName(), requiredType.getSimpleName()
            );
        }

        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            mensajeUsuario
        );
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException ex) {

        log.error("[PERSISTENCE-ERROR] Estructura de consulta inválida en los parámetros de persistencia (JPA). Causa: {} | Detalles: {}",
            ex.getClass().getSimpleName(), ex.getMessage());

        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "La solicitud contiene parámetros o estructuras de consulta inválidas"
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {

        // Extraer y formatear los detalles de los campos que fallaron
        String detallesErrores = ex.getConstraintViolations().stream()
            .map(violation -> {
                // Extrae el nombre del parámetro (ej: "id") y el mensaje (ej: "debe ser mayor que 0")
                String propiedad = violation.getPropertyPath().toString();
                // Limpiar el path si viene con el nombre (ej: "cambiarRol.usuarioId" -> "usuarioId")
                String campo = propiedad.substring(propiedad.lastIndexOf('.') + 1);
                return campo + ": " + violation.getMessage();
            })
            .collect(Collectors.joining(", "));

        log.warn("[BAD-REQUEST] Violación de restricciones en los parámetros de la petición. Detalles: [{}]", detallesErrores);

        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Los parámetros de la petición no son válidos: " + detallesErrores
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {

        String mensajeDetallado;

        if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException hibernateEx) {
            String detalleSql = hibernateEx.getSQLException() != null ? hibernateEx.getSQLException().getMessage() : "";

            mensajeDetallado = "No se pudo guardar el registro debido a una restricción en la base de datos. Detalles: " + detalleSql;
        } else {
            mensajeDetallado = "Violación de integridad: " + ex.getMostSpecificCause().getMessage();
        }

        log.error("[DATA-INTEGRITY-ERROR] Violación de integridad de datos. Causa raíz: {}", ex.getMostSpecificCause().getMessage());

        return buildErrorResponse(
            HttpStatus.CONFLICT,
            "La operación no se pudo completar: " + mensajeDetallado
        );
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex) {

        log.error("[DATABASE-SQL-ERROR] Error directo de restricción SQL. Código de error: {}, Estado SQL: {}, Detalle: {}",
            ex.getErrorCode(), ex.getSQLState(), ex.getMessage());

        return buildErrorResponse(
            HttpStatus.CONFLICT,
            "Error interno de consistencia en la base de datos. Verifique que los identificadores y campos obligatorios sean correctos."
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {

        log.error("[CRITICAL-500] Error interno no controlado detectado en el servidor. Stack Trace completo: ",
            ex);

        return buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Error interno en el servidor"
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
