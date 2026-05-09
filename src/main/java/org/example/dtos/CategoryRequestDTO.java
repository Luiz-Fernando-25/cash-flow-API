package org.example.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.example.domain.enums.CategoryType;

@Builder
public record CategoryRequestDTO(
  @NotBlank(message = "O nome não pode ser vazio") String name,
  @NotNull(message = "O tipo é obrigatório") CategoryType type
) {}
