package org.example.domain.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("CARTEIRA")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountWallet extends AbstractAccount {

  public AccountWallet(Integer id, String accountName, BigDecimal balance) {
    super(id, accountName, balance);
  }

  public AccountWallet(String accountName) {
    super(accountName);
  }
}
