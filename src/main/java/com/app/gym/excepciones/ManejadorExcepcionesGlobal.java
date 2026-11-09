package com.app.gym.excepciones;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.exception.ConstraintViolationException;
import org.postgresql.util.PSQLException;

@RestControllerAdvice
public class ManejadorExcepcionesGlobal {

    /**
     * Clase para manejar excepciones globalmente en la aplicación.
     * Utiliza @RestControllerAdvice para interceptar excepciones lanzadas por los controladores
     */

    private static final Logger logger = LoggerFactory.getLogger(ManejadorExcepcionesGlobal.class);

    @ExceptionHandler(RecursoNoEncontradoExcepcion.class) // Maneja excepciones de recursos no encontrados
    public ResponseEntity<String> handleRecursoNoEncontrado(RecursoNoEncontradoExcepcion ex) { 
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class) // Maneja excepciones de estado ilegal
    public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(FechaInvalidaExcepcion.class) // Maneja excepciones de fecha inválida
    public ResponseEntity<?> handleFechaInvalidaExcepcion(FechaInvalidaExcepcion ex){
	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(EmailNotVerifiedException.class) // Maneja excepciones de fecha inválida
    public ResponseEntity<?> handleEmailNoVerificadoExcepcion(EmailNotVerifiedException ex){
	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(AccountDisabledException.class) // Maneja excepciones de fecha inválida
    public ResponseEntity<?> handleCuentaInactivaExcepcion(AccountDisabledException ex){
	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(EmailYaExisteExcepcion.class) // Maneja excepciones de email ya existente
    public ResponseEntity<?> handleEmailExistenteExcepcion(EmailYaExisteExcepcion ex){
	return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(RateLimitException .class) // Maneja excepciones de límite de tasa excedido
    public ResponseEntity<?> handleRateLimitExcepcion(RateLimitException  ex){
	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(EmailYaVerificadoExcepcion.class) // Maneja excepciones de email ya verificado
    public ResponseEntity<?> handleEmailVerificadoExcepcion(EmailYaVerificadoExcepcion ex){
	return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ContrasenaInvalidaExcepcion.class) // Maneja excepciones de contraseña inválida
    public ResponseEntity<?> handleContrasenaInvalidaExcepcion(ContrasenaInvalidaExcepcion ex){
	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(TokenInvalidoExcepcion.class) // Maneja excepciones de token inválido
    public ResponseEntity<?> handleTokenInvalidoExcepcion(TokenInvalidoExcepcion ex){
	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(TokenExpiradoExcepcion.class) // Maneja excepciones de token expirado
    public ResponseEntity<?> handleTokenExpiradoExcepcion(TokenExpiradoExcepcion ex){
	return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(UsuarioSinMembresiaExepcion.class) // Maneja excepciones de usuario sin membresía
    public ResponseEntity<?> handleUsuarioSinMembresiaExcepcion(UsuarioSinMembresiaExepcion ex){
	return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(AppSecurityException.class)
    public ResponseEntity<?> handleAppSecurityException(AppSecurityException ex){
	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<?> handleLockedException(AccountLockedException ex){
	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<?> handleEmailSendingException(EmailSendingException ex){
	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorizedException(UnauthorizedException ex){
	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Validation failed");
        response.put("errors", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ContrasenaIgualExcepcion.class) // Maneja excepciones de contraseña igual
    public ResponseEntity<?> handleContrasenaInvalidaExcepcion(ContrasenaIgualExcepcion ex){
	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    /* Estas excepciones son para cuando se haga un intento de inicio de sesión con credenciales incorrectas,
        o si ocurre un error en la autenticación del usuario. */

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "error", "BAD_CREDENTIALS",
                        "message", "Email o contraseña incorrectos"
                ));
    }

    @ExceptionHandler(RedisConnectionFailureException.class)
    public ResponseEntity<?> handleRedisErrorConection(RedisConnectionFailureException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error", "UNABLE_TO_CONNECT_REDIS",
                        "message", "No se pudo conectar al servidor de Redis. Por favor, intentelo de nuevo."
                ));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<?> handleSecurityException(SecurityException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error", "Error de seguridad",
                        "message", "Error inesperado de la clase SecurityException - Java Lang"
                ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "error", "AUTHENTICATION_FAILED",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AuthorizationDeniedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "error", "ACCESS_DENIED",
                        "message", "You do not have permission to access this resource. Please, contact the administrator if you believe this is an error."
                ));
    }


    /**
     * Un único handler para violaciones de integridad (constraints únicos, etc.).
     * Acepta DataIntegrityViolationException y algunos tipos de root causes comunes.
     */
    @ExceptionHandler({ DataIntegrityViolationException.class, ConstraintViolationException.class, PSQLException.class })
    public ResponseEntity<?> handleDataIntegrityViolation(Exception ex) {
        // Log para diagnóstico (no enviar excepciones crudas al cliente)
        logger.error("Violación de integridad detectada", ex);

        // Intentar obtener mensaje raíz que viene de la DB
        Throwable root = findRootCause(ex);
        String dbMessage = root != null && root.getMessage() != null ? root.getMessage() : ex.getMessage();
        String mensajeUsuario = "Violación de integridad de datos.";

        // Detectar casos comunes: llave duplicada / unique constraint
        if (dbMessage != null) {
            String lower = dbMessage.toLowerCase();
            if (lower.contains("duplicate") || lower.contains("duplicate key") || lower.contains("llave duplicada")
                    || lower.contains("unique") || lower.contains("violates unique") || lower.contains("already exists")) {
                // Intentar extraer columna/constraint del mensaje
                String encontrado = extractConstraintDetail(dbMessage);
                if (encontrado != null) {
                    mensajeUsuario = "Valor duplicado para: " + encontrado + ". Verifique y vuelva a intentar.";
                } else {
                    mensajeUsuario = "Ya existe un registro con ese valor único. Verifique e intente con otro.";
                }
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", mensajeUsuario));
            }
        }

        // Fallback: Bad request con mensaje genérico
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", mensajeUsuario));
    }

    // Catch-all: maneja cualquier excepción no prevista.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        logger.error("Error inesperado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error inesperado: " + ex.getMessage()));
    }

    // ---------- helpers ----------

    private Throwable findRootCause(Throwable t) {
        Throwable root = t;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        return root;
    }

    /**
     * Intenta parsear el nombre de columna/constraint desde el mensaje del motor (Postgres, Hibernate, etc.)
     */
    private String extractConstraintDetail(String dbMessage) {
        // Ejemplo Postgres: "Key (email)=(foo@bar.com) already exists."
        Pattern p1 = Pattern.compile("\\(([^)]+)\\)=\\(([^)]+)\\)");
        Matcher m1 = p1.matcher(dbMessage);
        if (m1.find()) {
            return m1.group(1) + " = " + m1.group(2);
        }
        // Ejemplo: constraint "usuarios_email_key"
        Pattern p2 = Pattern.compile("constraint \"([^\"]+)\"", Pattern.CASE_INSENSITIVE);
        Matcher m2 = p2.matcher(dbMessage);
        if (m2.find()) {
            return m2.group(1);
        }
        return null;
    }
}

