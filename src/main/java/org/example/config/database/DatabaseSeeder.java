package org.example.config.database;

import lombok.AllArgsConstructor;
import org.example.domain.enums.CategoryType;
import org.example.domain.models.Category;
import org.example.repositories.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class DatabaseSeeder {

  private final CategoryRepository categoryRepository;

  @Bean
  public CommandLineRunner seedDataBase(CategoryRepository categoryRepository) {
    return args -> {
      if (categoryRepository.findById(1).isEmpty()) {
        System.out.println(
          "[Database Seeder] Criando Categoria Reservada (ID 1)..."
        );
        Category finalCategory = Category.builder()
          .name("Movimentação")
          .type(CategoryType.MOVIMENTACAO)
          .build();
        categoryRepository.save(finalCategory);
        System.out.println(
          "[Database Seeder] Categoria 'Movimentação' criada com sucesso!"
        );
      }
    };
  }
}
