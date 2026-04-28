package org.example.controllers;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.domain.enums.CategoryType;
import org.example.dtos.CategoryRequestDTO;
import org.example.dtos.CategoryResponseDTO;
import org.example.mappers.CategoryMapper;
import org.example.services.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    return new ResponseEntity<>(
      categoryMapper.toDto(
        categoryService.create(
          categoryRequestDTO.name(),
          categoryRequestDTO.type()
        )
      ),
      HttpStatus.CREATED
    );
  }

  @GetMapping(path = "/all")
  public ResponseEntity<List<CategoryResponseDTO>> listAll() {
    return ResponseEntity.ok(
      categoryMapper.toDtoList(categoryService.listAll())
    );
  }

  @GetMapping(path = "/{type}")
  public ResponseEntity<List<CategoryResponseDTO>> findByType(
    @PathVariable CategoryType type
  ) {
    return ResponseEntity.ok(
      categoryMapper.toDtoList(categoryService.ListForType(type))
    );
  }

  @DeleteMapping(path = "/{id}")
  public ResponseEntity<Void> delete(@PathVariable @Valid Integer id) {
    categoryService.remove(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PutMapping
  public ResponseEntity<Void> replace(
    @RequestBody @Valid CategoryRequestDTO categoryRequestDTO
  ) {
    categoryService.changeName(
      categoryRequestDTO.id(),
      categoryRequestDTO.name()
    );
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
