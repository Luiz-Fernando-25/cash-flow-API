package org.example.mappers;

import java.util.List;

import org.example.domain.models.CreditCard;
import org.example.dtos.CreditCardResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreditCardMapper {
    CreditCardResponseDTO toDto(CreditCard entity);

    List<CreditCardResponseDTO> toDtoList(List<CreditCard> entities);
}
