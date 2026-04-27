package org.example.domain.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("BANCO")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountBank extends AbstractAccount {

  public AccountBank(Integer id, String accountName, BigDecimal balance) {
    super(id, accountName, balance);
  }

  public AccountBank(String accountName) {
    super(accountName);
  }
}
