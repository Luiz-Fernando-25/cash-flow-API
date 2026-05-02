package org.example.services;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.example.domain.enums.TransactionStatus;
import org.example.domain.enums.TransactionType;
import org.example.domain.models.AbstractTransaction;
import org.example.dtos.TransactionUpdateDTO;

public interface TransactionService {
  AbstractTransaction create(
    BigDecimal value,
    String description,
    Date date,
    TransactionStatus status,
    Integer categoryId,
    TransactionType transactionType,
    Integer accountId
  );

  AbstractTransaction createCreditCardTransaction(
    BigDecimal value,
    String description,
    Date date,
    Integer categoryId,
    Integer accountId,
    Integer cardId
  );

  AbstractTransaction findById(Integer transactionId);

  AbstractTransaction update(Integer transactionId, TransactionUpdateDTO dto);

  List<AbstractTransaction> searchTransactions(
    TransactionStatus status,
    Integer categoryId,
    TransactionType transactionType,
    Integer accountId,
    Integer cardId
  );

  List<AbstractTransaction> updateBatch(List<TransactionUpdateDTO> dtos);

  void remove(Integer transactionId);
}
