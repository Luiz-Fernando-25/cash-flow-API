package org.example.exceptions;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record StandardError(
  LocalDateTime timestamp,
  Integer status,
  String error,
  String message,
  String path,
  List<String> validationErros
) {}
