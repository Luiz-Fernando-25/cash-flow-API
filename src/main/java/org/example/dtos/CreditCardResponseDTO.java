package org.example.dtos;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record CreditCardResponseDTO(
  Integer id,
  String name,
  BigDecimal limit,
  BigDecimal balance,
  Integer closingDay,
  Integer dueDate,
  Integer bankId
) {}
