package org.example.dtos;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Builder;
import org.example.domain.enums.TransactionStatus;
import org.example.domain.enums.TransactionType;

@Builder
public record TransactionUpdateDTO(
  Integer id,
  BigDecimal transactionValue,
  String description,
  Date date,
  TransactionStatus status,
  Integer categoryId,
  TransactionType type,
  Integer accountId,
  Integer creditCardId,
  Date dueDate
) {}
