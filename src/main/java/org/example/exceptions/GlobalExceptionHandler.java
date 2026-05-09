package org.example.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<StandardError> handlResourceNotFoundException(
    ResourceNotFoundException ex,
    HttpServletRequest request
  ) {
    StandardError err = StandardError.builder()
      .timestamp(LocalDateTime.now())
      .status(HttpStatus.NOT_FOUND.value())
      .error("Resource not found")
      .message(ex.getMessage())
      .path(request.getRequestURI())
      .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
  }

  @ExceptionHandler(BusinessRuleException.class)
  public ResponseEntity<StandardError> handleBusinessRuleException(
    BusinessRuleException ex,
    HttpServletRequest request
  ) {
    StandardError err = StandardError.builder()
      .timestamp(LocalDateTime.now())
      .status(HttpStatus.UNPROCESSABLE_CONTENT.value())
      .error("Business rule violation")
      .message(ex.getMessage())
      .path(request.getRequestURI())
      .build();

    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(err);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<StandardError> handleValidationExceptions(
    MethodArgumentNotValidException ex,
    HttpServletRequest request
  ) {
    List<String> errors = ex
      .getBindingResult()
      .getFieldErrors()
      .stream()
      .map(e -> e.getField() + ": " + e.getDefaultMessage())
      .collect(Collectors.toList());

    StandardError err = StandardError.builder()
      .timestamp(LocalDateTime.now())
      .status(HttpStatus.BAD_REQUEST.value())
      .error("Validation error")
      .message("Um ou mais campos estão inválidos")
      .validationErrors(errors)
      .path(request.getRequestURI())
      .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
  }
}
