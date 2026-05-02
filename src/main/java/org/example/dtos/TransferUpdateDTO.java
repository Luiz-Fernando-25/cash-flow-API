package org.example.dtos;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Builder;

@Builder
public record TransferUpdateDTO(
  Integer id,
  BigDecimal value,
  Date date,
  Integer accOutputId,
  Integer accInputId
) {}
