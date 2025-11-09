package com.store.arka.backend.infrastructure.exception;

import com.store.arka.backend.domain.exception.*;
import com.store.arka.backend.infrastructure.web.dto.ErrorListResponseDto;
import com.store.arka.backend.infrastructure.web.dto.ErrorResponseDto;
import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
  /**
   * Convierte errores de validación (@Valid) en un 400 con un mapa campo -> mensaje
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorListResponseDto> handlerMethodArgumentNotValidException(
      MethodArgumentNotValidException ex, WebRequest request) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error -> {
      errors.put(error.getField(), error.getDefaultMessage());
    });
    log.warn("[GLOBAL_EXCEPTION_HANDLER][METHOD_ARGUMENT_NOT_VALID] Invalid fields: {}", errors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorListResponseDto(
        HttpStatus.BAD_REQUEST.value(), errors, request.getDescription(false)));
  }

  /**
   * Devuelve 400 cuando falta o está malformado el body JSON (evita 500)
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex, HttpServletRequest request) {
    log.warn("[GLOBAL_EXCEPTION_HANDLER][HTTP_MESSAGE_NOT_READABLE] Malformed or missing request body");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(
        HttpStatus.BAD_REQUEST.value(), "Request body is missing or malformed", request.getRequestURI()
    ));
  }

  /**
   * Devuelve 409 CONFLICT en violaciones de integridad; intenta detectar duplicados y extraer el campo
   */
  public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolationException(
      DataIntegrityViolationException ex, WebRequest request) {
    String message = "Data integrity violation";
    ex.getRootCause();
    if (ex.getRootCause().getMessage().contains("Llave duplicada")) {
      message = "Duplicate entry: " + extractFieldName(ex.getRootCause().getMessage());
    }
    log.error("[GLOBAL_EXCEPTION_HANDLER][DATA_INTEGRITY_VIOLATION] {}", message);
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDto(
        HttpStatus.CONFLICT.value(), message, request.getDescription(false)));
  }

  private String extractFieldName(String message) {
    if (message == null) return "unknown field";
    int start = message.indexOf('(');
    int end = message.indexOf(')');
    if (start >= 0 && end > start) return message.substring(start + 1, end);
    return "unknown field";
  }

  /**
   * 400 para errores de negocio genéricos
   */
  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponseDto> handlerBusiness(BusinessException ex, WebRequest request) {
    log.warn("[GLOBAL_EXCEPTION_HANDLER][BUSINESS] {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(
        HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getDescription(false)));
  }

  /**
   * 409 para campos/entidades ya existentes
   */
  @ExceptionHandler(FieldAlreadyExistsException.class)
  public ResponseEntity<ErrorResponseDto> handlerFieldAlreadyExists(FieldAlreadyExistsException ex, WebRequest request) {
    log.warn("[GLOBAL_EXCEPTION_HANDLER][FIELD_ALREADY_EXISTS] Field already exists: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDto(
        HttpStatus.CONFLICT.value(), ex.getMessage(), request.getDescription(false)));
  }

  /**
   * 400 para argumentos inválidos
   */
  @ExceptionHandler(InvalidArgumentException.class)
  public ResponseEntity<ErrorResponseDto> handlerInvalidArgument(InvalidArgumentException ex, WebRequest request) {
    log.warn("[GLOBAL_EXCEPTION_HANDLER][INVALID_ARGUMENT] Invalid argument: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(
        HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getDescription(false)));
  }

  /**
   * 400 cuando un valor enum no existe
   */
  @ExceptionHandler(InvalidEnumValueException.class)
  public ResponseEntity<ErrorResponseDto> handleInvalidEnumValue(InvalidEnumValueException ex, WebRequest request) {
    log.warn("[GLOBAL_EXCEPTION_HANDLER][INVALID_ENUM_VALUE] Invalid enum value: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(
        HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getDescription(false)
    ));
  }

  /**
   * 400 para IDs con formato inválido
   */
  @ExceptionHandler(InvalidIdException.class)
  public ResponseEntity<ErrorResponseDto> handleInvalidId(InvalidIdException ex, WebRequest request) {
    log.warn("[GLOBAL_EXCEPTION_HANDLER][INVALID_ID] Invalid ID: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(
        HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getDescription(false)));
  }

  /**
   * 400 para transiciones de estado inválidas
   */
  @ExceptionHandler(InvalidStateException.class)
  public ResponseEntity<ErrorResponseDto> handleInvalidState(InvalidStateException ex, WebRequest request) {
    log.warn("[GLOBAL_EXCEPTION_HANDLER][INVALID_STATE] Invalid state: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(
        HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getDescription(false)
    ));
  }

  /**
   * 400 cuando una colección esperada está vacía
   */
  @ExceptionHandler(ItemsEmptyException.class)
  public ResponseEntity<ErrorResponseDto> handleItemsEmpty(ItemsEmptyException ex, WebRequest request) {
    log.warn("[GLOBAL_EXCEPTION_HANDLER][ITEMS_EMPTY] Empty item list");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(
        HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getDescription(false)
    ));
  }

  /**
   * 409 para errores al activar modelos (conflicto)
   */
  @ExceptionHandler(ModelActivationException.class)
  public ResponseEntity<ErrorResponseDto> handlerModelActivation(ModelActivationException ex, WebRequest request) {
    log.error("[GLOBAL_EXCEPTION_HANDLER][MODEL_ACTIVATION] Activation failed: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDto(
        HttpStatus.CONFLICT.value(), ex.getMessage(), request.getDescription(false)));
  }

  /**
   * 409 para errores al eliminar (conflicto)
   */
  @ExceptionHandler(ModelDeletionException.class)
  public ResponseEntity<ErrorResponseDto> handlerModelDeletion(ModelDeletionException ex, WebRequest request) {
    log.error("[GLOBAL_EXCEPTION_HANDLER][MODEL_DELETION] Deletion failed: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDto(
        HttpStatus.CONFLICT.value(), ex.getMessage(), request.getDescription(false)));
  }

  /**
   * 409 si el recurso no está disponible
   */
  @ExceptionHandler(ModelNotAvailableException.class)
  public ResponseEntity<ErrorResponseDto> handlerModelNotAvailable(ModelNotAvailableException ex, WebRequest request) {
    log.error("[GLOBAL_EXCEPTION_HANDLER][MODEL_NOT_AVAILABLE] Model not available: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDto(
        HttpStatus.CONFLICT.value(), ex.getMessage(), request.getDescription(false)));
  }

  /**
   * 404 cuando no existe el recurso
   */
  @ExceptionHandler(ModelNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handlerModelNotFound(ModelNotFoundException ex, WebRequest request) {
    log.error("[GLOBAL_EXCEPTION_HANDLER][MODEL_NOT_FOUND] Model not found {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(
        HttpStatus.NOT_FOUND.value(), ex.getMessage(), request.getDescription(false)));
  }

  /**
   * 400 cuando falta un modelo obligado
   */
  @ExceptionHandler(ModelNullException.class)
  public ResponseEntity<ErrorResponseDto> handlerModelNull(ModelNullException ex, WebRequest request) {
    log.error("[GLOBAL_EXCEPTION_HANDLER][MODEL_NULL] Null model: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(
        HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getDescription(false)));
  }

  /**
   * 409 para errores en validación de pagos
   */
  @ExceptionHandler(PaymentValidationException.class)
  public ResponseEntity<ErrorResponseDto> handlerPaymentValidation(PaymentValidationException ex, WebRequest request) {
    log.error("[GLOBAL_EXCEPTION_HANDLER][PAYMENT_VALIDATION] Payment validation failed: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDto(
        HttpStatus.CONFLICT.value(), ex.getMessage(), request.getDescription(false)));
  }

  /**
   * 400 cuando un producto no está presente en una operación esperada
   */
  @ExceptionHandler(ProductNotFoundInOperationException.class)
  public ResponseEntity<ErrorResponseDto> handlerProductNotFoundInOrder(ProductNotFoundInOperationException ex, WebRequest request) {
    log.error("[GLOBAL_EXCEPTION_HANDLER][PRODUCT_NOT_FOUND_IN_OPERATION] Product not found in operation: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(
        HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getDescription(false)));
  }

  /**
   * 400 para inconsistencias de cantidad/stock
   */
  @ExceptionHandler(QuantityBadRequestException.class)
  public ResponseEntity<ErrorResponseDto> handlerInvalidStockException(QuantityBadRequestException ex, WebRequest request) {
    log.warn("[GLOBAL_EXCEPTION_HANDLER][QUANTITY_BAD_REQUEST] Quantity error: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(
        HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getDescription(false)));
  }

  /**
   * 400 para violaciones de constraints (bean validation fuera de controller)
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponseDto> handlerConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
    log.warn("[GLOBAL_EXCEPTION_HANDLER][CONSTRAINT_VIOLATION] Constraint violation: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(
        HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getDescription(false)));
  }

  /**
   * 409 cuando ocurre conflicto por concurrencia optimista
   */
  @ExceptionHandler(OptimisticLockException.class)
  public ResponseEntity<ErrorResponseDto> handlerOptimisticLockException(OptimisticLockException ex, WebRequest request) {
    log.error("[GLOBAL_EXCEPTION_HANDLER][OPTIMISTIC_LOCK] Optimistic lock detected");
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDto(
        HttpStatus.CONFLICT.value(),
        "The resource was modified by another user. Please refresh and try again.",
        request.getDescription(false)));
  }

  /**
   * 404 sin cuerpo (resource not found)
   */
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleNoResourceFound(NoResourceFoundException ex, WebRequest request) {
    log.warn("[GLOBAL_EXCEPTION_HANDLER][NO_RESOURCE_FOUND] Path not found for request: {}", request.getDescription(false));
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(
        HttpStatus.NOT_FOUND.value(),
        "Invalid path or endpoint. Please verify the requested URL",
        request.getDescription(false)
    ));
  }

  /**
   * 401 - Error de autenticación interna
   */
  @ExceptionHandler(InternalAuthenticationServiceException.class)
  public ResponseEntity<ErrorResponseDto> handleInternalAuthError(InternalAuthenticationServiceException ex, WebRequest request) {
    if (ex.getCause() instanceof UserNotFoundException userNotFound) {
      log.warn("[GLOBAL_EXCEPTION_HANDLER][INTERNAL_AUTH_SERVICE] User not found: {}", userNotFound.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(
          HttpStatus.NOT_FOUND.value(), userNotFound.getMessage(), request.getDescription(false)
      ));
    }
    log.error("[GLOBAL_EXCEPTION_HANDLER][INTERNAL_AUTH_SERVICE] Unexpected authentication error", ex);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(
        HttpStatus.UNAUTHORIZED.value(), "Authentication failed", request.getDescription(false)
    ));
  }

  /**
   * 401 para token o credenciales inválidas
   */
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponseDto> handleAuthentication(AuthenticationException ex, WebRequest request) {
    log.warn("[GLOBAL_EXCEPTION_HANDLER][AUTH] Invalid or missing credentials");
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(
        HttpStatus.UNAUTHORIZED.value(), "Authentication required or invalid token", request.getDescription(false)));
  }

  /**
   * 403 con URI y mensaje de acceso denegado
   */
  @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
  public ResponseEntity<ErrorResponseDto> handleAccessDenied(Exception ex, HttpServletRequest request) {
    log.warn("[GLOBAL_EXCEPTION_HANDLER][FORBIDDEN] Access denied to '{}'", request.getRequestURI());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponseDto(
        HttpStatus.FORBIDDEN.value(),
        "Access denied. You do not have permission to perform this action.",
        request.getRequestURI()
    ));
  }

  /**
   * 500 para errores inesperados (log de stacktrace)
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDto> handlerException(Exception ex, WebRequest request) {
    log.error("[GLOBAL_EXCEPTION_HANDLER][EXCEPTION] Unexpected error ", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "Unexpected server error. Please contact support.",
        request.getDescription(false)));
  }
}
