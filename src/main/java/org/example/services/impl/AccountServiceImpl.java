package org.example.services.impl;

import java.math.BigDecimal;
import java.util.List;
import org.example.domain.enums.AccountType;
import org.example.domain.models.AbstractAccount;
import org.example.domain.models.AccountBank;
import org.example.domain.models.AccountWallet;
import org.example.exceptions.BusinessRuleException;
import org.example.exceptions.ResourceNotFoundException;
import org.example.repositories.AccountRepository;
import org.example.services.AccountService;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

  private final AccountRepository repoAccount;

  public AccountServiceImpl(AccountRepository repoAccount) {
    this.repoAccount = repoAccount;
  }

  @Override
  public AbstractAccount create(String name, AccountType type) {
    if (name == null || name.trim().isEmpty()) throw new BusinessRuleException(
      "O nome não pode ser vazio!"
    );
    AbstractAccount account;
    if (AccountType.BANCO == type) {
      account = new AccountBank(name);
      account.setType(AccountType.BANCO);
    } else if (AccountType.CARTEIRA == type) {
      account = new AccountWallet(name);
      account.setType(AccountType.CARTEIRA);
    } else {
      throw new BusinessRuleException(
        "O tipo de conta fornecedido é inexistente!"
      );
    }

    return repoAccount.save(account);
  }

  @Override
  public AbstractAccount findById(Integer accountId) {
    return repoAccount
      .findById(accountId)
      .orElseThrow(() ->
        new ResourceNotFoundException(
          "Conta com ID " + accountId + " não encontrada"
        )
      );
  }

  @Override
  public AbstractAccount update(Integer accountid, String name) {
    AbstractAccount account = this.findById(accountid);

    if (name == null || name.trim().isEmpty()) throw new BusinessRuleException(
      "O nome não pode ser vazio!"
    );
    account.setAccountName(name);

    return repoAccount.save(account);
  }

  @Override
  public void deposit(Integer accountId, BigDecimal value) {
    if (
      value == null || value.compareTo(BigDecimal.ZERO) <= 0
    ) throw new BusinessRuleException(
      "O valor do depósito deve ser maior que zero."
    );

    AbstractAccount account = this.findById(accountId);
    account.deposit(value);
    repoAccount.save(account);
  }

  @Override
  public void withdraw(Integer accountId, BigDecimal value) {
    if (
      value == null || value.compareTo(BigDecimal.ZERO) <= 0
    ) throw new BusinessRuleException(
      "O valor do saque deve ser maior que zero."
    );

    AbstractAccount account = this.findById(accountId);
    account.withdraw(value);
    repoAccount.save(account);
  }

  @Override
  public List<AbstractAccount> searchAccounts(AccountType accountType) {
    return repoAccount
      .findAll()
      .stream()
      .filter(a -> accountType == null || a.getType().equals(accountType))
      .toList();
  }

  @Override
  public void remove(Integer accountId) {
    repoAccount.delete(this.findById(accountId));
  }
}
