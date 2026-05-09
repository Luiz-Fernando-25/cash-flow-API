package org.example.services.impl;

import java.math.BigDecimal;
import java.util.List;
import org.example.domain.models.AbstractAccount;
import org.example.domain.models.AccountBank;
import org.example.domain.models.CreditCard;
import org.example.dtos.CreditCardUpdateDTO;
import org.example.exceptions.BusinessRuleException;
import org.example.exceptions.ResourceNotFoundException;
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
  public CreditCard create(
    String name,
    BigDecimal limit,
    BigDecimal balance,
    Integer closingDay,
    Integer dueDate,
    Integer bankId
  ) {
    if (name == null || name.trim().isEmpty()) throw new BusinessRuleException(
      "O nome não pode ser vazio!"
    );

    validateDay(closingDay);
    validateDay(dueDate);

    AbstractAccount account = repoAccount
      .findById(bankId)
      .orElseThrow(() ->
        new BusinessRuleException("Banco com ID" + bankId + " não encontrado")
      );
    if (!(account instanceof AccountBank bank)) throw new BusinessRuleException(
      "A conta informada não é um banco."
    );

    CreditCard creditCard = new CreditCard(
      name,
      limit == null ? BigDecimal.ZERO : limit,
      balance == null ? BigDecimal.ZERO : balance,
      closingDay,
      dueDate,
      bank
    );

    return repoCreditCard.save(creditCard);
  }

  private void validateDay(Integer day) {
    if (day == null || !(day > 0 && day <= 28)) throw new BusinessRuleException(
      "O dia tem que ser um número entre 1 e 28"
    );
  }

  @Override
  public CreditCard findById(Integer creditCardId) {
    return repoCreditCard
      .findById(creditCardId)
      .orElseThrow(() ->
        new ResourceNotFoundException(
          "Cartão com ID " + creditCardId + " não encontrado"
        )
      );
  }

  @Override
  public List<CreditCard> listAll() {
    return repoCreditCard.findAll();
  }

  @Override
  public void deposit(Integer creditCardId, BigDecimal value) {
    if (
      value == null || value.compareTo(BigDecimal.ZERO) <= 0
    ) throw new BusinessRuleException("Valor deve ser positivo.");
    CreditCard creditCard = this.findById(creditCardId);
    creditCard.deposit(value);
    repoCreditCard.save(creditCard);
  }

  @Override
  public void withdraw(Integer creditCardId, BigDecimal value) {
    if (
      value == null || value.compareTo(BigDecimal.ZERO) <= 0
    ) throw new BusinessRuleException("Valor deve ser positivo.");
    CreditCard creditCard = this.findById(creditCardId);
    creditCard.withdraw(value);
    repoCreditCard.save(creditCard);
  }

  @Override
  public void remove(Integer creditCardId) {
    repoCreditCard.delete(this.findById(creditCardId));
  }

  @Override
  public CreditCard update(Integer creditCardId, CreditCardUpdateDTO dto) {
    CreditCard creditCard = this.findById(creditCardId);

    if (dto.name() != null) creditCard.setName(dto.name());

    if (dto.limit() != null) {
      if (
        dto.limit().compareTo(BigDecimal.ZERO) < 0
      ) throw new BusinessRuleException("O limite não pode ser negativo");
      creditCard.setLimit(dto.limit());
    }

    if (dto.closingDay() != null) {
      validateDay(dto.closingDay());
      creditCard.setClosingDay(dto.closingDay());
    }

    if (dto.dueDate() != null) {
      validateDay(dto.dueDate());
      creditCard.setDueDate(dto.dueDate());
    }

    if (dto.bankId() != null) {
      AbstractAccount account = repoAccount
        .findById(dto.bankId())
        .orElseThrow(() ->
          new BusinessRuleException(
            "Banco com ID " + dto.bankId() + " não encontrado"
          )
        );
      if (account instanceof AccountBank bank) creditCard.setBank(bank);
    }

    return repoCreditCard.save(creditCard);
  }
}
