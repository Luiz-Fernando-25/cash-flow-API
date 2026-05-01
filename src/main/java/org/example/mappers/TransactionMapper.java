package org.example.mappers;

import java.util.List;
import org.example.domain.models.AbstractTransaction;
import org.example.dtos.TransactionResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
  TransactionResponseDTO toDto(AbstractTransaction entity);

  List<TransactionResponseDTO> toDtoList(List<AbstractTransaction> entities);
}
