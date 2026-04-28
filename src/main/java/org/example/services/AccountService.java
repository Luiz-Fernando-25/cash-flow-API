package org.example.services;

import java.math.BigDecimal;
import java.util.List;
import org.example.domain.enums.AccountType;
import org.example.domain.models.AbstractAccount;

public interface AccountService {
  AbstractAccount create(String name, AccountType type);

  void changeName(Integer accountid, String name);

  void deposit(Integer accountId, BigDecimal value);

  void withdraw(Integer accountId, BigDecimal value);

  List<AbstractAccount> searchAccounts(AccountType accountType);

  void remove(Integer accountId);
}
