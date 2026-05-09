package org.example.controllers;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.domain.enums.CategoryType;
import org.example.domain.models.Category;
import org.example.dtos.CategoryRequestDTO;
import org.example.dtos.CategoryResponseDTO;
import org.example.dtos.CategoryUpdateDTO;
import org.example.mappers.CategoryMapper;
import org.example.services.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;
  private final CategoryMapper categoryMapper;

  @PostMapping
  public ResponseEntity<CategoryResponseDTO> save(
    @RequestBody @Valid CategoryRequestDTO categoryRequestDTO
  ) {
    CategoryResponseDTO responseDTO = categoryMapper.toDto(
      categoryService.create(
        categoryRequestDTO.name(),
        categoryRequestDTO.type()
      )
    );

    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
      .path("/{id}")
      .buildAndExpand(responseDTO.id())
      .toUri();

    return ResponseEntity.created(location).body(responseDTO);
  }

  @GetMapping(path = "/{id}")
  public ResponseEntity<CategoryResponseDTO> findById(
    @PathVariable Integer id
  ) {
    return ResponseEntity.ok(
      categoryMapper.toDto(categoryService.findById(id))
    );
  }

  @GetMapping
  public ResponseEntity<List<CategoryResponseDTO>> listAll(
    @RequestParam(required = false) CategoryType type
  ) {
    return ResponseEntity.ok(
      categoryMapper.toDtoList(categoryService.listAll(type))
    );
  }

  @DeleteMapping(path = "/{id}")
  public ResponseEntity<Void> delete(@PathVariable @Valid Integer id) {
    categoryService.remove(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PatchMapping(path = "/{id}")
  public ResponseEntity<CategoryResponseDTO> replace(
    @PathVariable Integer id,
    @RequestBody CategoryUpdateDTO dto
  ) {
    Category updatedCategory = categoryService.update(
      id,
      dto.name(),
      dto.type()
    );
    return ResponseEntity.ok(categoryMapper.toDto(updatedCategory));
  }
}
