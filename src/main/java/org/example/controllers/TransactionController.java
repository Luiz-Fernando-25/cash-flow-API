package org.example.controllers;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.domain.enums.TransactionStatus;
import org.example.domain.enums.TransactionType;
import org.example.domain.models.AbstractTransaction;
import org.example.dtos.TransactionRequestDTO;
import org.example.dtos.TransactionResponseDTO;
import org.example.dtos.TransactionUpdateDTO;
import org.example.mappers.TransactionMapper;
import org.example.services.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

  private final TransactionService transactionService;
  private final TransactionMapper transactionMapper;

  @PostMapping
  public ResponseEntity<TransactionResponseDTO> save(
    @RequestBody TransactionRequestDTO transactionRequestDTO
  ) {
    AbstractTransaction entity;
    if (transactionRequestDTO.creditCardId() != null) {
      entity = transactionService.createCreditCardTransaction(
        transactionRequestDTO.transactionValue(),
        transactionRequestDTO.description(),
        transactionRequestDTO.date(),
        transactionRequestDTO.categoryId(),
        transactionRequestDTO.accountId(),
        transactionRequestDTO.creditCardId()
      );
    } else {
      entity = transactionService.create(
        transactionRequestDTO.transactionValue(),
        transactionRequestDTO.description(),
        transactionRequestDTO.date(),
        transactionRequestDTO.status(),
        transactionRequestDTO.categoryId(),
        transactionRequestDTO.type(),
        transactionRequestDTO.accountId()
      );
    }
    return new ResponseEntity<>(
      transactionMapper.toDto(entity),
      HttpStatus.CREATED
    );
  }

  @GetMapping(path = "/{id}")
  public ResponseEntity<TransactionResponseDTO> findById(
    @PathVariable Integer id
  ) {
    return ResponseEntity.ok(
      transactionMapper.toDto(transactionService.findById(id))
    );
  }

  @GetMapping(path = "/all")
  public ResponseEntity<List<TransactionResponseDTO>> listAll(
    @RequestParam(required = false) TransactionStatus status,
    Integer categoryId,
    TransactionType transactionType,
    Integer accountId,
    Integer cardId
  ) {
    return ResponseEntity.ok(
      transactionMapper.toDtoList(
        transactionService.searchTransactions(
          status,
          categoryId,
          transactionType,
          accountId,
          cardId
        )
      )
    );
  }

  @DeleteMapping(path = "/{id}")
  public ResponseEntity<Void> delete(@PathVariable @Valid Integer id) {
    transactionService.remove(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PatchMapping(path = "/{id}")
  public ResponseEntity<TransactionResponseDTO> replace(
    @PathVariable Integer id,
    @RequestBody TransactionUpdateDTO dto
  ) {
    AbstractTransaction updatedTransaction = transactionService.update(id, dto);
    return ResponseEntity.ok(transactionMapper.toDto(updatedTransaction));
  }

  @PatchMapping(path = "/batch")
  public ResponseEntity<List<TransactionResponseDTO>> updateBatch(
    @RequestBody List<TransactionUpdateDTO> dtos
  ) {
    List<AbstractTransaction> updatedTransactions =
      transactionService.updateBatch(dtos);
    return ResponseEntity.ok(transactionMapper.toDtoList(updatedTransactions));
  }
}
