package com.store.arka.backend.infrastructure.exception;

import com.store.arka.backend.domain.exception.*;
import com.store.arka.backend.infrastructure.web.dto.ErrorListResponseDto;
import com.store.arka.backend.infrastructure.web.dto.ErrorResponseDto;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorListResponseDto> handlerMethodArgumentNotValidException(
      MethodArgumentNotValidException ex, WebRequest webRequest) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error -> {
      errors.put(error.getField(), error.getDefaultMessage());
    });
    log.warn("Method argument not valid: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorListResponseDto(
        HttpStatus.BAD_REQUEST.value(),
        errors,
        webRequest.getDescription(false)));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolationException(
      DataIntegrityViolationException ex, WebRequest request) {
    String message = "Data integrity violation";
    if (ex.getRootCause() != null && ex.getRootCause().getMessage().contains("llave duplicada")) {
      message = "Duplicate entry: " + extractFieldName(ex.getRootCause().getMessage());
    }
    log.warn("Data integrity violation: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDto(
        HttpStatus.CONFLICT.value(),
        message,
        request.getDescription(false)
    ));
  }

  private String extractFieldName(String message) {
    try {
      int start = message.indexOf('(');
      int end = message.indexOf(')');
      return message.substring(start + 1, end);
    } catch (Exception e) {
      return "unknown field";
    }
  }

  @ExceptionHandler(InvalidEnumValueException.class)
  public ResponseEntity<ErrorResponseDto> handleInvalidEnumValueException(
      InvalidEnumValueException ex, WebRequest request) {
    log.warn("Invalid enum value: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(
        HttpStatus.BAD_REQUEST.value(),
        ex.getMessage(),
        request.getDescription(false)
    ));
  }

  @ExceptionHandler(FieldAlreadyExistsException.class)
  public ResponseEntity<ErrorResponseDto> handlerSkuAlreadyExists(FieldAlreadyExistsException ex, WebRequest webRequest) {
    log.warn("Field already exist: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDto(
        HttpStatus.CONFLICT.value(),
        ex.getMessage(),
        webRequest.getDescription(false)));
  }

  @ExceptionHandler(InvalidArgumentException.class)
  public ResponseEntity<ErrorResponseDto> handlerValidatedFieldRequiredException(
      InvalidArgumentException ex, WebRequest webRequest) {
    log.warn("Invalid argument: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(
        HttpStatus.BAD_REQUEST.value(),
        ex.getMessage(),
        webRequest.getDescription(false)));
  }

  @ExceptionHandler(ModelActivationException.class)
  public ResponseEntity<ErrorResponseDto> handlerBadRequestException(ModelActivationException ex, WebRequest webRequest) {
    log.warn("Model activation: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(
        HttpStatus.BAD_REQUEST.value(),
        ex.getMessage(),
        webRequest.getDescription(false)));
  }

  @ExceptionHandler(ModelDeletionException.class)
  public ResponseEntity<ErrorResponseDto> handlerProductActivationException(
      ModelDeletionException ex, WebRequest webRequest) {
    log.warn("Model deletion: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(
        HttpStatus.BAD_REQUEST.value(),
        ex.getMessage(),
        webRequest.getDescription(false)));
  }

  @ExceptionHandler(ModelNotAvailable.class)
  public ResponseEntity<ErrorResponseDto> handlerProductDeletionException(ModelNotAvailable ex, WebRequest webRequest) {
    log.warn("Model not available: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(
        HttpStatus.BAD_REQUEST.value(),
        ex.getMessage(),
        webRequest.getDescription(false)));
  }

  @ExceptionHandler(ModelNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handlerProductNotFoundException(
      ModelNotFoundException ex, WebRequest webRequest) {
    log.warn("Model not found: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(
        HttpStatus.NOT_FOUND.value(),
        ex.getMessage(),
        webRequest.getDescription(false)));
  }

  @ExceptionHandler(ModelNullException.class)
  public ResponseEntity<ErrorResponseDto> handlerInvalidPriceException(ModelNullException ex, WebRequest webRequest) {
    log.warn("Model null: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(
        HttpStatus.BAD_REQUEST.value(),
        ex.getMessage(),
        webRequest.getDescription(false)));
  }

  @ExceptionHandler(QuantityBadRequestException.class)
  public ResponseEntity<ErrorResponseDto> handlerInvalidStockException(
      QuantityBadRequestException ex, WebRequest webRequest) {
    log.warn("Quantity bad request: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(
        HttpStatus.BAD_REQUEST.value(),
        ex.getMessage(),
        webRequest.getDescription(false)));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponseDto> handlerConstraintViolationException(
      ConstraintViolationException ex, WebRequest webRequest) {
    log.warn("Constraint violation: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(
        HttpStatus.BAD_REQUEST.value(),
        ex.getMessage(),
        webRequest.getDescription(false)));
  }

  @ExceptionHandler(OptimisticLockException.class)
  public ResponseEntity<ErrorResponseDto> handlerOptimisticLockException(
      OptimisticLockException ex, WebRequest webRequest) {
    log.warn("Optimistic lock conflict detected: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDto(
        HttpStatus.CONFLICT.value(),
        "The resource was modified by another user. Please refresh and try again.",
        webRequest.getDescription(false)));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDto> handlerException(Exception ex, WebRequest request) {
    log.warn("General exception: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "Ups... Something went wrong! " + ex.getMessage(),
        request.getDescription(false)));
  }
}
