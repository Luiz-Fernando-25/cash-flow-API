package org.example.controllers;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dtos.CreditCardRequestDTO;
import org.example.dtos.CreditCardResponseDTO;
import org.example.dtos.CreditCardUpdateDTO;
import org.example.mappers.CreditCardMapper;
import org.example.services.CreditCardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
    CreditCardResponseDTO responseDTO = creditCardMapper.toDto(
      creditCardService.create(
        creditCardRequestDTO.name(),
        creditCardRequestDTO.limit(),
        creditCardRequestDTO.balance(),
        creditCardRequestDTO.closingDay(),
        creditCardRequestDTO.dueDate(),
        creditCardRequestDTO.bankId()
      )
    );

    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
      .path("/{id}")
      .buildAndExpand(responseDTO.id())
      .toUri();

    return ResponseEntity.created(location).body(responseDTO);
  }

  @GetMapping(path = "/{id}")
  public ResponseEntity<CreditCardResponseDTO> findById(
    @PathVariable Integer id
  ) {
    return ResponseEntity.ok(
      creditCardMapper.toDto(creditCardService.findById(id))
    );
  }

  @GetMapping
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

  @PatchMapping(path = "/{id}")
  public ResponseEntity<CreditCardResponseDTO> replace(
    @PathVariable Integer id,
    @RequestBody CreditCardUpdateDTO dto
  ) {
    return ResponseEntity.ok(
      creditCardMapper.toDto(creditCardService.update(id, dto))
    );
  }
}
