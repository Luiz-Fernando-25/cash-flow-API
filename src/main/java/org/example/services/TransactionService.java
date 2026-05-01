package org.example.services;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.example.domain.enums.TransactionStatus;
import org.example.domain.enums.TransactionType;
import org.example.domain.models.AbstractTransaction;

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

  List<AbstractTransaction> searchTransactions(
    TransactionStatus status,
    Integer categoryId,
    TransactionType transactionType,
    Integer accountId,
    Integer cardId
  );

  void changeValue(Integer transactionId, BigDecimal value);

  void changeDescription(Integer transactionId, String description);

  void changeDate(Integer transactionId, Date date);

  void changeStatus(Integer transactionId, TransactionStatus status);

  void changeCategory(Integer transactionId, Integer categoryId);

  void changeAccount(Integer transactionId, Integer accountId);

  void changeCreditCard(Integer transactionId, Integer creditCardId);

  void changeDateBuy(Integer transactionId, Date dateBuy);

  void remove(Integer transactionId);
}
