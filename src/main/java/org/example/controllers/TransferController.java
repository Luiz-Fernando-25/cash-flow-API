package org.example.controllers;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dtos.TransferRequestDTO;
import org.example.dtos.TransferResponseDTO;
import org.example.mappers.TransferMapper;
import org.example.services.TransferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
public class TransferController {

  private final TransferService transferService;
  private final TransferMapper transferMapper;

  @PostMapping
  public ResponseEntity<TransferResponseDTO> save(
    @RequestBody @Valid TransferRequestDTO transferRequestDTO
  ) {
    return new ResponseEntity<>(
      transferMapper.toDto(
        transferService.create(
          transferRequestDTO.value(),
          transferRequestDTO.date(),
          transferRequestDTO.accOutputId(),
          transferRequestDTO.accInputId()
        )
      ),
      HttpStatus.CREATED
    );
  }

  @GetMapping(path = "/all")
  public ResponseEntity<List<TransferResponseDTO>> listAll() {
    return ResponseEntity.ok(
      transferMapper.toDtoList(transferService.listAll())
    );
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable @Valid Integer id) {
    transferService.remove(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
