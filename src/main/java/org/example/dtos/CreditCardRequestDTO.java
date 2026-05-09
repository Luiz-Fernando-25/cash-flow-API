package org.example.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record CreditCardRequestDTO(
  @NotBlank(message = "O nome não pode ser vazio") String name,
  BigDecimal limit,
  BigDecimal balance,
  @NotNull(message = "O dia de fechamento é obrigatório") Integer closingDay,
  @NotNull(message = "O dia de vencimento é obrigatório") Integer dueDate,
  @NotNull(message = "O banco é obrigatório") Integer bankId
) {}
