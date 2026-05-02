package org.example.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.example.domain.enums.AccountType;
import org.example.domain.interfaces.Account;
import org.example.exceptions.BusinessRuleException;

//@AllArgsConstructor
//@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "conta")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
  name = "tipo_conta",
  discriminatorType = DiscriminatorType.STRING
)
public abstract class AbstractAccount implements Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Integer id;

  @NotBlank
  @Column(name = "nome")
  protected String accountName;

  @Column(name = "saldo_atual")
  protected BigDecimal balance = BigDecimal.ZERO;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_conta", insertable = false, updatable = false)
  protected AccountType type;

  public AbstractAccount(Integer id, String accountName, BigDecimal balance) {
    this.id = id;
    this.accountName = accountName;
    this.balance = balance;
  }

  public AbstractAccount(String accountName) {
    this.accountName = accountName;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    AbstractAccount that = (AbstractAccount) o;
    return Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }

  @Override
  public void deposit(BigDecimal value) {
    if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
      throw new BusinessRuleException(
        "O valor do depósito deve ser maior que zero."
      );
    }
    this.balance = this.balance.add(value);
  }

  @Override
  public void withdraw(BigDecimal value) {
    if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
      throw new BusinessRuleException(
        "O valor do saque deve ser maior que zero."
      );
    }
    this.balance = this.balance.subtract(value);
  }
}
