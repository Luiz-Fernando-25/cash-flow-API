package org.example.services.impl;

import java.math.BigDecimal;
import java.util.List;
import org.example.domain.models.AbstractAccount;
import org.example.domain.models.AccountBank;
import org.example.domain.models.CreditCard;
import org.example.repositories.AccountRepository;
import org.example.repositories.CreditCardRepository;
import org.example.services.CreditCardService;
import org.springframework.stereotype.Service;

@Service
public class CreditCardServiceImpl implements CreditCardService {

  private final CreditCardRepository repoCreditCard;
  private final AccountRepository repoAccount;

  public CreditCardServiceImpl(
    CreditCardRepository repoCreditCard,
    AccountRepository repoAccount
  ) {
    this.repoCreditCard = repoCreditCard;
    this.repoAccount = repoAccount;
  }

  @Override
  public void create(
    String name,
    BigDecimal limit,
    BigDecimal balance,
    int closingDay,
    int dueDate,
    int bankId
  ) {
    if (name == null || name.trim().isEmpty()) throw new RuntimeException(
      "O nome não pode ser vazio!"
    );
    if (limit == null) {
      limit = BigDecimal.ZERO;
    } else if (limit.compareTo(BigDecimal.ZERO) < 0) {
      throw new RuntimeException("O limite não pode ser negativo.");
    }
    if (balance == null) {
      balance = BigDecimal.ZERO;
    } else if (balance.compareTo(BigDecimal.ZERO) < 0) {
      throw new RuntimeException(
        "O valor do saldo do cartão não pode ser negativo."
      );
    }
    validateDay(closingDay);
    validateDay(dueDate);
    AbstractAccount account = repoAccount
      .findById(bankId)
      .orElseThrow(() ->
        new RuntimeException("O id da banco não foi encontrado.")
      );
    if (!(account instanceof AccountBank bank)) throw new RuntimeException(
      "A conta informada não é um banco."
    );
    CreditCard creditCard = new CreditCard(
      name,
      limit,
      balance,
      closingDay,
      dueDate,
      bank
    );
    repoCreditCard.save(creditCard);
  }

  private void validateDay(int day) {
    if (!(day > 0 && day <= 28)) throw new RuntimeException(
      "O dia tem que ser um nomero entre 1 e 28"
    );
  }

  @Override
  public List<CreditCard> listAll() {
    List<CreditCard> creditCards = repoCreditCard.findAll();
    return creditCards;
  }

  @Override
  public void changeName(Integer creditCardId, String name) {
    CreditCard creditCard = repoCreditCard
      .findById(creditCardId)
      .orElseThrow(() ->
        new RuntimeException("Id do cartão de credito não encontrada")
      );
    if (name == null || name.trim().isEmpty()) throw new RuntimeException(
      "O nome não pode ser vazio!"
    );
    creditCard.setName(name);
    repoCreditCard.save(creditCard);
  }

  @Override
  public void changeLimit(Integer creditCardId, BigDecimal newLimit) {
    CreditCard creditCard = repoCreditCard
      .findById(creditCardId)
      .orElseThrow(() ->
        new RuntimeException("Id do cartão de credito não encontrada")
      );
    if (newLimit == null) {
      newLimit = BigDecimal.ZERO;
    } else if (newLimit.compareTo(BigDecimal.ZERO) < 0) {
      throw new RuntimeException("O limite não pode ser negativo.");
    }
    creditCard.setLimit(newLimit);
    repoCreditCard.save(creditCard);
  }

  @Override
  public void changeBalance(Integer creditCardId, BigDecimal newBalance) {
    CreditCard creditCard = repoCreditCard
      .findById(creditCardId)
      .orElseThrow(() ->
        new RuntimeException("Id do cartão de credito não encontrada")
      );
    if (newBalance == null) {
      newBalance = BigDecimal.ZERO;
    } else if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
      throw new RuntimeException(
        "O valor do saldo do cartão não pode ser negativo."
      );
    }
    creditCard.setBalance(newBalance);
    repoCreditCard.save(creditCard);
  }

  @Override
  public void changeClosingDay(Integer creditCardId, int closingDay) {
    CreditCard creditCard = repoCreditCard
      .findById(creditCardId)
      .orElseThrow(() ->
        new RuntimeException("Id do cartão de credito não encontrada")
      );
    validateDay(closingDay);
    creditCard.setClosingDay(closingDay);
    repoCreditCard.save(creditCard);
  }

  @Override
  public void changeDueDate(Integer creditCardId, int dueDate) {
    CreditCard creditCard = repoCreditCard
      .findById(creditCardId)
      .orElseThrow(() ->
        new RuntimeException("Id do cartão de credito não encontrada")
      );
    validateDay(dueDate);
    creditCard.setDueDate(dueDate);
    repoCreditCard.save(creditCard);
  }

  @Override
  public void changeBank(Integer creditCardId, int bankId) {
    CreditCard creditCard = repoCreditCard
      .findById(creditCardId)
      .orElseThrow(() ->
        new RuntimeException("Id do cartão de credito não encontrada")
      );
    AbstractAccount account = repoAccount
      .findById(bankId)
      .orElseThrow(() ->
        new RuntimeException("O id da banco não foi encontrado.")
      );
    if (!(account instanceof AccountBank bank)) throw new RuntimeException(
      "A conta informada não é um banco."
    );
    creditCard.setBank(bank);
    repoCreditCard.save(creditCard);
  }

  @Override
  public void remove(Integer creditCardId) {
    if (creditCardId == null || creditCardId < 0) throw new RuntimeException(
      "Cartão de credito não encontrada para o ID informado."
    );
    repoCreditCard.deleteById(creditCardId);
  }
}
