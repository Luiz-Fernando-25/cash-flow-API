package org.example.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreditCardRequestDTO(
    Integer id,
    @NotBlank(message = "O nome não pode ser vazio") String name,
    BigDecimal limit,
    BigDecimal balance,
    @NotNull(message = "O dia de fechamento é obrigatório") int closingDay,
    @NotNull(message = "O dia de vencimento é obrigatório") int dueDate, 
    @NotNull(message = "O banco é obrigatório") int bankId

) {
}
