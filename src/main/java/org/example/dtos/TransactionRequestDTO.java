package org.example.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Builder;
import org.example.domain.enums.TransactionStatus;
import org.example.domain.enums.TransactionType;

@Builder
public record TransactionRequestDTO(
  Integer id,
  @NotNull(message = "O valor da transação é obrigatório")
  BigDecimal transactionValue,
  @NotBlank(message = "A descrição da transação é obrigatória")
  String description,
  @NotNull(message = "A data da transação é obrigatória") Date date,
  @NotNull(message = "O status da transação é obrigatório")
  TransactionStatus status,
  @NotNull(message = "A categoria da transação é obrigatória")
  Integer categoryId,
  @NotNull(message = "O tipo da transação é obrigatório") TransactionType type,
  @NotNull(message = "A conta da transação é obrigatória") Integer accountId,
  Integer creditCardId,
  Date dueDate
) {}
