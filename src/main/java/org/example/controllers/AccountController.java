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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;


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

  //quando fizer a refatoração do findall tenho q mudar o required para false
  @GetMapping
  public ResponseEntity<List<AccountResponseDTO>> listAll(
    @RequestParam(required = true) AccountType accountType
  ) {
    return ResponseEntity.ok(
      accountMapper.toDtoList(accountService.searchAccounts(accountType))
    );
  }

  @DeleteMapping(path = "/{id}")
  public ResponseEntity<Void> delete(@PathVariable @Valid Integer id) {
    accountService.remove(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PutMapping
  public ResponseEntity<Void> replace(@RequestBody @Valid AccountRequestDTO accountRequestDTO) {
    accountService.changeName(
      accountRequestDTO.id(),
      accountRequestDTO.name()
    );
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }


}
