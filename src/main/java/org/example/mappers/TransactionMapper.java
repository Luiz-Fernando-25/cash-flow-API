package org.example.mappers;

import java.util.List;
import org.example.domain.models.AbstractTransaction;
import org.example.dtos.TransactionResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

  @Mapping(source = "category.id", target = "categoryId")
  @Mapping(source = "account.id", target = "accountId")
  @Mapping(target = "creditCardId", ignore = true)
  @Mapping(target = "dueDate", ignore = true)
  TransactionResponseDTO toDto(AbstractTransaction entity);

  List<TransactionResponseDTO> toDtoList(List<AbstractTransaction> entities);
}