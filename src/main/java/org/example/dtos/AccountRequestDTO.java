package org.example.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Builder;
import org.example.domain.enums.AccountType;

@Builder
public record AccountRequestDTO(
  Integer id,
  @NotBlank(message = "O nome da conta é obrigatório") String name,
  BigDecimal balance,
  @NotNull(message = "O tipo de conta é obrigatório") AccountType type
) {}
