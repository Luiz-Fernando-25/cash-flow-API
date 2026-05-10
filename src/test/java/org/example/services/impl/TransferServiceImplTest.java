package org.example.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.example.domain.enums.TransactionStatus;
import org.example.domain.enums.TransactionType;
import org.example.domain.models.AccountBank;
import org.example.domain.models.TransactionInput;
import org.example.domain.models.TransactionOutput;
import org.example.domain.models.Transfer;
import org.example.exceptions.ResourceNotFoundException;
import org.example.repositories.TransferRepository;
import org.example.services.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TransferServiceImplTest {

  @Mock
  private TransferRepository transferRepository;

  @Mock
  private TransactionService transactionService;

  @InjectMocks
  private TransferServiceImpl transferService;

  @Test
  @DisplayName("Deve criar transferência com sucesso criando as duas transações")
  void shouldCreateTransferSuccessfully() {
    Date date = new Date();
    TransactionOutput output = new TransactionOutput(BigDecimal.valueOf(100), "Transferência", date, TransactionStatus.EFETIVADA, null, TransactionType.SAIDA, new AccountBank("Output"));
    output.setId(1);
    TransactionInput input = new TransactionInput(BigDecimal.valueOf(100), "Transferência", date, TransactionStatus.EFETIVADA, null, TransactionType.ENTRADA, new AccountBank("Input"));
    input.setId(2);

    when(transactionService.create(
        eq(BigDecimal.valueOf(100)), eq("Transferência"), eq(date), eq(TransactionStatus.EFETIVADA),
        eq(1), eq(TransactionType.SAIDA), eq(1), eq(null))).thenReturn(output);
        
    when(transactionService.create(
        eq(BigDecimal.valueOf(100)), eq("Transferência"), eq(date), eq(TransactionStatus.EFETIVADA),
        eq(1), eq(TransactionType.ENTRADA), eq(2), eq(null))).thenReturn(input);

    Transfer mockTransfer = new Transfer(output, input);
    when(transferRepository.save(any())).thenReturn(mockTransfer);

    Transfer transfer = transferService.create(BigDecimal.valueOf(100), date, 1, 2);

    assertNotNull(transfer);
    assertEquals(output, transfer.getOutputTransaction());
    assertEquals(input, transfer.getInputTransaction());
    verify(transferRepository, atLeastOnce()).save(any());
  }

  @Test
  @DisplayName("Deve lançar exceção ao tentar buscar transferência inexistente")
  void shouldThrowExceptionWhenFindByIdNotFound() {
    when(transferRepository.findById(1)).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(
      ResourceNotFoundException.class,
      () -> transferService.findById(1)
    );

    assertEquals("Transferencia com o ID 1 não encontrada", exception.getMessage());
  }

  @Test
  @DisplayName("Deve buscar transferência com sucesso por ID")
  void shouldFindByIdSuccessfully() {
    Transfer mockTransfer = new Transfer(
      new TransactionOutput(BigDecimal.ZERO, "", new Date(), TransactionStatus.PENDENTE, null, TransactionType.SAIDA, null), 
      new TransactionInput(BigDecimal.ZERO, "", new Date(), TransactionStatus.PENDENTE, null, TransactionType.ENTRADA, null)
    );
    when(transferRepository.findById(1)).thenReturn(Optional.of(mockTransfer));

    Transfer foundTransfer = transferService.findById(1);

    assertNotNull(foundTransfer);
  }

  @Test
  @DisplayName("Deve listar todas as transferências com sucesso")
  void shouldListAllSuccessfully() {
    List<Transfer> transfers = List.of(new Transfer(
      new TransactionOutput(BigDecimal.ZERO, "", new Date(), TransactionStatus.PENDENTE, null, TransactionType.SAIDA, null), 
      new TransactionInput(BigDecimal.ZERO, "", new Date(), TransactionStatus.PENDENTE, null, TransactionType.ENTRADA, null)
    ));
    when(transferRepository.findAll()).thenReturn(transfers);

    List<Transfer> foundTransfers = transferService.listAll();

    assertEquals(1, foundTransfers.size());
  }

  @Test
  @DisplayName("Deve atualizar transferência com sucesso chamando os dois updates de transação")
  void shouldUpdateTransferSuccessfully() {
    TransactionOutput output = new TransactionOutput(BigDecimal.ZERO, "", new Date(), TransactionStatus.PENDENTE, null, TransactionType.SAIDA, null);
    output.setId(1);
    TransactionInput input = new TransactionInput(BigDecimal.ZERO, "", new Date(), TransactionStatus.PENDENTE, null, TransactionType.ENTRADA, null);
    input.setId(2);
    Transfer mockTransfer = new Transfer(output, input);
    
    when(transferRepository.findById(1)).thenReturn(Optional.of(mockTransfer));
    when(transactionService.update(eq(1), any())).thenReturn(output);
    when(transactionService.update(eq(2), any())).thenReturn(input);

    Transfer updatedTransfer = transferService.update(1, BigDecimal.valueOf(500));

    assertNotNull(updatedTransfer);
    verify(transactionService, atLeastOnce()).update(eq(1), any());
    verify(transactionService, atLeastOnce()).update(eq(2), any());
  }

  @Test
  @DisplayName("Deve remover transferência com sucesso removendo as duas transações")
  void shouldRemoveSuccessfully() {
    TransactionOutput output = new TransactionOutput(BigDecimal.ZERO, "", new Date(), TransactionStatus.PENDENTE, null, TransactionType.SAIDA, null);
    output.setId(1);
    TransactionInput input = new TransactionInput(BigDecimal.ZERO, "", new Date(), TransactionStatus.PENDENTE, null, TransactionType.ENTRADA, null);
    input.setId(2);
    Transfer mockTransfer = new Transfer(output, input);
    
    when(transferRepository.findById(1)).thenReturn(Optional.of(mockTransfer));

    transferService.remove(1);

    verify(transactionService, atLeastOnce()).remove(1);
    verify(transactionService, atLeastOnce()).remove(2);
    verify(transferRepository, atLeastOnce()).deleteById(1);
  }

}
