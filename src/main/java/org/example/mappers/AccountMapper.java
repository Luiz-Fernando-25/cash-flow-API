package org.example.mappers;

import java.util.List;
import org.example.domain.models.AbstractAccount;
import org.example.dtos.AccountResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {
  @Mapping(source = "accountName", target = "name")
  AccountResponseDTO toDto(AbstractAccount entity);

  List<AccountResponseDTO> toDtoList(List<AbstractAccount> entities);
}
