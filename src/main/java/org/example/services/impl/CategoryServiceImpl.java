package org.example.services.impl;

import java.util.ArrayList;
import java.util.List;
import org.example.domain.enums.CategoryType;
import org.example.domain.models.Category;
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
    if (name == null || name.trim().isEmpty()) throw new RuntimeException(
      "O nome não pode ser vazio!"
    );
    List<Category> categories = repoCategory.findAll();
    for (Category c : categories) {
      if (c.getName().equals(name)) {
        throw new RuntimeException(
          "Já existe esse nome na lista de catogorias."
        );
      }
    }
    Category category = new Category(name, type);
    repoCategory.save(category);
    return category;
  }

  @Override
  public void changeName(Integer categoryId, String name) {
    Category category = repoCategory
      .findById(categoryId)
      .orElseThrow(() ->
        new ResourceNotFoundException(
          "Categoria com ID " + categoryId + " não encontrada"
        )
      );
    if (name == null || name.trim().isEmpty()) throw new RuntimeException(
      "O nome não pode ser vazio!"
    );
    category.setName(name);
    repoCategory.save(category);
  }

  @Override
  public List<Category> listAll() {
    List<Category> categories = repoCategory.findAll();
    return categories;
  }

  @Override
  public List<Category> ListForType(CategoryType type) {
    List<Category> categories = repoCategory.findAll();
    List<Category> categoriesForType = new ArrayList<>();
    for (Category c : categories) {
      if (c.getType() == type) categoriesForType.add(c);
    }
    return categoriesForType;
  }

  @Override
  public void remove(Integer categoryId) {
    if (
      categoryId == null || categoryId < 0
    ) throw new ResourceNotFoundException(
      "Categoria com ID " + categoryId + " não encontrada"
    );
    repoCategory.deleteById(categoryId);
  }
}
