package org.example.services;

import java.math.BigDecimal;
import java.util.List;
import org.example.domain.models.CreditCard;
import org.example.dtos.CreditCardUpdateDTO;

public interface CreditCardService {
  CreditCard create(
    String name,
    BigDecimal limit,
    BigDecimal balance,
    Integer closingDay,
    Integer dueDate,
    Integer bankId
  );

  CreditCard findById(Integer creditCardId);

  CreditCard update(Integer creditCardId, CreditCardUpdateDTO dto);

  void deposit(Integer creditCardId, BigDecimal value);

  void withdraw(Integer creditCardId, BigDecimal value);

  List<CreditCard> listAll();

  void remove(Integer creditCardId);
}
