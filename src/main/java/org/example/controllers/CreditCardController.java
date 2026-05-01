package org.example.controllers;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dtos.CreditCardRequestDTO;
import org.example.dtos.CreditCardResponseDTO;
import org.example.mappers.CreditCardMapper;
import org.example.services.CreditCardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/credit-cards")
@RequiredArgsConstructor
public class CreditCardController {

  private final CreditCardService creditCardService;
  private final CreditCardMapper creditCardMapper;

  @PostMapping
  public ResponseEntity<CreditCardResponseDTO> save(
    @RequestBody @Valid CreditCardRequestDTO creditCardRequestDTO
  ) {
    return new ResponseEntity<>(
      creditCardMapper.toDto(
        creditCardService.create(
          creditCardRequestDTO.name(),
          creditCardRequestDTO.limit(),
          creditCardRequestDTO.balance(),
          creditCardRequestDTO.closingDay(),
          creditCardRequestDTO.dueDate(),
          creditCardRequestDTO.bankId()
        )
      ),
      HttpStatus.CREATED
    );
  }

  @GetMapping(path = "/all")
  public ResponseEntity<List<CreditCardResponseDTO>> listAll() {
    return ResponseEntity.ok(
      creditCardMapper.toDtoList(creditCardService.listAll())
    );
  }

  @DeleteMapping(path = "/{id}")
  public ResponseEntity<Void> delete(@PathVariable @Valid Integer id) {
    creditCardService.remove(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  //Esse metodo sera alterado no futuro quando fizer a refatoração do service
  @PutMapping
  public ResponseEntity<Void> replace(
    @RequestBody CreditCardRequestDTO creditCardRequestDTO
  ) {
    creditCardService.changeName(
      creditCardRequestDTO.id(),
      creditCardRequestDTO.name()
    );
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
