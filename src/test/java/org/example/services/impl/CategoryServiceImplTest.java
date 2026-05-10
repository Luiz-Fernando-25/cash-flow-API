package org.example.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.example.domain.enums.CategoryType;
import org.example.domain.models.Category;
import org.example.exceptions.BusinessRuleException;
import org.example.exceptions.ResourceNotFoundException;
import org.example.repositories.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

  @Mock
  private CategoryRepository categoryRepository;

  @InjectMocks
  private CategoryServiceImpl categoryService;

  @Test
  @DisplayName("Deve lançar exceção ao tentar criar categoria com nome já existente")
  void shouldThrowExceptionWhenCategoryNameExists() {
    String name = "Lazer";
    Category existing = new Category(name, CategoryType.DESPESA);
    when(categoryRepository.findAll()).thenReturn(List.of(existing));

    BusinessRuleException exception = assertThrows(
        BusinessRuleException.class,
        () -> {
          categoryService.create(name, CategoryType.DESPESA);
        });

    assertEquals(
        "Já existe esse nome na lista de catogorias.",
        exception.getMessage());
    verify(categoryRepository, never()).save(any());
  }

  @ParameterizedTest
  @DisplayName("Deve lançar exceção ao tentar salvar categoria com nome inválido")
  @NullAndEmptySource
  @ValueSource(strings = { "  " })
  void shouldThrowExceptionWhenCategoryNameIsInvalid(String name) {
    BusinessRuleException exception = assertThrows(
        BusinessRuleException.class,
        () -> {
          categoryService.create(name, CategoryType.DESPESA);
        });

    assertEquals("O nome não pode ser vazio!", exception.getMessage());
    verify(categoryRepository, never()).save(any());
  }

  @Test
  @DisplayName("Deve criar categoria com sucesso")
  void shouldCreateCategorySuccessfully() {
    when(categoryRepository.findAll()).thenReturn(List.of());
    when(categoryRepository.save(any())).thenReturn(
        new Category("Lazer", CategoryType.DESPESA));

    Category createdCategory = categoryService.create(
        "Lazer",
        CategoryType.DESPESA);

    assertEquals("Lazer", createdCategory.getName());
    assertEquals(CategoryType.DESPESA, createdCategory.getType());
    verify(categoryRepository, atLeastOnce()).save(any());
  }

  @Test
  @DisplayName("Deve lançar exceção ao tentar buscar categoria inexistente")
  void shouldThrowExceptionWhenCategoryNotFound() {
    when(categoryRepository.findById(1)).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> {
          categoryService.findById(1);
        });

    assertEquals("Categoria com ID 1 não encontrada", exception.getMessage());
    verify(categoryRepository, atLeastOnce()).findById(1);
  }

  @Test
  @DisplayName("Deve buscar com sucesso por id")
  void shouldFindCategorySuccessfullyById() {
    Category category = new Category("Lazer", CategoryType.DESPESA);
    when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

    Category foundCategory = categoryService.findById(1);

    assertEquals(category, foundCategory);
    assertEquals("Lazer", foundCategory.getName());
    assertEquals(CategoryType.DESPESA, foundCategory.getType());
    verify(categoryRepository, atLeastOnce()).findById(1);
  }

  @Test
  @DisplayName("Deve buscar todas as categorias com sucesso")
  void shouldFindAllCategoriesSuccessfully() {
    List<Category> categories = new ArrayList<>();
    categories.add(new Category("Lazer", CategoryType.DESPESA));
    categories.add(new Category("Alimentação", CategoryType.DESPESA));
    when(categoryRepository.findAll()).thenReturn(categories);

    List<Category> foundCategories = categoryService.listAll(null);

    assertEquals(2, foundCategories.size());
    verify(categoryRepository, atLeastOnce()).findAll();
  }

  @Test
  @DisplayName("Deve buscar todas as categorias com sucesso pelo tipo")
  void shouldFindAllCategoriesSuccessfullyByType() {
    List<Category> categories = new ArrayList<>();
    categories.add(new Category("Lazer", CategoryType.DESPESA));
    categories.add(new Category("Salario", CategoryType.RECEITA));
    when(categoryRepository.findAll()).thenReturn(categories);

    List<Category> foundCategories = categoryService.listAll(
        CategoryType.DESPESA);

    assertEquals(1, foundCategories.size());
    assertEquals("Lazer", foundCategories.get(0).getName());
    verify(categoryRepository, atLeastOnce()).findAll();
  }

  @ParameterizedTest
  @DisplayName("Deve lançar exceção ao tentar atualziar categoria com nome inválido")
  @NullAndEmptySource
  @ValueSource(strings = { "  " })
  void shouldThrowExceptionWhenUpdateCategoryNameInvalid(String name) {
    when(categoryRepository.findById(1)).thenReturn(
        Optional.of(new Category("Lazer", CategoryType.DESPESA)));

    BusinessRuleException exception = assertThrows(
        BusinessRuleException.class,
        () -> {
          categoryService.update(1, name, CategoryType.DESPESA);
        });

    assertEquals("O nome não pode ser vazio!", exception.getMessage());
    verify(categoryRepository, never()).save(any());
  }

  @Test
  @DisplayName("Deve lançar exceção ao tentar atualizar categoria com nome já existente")
  void shouldThrowExceptionWhenUpdateCategoryNameExists() {
    when(categoryRepository.findAll()).thenReturn(
        List.of(new Category("Lazer", CategoryType.DESPESA)));
    when(categoryRepository.findById(1)).thenReturn(
        Optional.of(new Category("Lazer", CategoryType.DESPESA)));

    BusinessRuleException exception = assertThrows(
        BusinessRuleException.class,
        () -> {
          categoryService.update(1, "Lazer", CategoryType.DESPESA);
        });

    assertEquals(
        "Já existe esse nome na lista de catogorias.",
        exception.getMessage());
    verify(categoryRepository, never()).save(any());
  }

  @Test
  @DisplayName("Deve atualizar categoria com sucesso")
  void shouldUpdateCategorySuccessfully() {
    when(categoryRepository.findAll()).thenReturn(List.of()); // sem nome duplicado
    when(categoryRepository.findById(1)).thenReturn(
        Optional.of(new Category("Lazer", CategoryType.DESPESA)));
    when(categoryRepository.save(any())).thenReturn(
        new Category("Academia", CategoryType.DESPESA));

    Category updatedCategory = categoryService.update(
        1,
        "Academia", null);

    assertEquals("Academia", updatedCategory.getName());
    assertEquals(CategoryType.DESPESA, updatedCategory.getType());
    verify(categoryRepository, atLeastOnce()).save(any());
  }

  @Test
  @DisplayName("Deve lançar exceção ao tentar remover categoria com ID 1")
  void shouldThrowExceptionWhenRemoveCategoryWithIdOne() {
    Category categoryId1 = new Category("Lazer", CategoryType.DESPESA);
    categoryId1.setId(1); 

    when(categoryRepository.findById(1)).thenReturn(Optional.of(categoryId1));

    BusinessRuleException exception = assertThrows(
        BusinessRuleException.class,
        () -> {
          categoryService.remove(1);
        });

    assertEquals("Não é possivel excluir essa categoria", exception.getMessage());
    verify(categoryRepository, never()).delete(any());
  }

  @Test
  @DisplayName("Deve remover categoria com sucesso")
  void shouldRemoveCategorySuccessfully() {
    Category categoryId2 = new Category("Alimentação", CategoryType.DESPESA);
    categoryId2.setId(2); // Usando ID 2 para não cair na regra de bloqueio do ID 1

    when(categoryRepository.findById(2)).thenReturn(Optional.of(categoryId2));

    categoryService.remove(2);

    verify(categoryRepository, atLeastOnce()).delete(any());
  }

}
