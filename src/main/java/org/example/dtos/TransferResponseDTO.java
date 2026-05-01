package org.example.dtos;

import lombok.Builder;

@Builder
public record TransferResponseDTO(
  Integer id,
  Integer outputTransactionId,
  Integer inputTransactionId
) {}
