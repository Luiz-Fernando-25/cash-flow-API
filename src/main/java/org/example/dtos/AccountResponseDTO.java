package org.example.dtos;

import java.math.BigDecimal;
import lombok.Builder;
import org.example.domain.enums.AccountType;

@Builder
public record AccountResponseDTO(
  Integer id,
  String name,
  BigDecimal balance,
  AccountType type
) {}
