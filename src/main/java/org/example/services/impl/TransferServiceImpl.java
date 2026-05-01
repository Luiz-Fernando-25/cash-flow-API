package org.example.services.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.example.domain.enums.TransactionStatus;
import org.example.domain.enums.TransactionType;
import org.example.domain.models.AbstractTransaction;
import org.example.domain.models.Transfer;
import org.example.repositories.TransferRepository;
import org.example.services.TransactionService;
import org.example.services.TransferService;
import org.springframework.stereotype.Service;

@Service
public class TransferServiceImpl implements TransferService {

  private final TransferRepository repoTransfer;
  private final TransactionService servTransaction;

  public TransferServiceImpl(
    TransferRepository repoTransfer,
    TransactionService servTransaction
  ) {
    this.repoTransfer = repoTransfer;
    this.servTransaction = servTransaction;
  }

  @Override
  public Transfer create(
    BigDecimal value,
    Date dataHoje,
    Integer accOutputId,
    Integer accInputId
  ) {
    AbstractTransaction transactionOutput = servTransaction.create(
      value,
      "Transferência",
      dataHoje,
      TransactionStatus.EFETIVADA,
      1,
      TransactionType.SAIDA,
      accOutputId
    );
    AbstractTransaction transactionInput = servTransaction.create(
      value,
      "Transferência",
      dataHoje,
      TransactionStatus.EFETIVADA,
      1,
      TransactionType.ENTRADA,
      accInputId
    );
    Transfer transfer = new Transfer(transactionOutput, transactionInput);
    repoTransfer.save(transfer);
    return transfer;
  }

  @Override
  public List<Transfer> listAll() {
    List<Transfer> transfers = repoTransfer.findAll();
    return transfers;
  }

  @Override
  public void remove(Integer transferId) {
    Transfer transfer = repoTransfer
      .findById(transferId)
      .orElseThrow(() ->
        new RuntimeException(
          "Transferencia não encontrada para o ID informado."
        )
      );
    servTransaction.remove(transfer.getOutputTransaction().getId());
    servTransaction.remove(transfer.getInputTransaction().getId());
    repoTransfer.deleteById(transferId);
  }
}
