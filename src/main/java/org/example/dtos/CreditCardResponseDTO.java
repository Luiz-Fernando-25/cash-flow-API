package org.example.dtos;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record CreditCardResponseDTO(
    Integer id,
    String name,
    BigDecimal limit,
    BigDecimal balance,
    int closingDay,
    int dueDate, 
    int bankId
) {
    
}
