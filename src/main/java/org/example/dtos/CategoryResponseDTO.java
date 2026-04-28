package org.example.dtos;

import lombok.Builder;
import org.example.domain.enums.CategoryType;

@Builder
public record CategoryResponseDTO(Integer id, String name, CategoryType type) {}
