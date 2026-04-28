package org.example.controllers;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.domain.enums.AccountType;
import org.example.dtos.AccountRequestDTO;
import org.example.dtos.AccountResponseDTO;
import org.example.mappers.AccountMapper;
import org.example.services.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accountService;
  private final AccountMapper accountMapper;

  @PostMapping
  public ResponseEntity<AccountResponseDTO> save(
    @RequestBody @Valid AccountRequestDTO accountRequestDTO
  ) {
    return new ResponseEntity<>(
      accountMapper.toDto(
        accountService.create(
          accountRequestDTO.name(),
          accountRequestDTO.type()
        )
      ),
      HttpStatus.CREATED
    );
  }

  @GetMapping(path = "/{accountType}")
  public ResponseEntity<List<AccountResponseDTO>> listAll(
    @PathVariable AccountType accountType
  ) {
    return ResponseEntity.ok(
      accountMapper.toDtoList(accountService.searchAccounts(accountType))
    );
  }
}
