package org.example.services;

import java.util.List;
import org.example.domain.enums.CategoryType;
import org.example.domain.models.Category;

public interface CategoryService {
  Category create(String name, CategoryType type);

  void changeName(Integer accountid, String name);

  List<Category> listAll();

  List<Category> ListForType(CategoryType type);

  void remove(Integer categoryId);
}
