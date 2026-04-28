package org.example.mappers;

import java.util.List;
import org.example.domain.models.Category;
import org.example.dtos.CategoryResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
  CategoryResponseDTO toDto(Category entity);

  List<CategoryResponseDTO> toDtoList(List<Category> entities);
}
