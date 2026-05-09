package org.example.mappers;

import java.util.List;
import org.example.domain.models.CreditCard;
import org.example.dtos.CreditCardResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CreditCardMapper {
  @Mapping(source = "bank.id", target = "bankId")
  CreditCardResponseDTO toDto(CreditCard entity);

  List<CreditCardResponseDTO> toDtoList(List<CreditCard> entities);
}
