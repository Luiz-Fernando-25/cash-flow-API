package org.example.dtos;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Builder;

@Builder
public record TransferRequestDTO(
  @NotNull(message = "O valor é obrigatório") BigDecimal value,
  @NotNull(message = "A data é obrigatória") Date date,
  @NotNull(message = "A saída é obrigatória") Integer accOutputId,
  @NotNull(message = "A entrada é obrigatória") Integer accInputId
) {}
