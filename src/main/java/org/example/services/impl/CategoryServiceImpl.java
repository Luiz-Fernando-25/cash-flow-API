package org.example.services.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.example.domain.enums.CategoryType;
import org.example.domain.models.Category;
import org.example.exceptions.BusinessRuleException;
import org.example.exceptions.ResourceNotFoundException;
import org.example.repositories.CategoryRepository;
import org.example.services.CategoryService;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository repoCategory;

  public CategoryServiceImpl(CategoryRepository repoCategory) {
    this.repoCategory = repoCategory;
  }

  @Override
  public Category create(String name, CategoryType type) {
    if (name == null || name.trim().isEmpty()) throw new BusinessRuleException(
      "O nome não pode ser vazio!"
    );

    boolean exists = repoCategory
      .findAll()
      .stream()
      .anyMatch(c -> c.getName().equalsIgnoreCase(name));

    if (exists) {
      throw new BusinessRuleException(
        "Já existe esse nome na lista de catogorias."
      );
    }

    Category category = new Category(name, type);

    return repoCategory.save(category);
  }

  @Override
  public Category findById(Integer categoryId) {
    return repoCategory
      .findById(categoryId)
      .orElseThrow(() ->
        new ResourceNotFoundException(
          "Categoria com ID " + categoryId + " não encontrada"
        )
      );
  }

  @Override
  public Category update(Integer categoryId, String name, CategoryType type) {
    Category category = this.findById(categoryId);

    if (name == null || name.trim().isEmpty()) throw new BusinessRuleException(
      "O nome não pode ser vazio!"
    );
    category.setName(name);

    if (type != null) category.setType(type);

    return repoCategory.save(category);
  }

  @Override
  public List<Category> listAll() {
    return repoCategory.findAll();
  }

  @Override
  public List<Category> ListForType(CategoryType type) {
    return repoCategory
      .findAll()
      .stream()
      .filter(c -> c.getType().equals(type))
      .collect(Collectors.toList());
  }

  @Override
  public void remove(Integer categoryId) {
    Category category = this.findById(categoryId);
    if (category.getId() == 1) {
      throw new BusinessRuleException("Não é possivel excluir essa categoria");
    }
    repoCategory.delete(category);
  }
}
