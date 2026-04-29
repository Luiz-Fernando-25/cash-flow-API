package org.example.services;

import java.math.BigDecimal;
import java.util.List;
import org.example.domain.models.CreditCard;

public interface CreditCardService {
  CreditCard create(
    String name,
    BigDecimal limit,
    BigDecimal balance,
    int closingDay,
    int dueDate,
    int bankId
  );

  List<CreditCard> listAll();

  void changeName(Integer creditCardId, String name);

  void changeLimit(Integer creditCardId, BigDecimal newlimit);

  void changeBalance(Integer creditCardId, BigDecimal newBalance);

  void changeClosingDay(Integer creditCardId, int closingDay);

  void changeDueDate(Integer creditCardId, int dueDate);

  void changeBank(Integer creditCardId, int bankId);

  void remove(Integer creditCardId);
}
