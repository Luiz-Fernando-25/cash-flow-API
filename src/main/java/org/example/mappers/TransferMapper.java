package org.example.mappers;

import java.util.List;
import org.example.domain.models.Transfer;
import org.example.dtos.TransferResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransferMapper {
  TransferResponseDTO toDto(Transfer entity);

  List<TransferResponseDTO> toDtoList(List<Transfer> entities);
}
