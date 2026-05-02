package org.example.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.example.exceptions.BusinessRuleException;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
//@Builder
@Entity
@Table(name = "cartaocredito")
public class CreditCard {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank
  @Column(name = "nome")
  private String name;

  @Min(0)
  @Column(name = "limite")
  private BigDecimal limit = BigDecimal.ZERO;

  @Min(0)
  @Column(name = "saldo")
  private BigDecimal balance = BigDecimal.ZERO;

  @NotNull
  @Min(1)
  @Max(28)
  @Column(name = "dia_fechamento")
  private int closingDay;

  @NotNull
  @Min(1)
  @Max(28)
  @Column(name = "dia_vencimento")
  private int dueDate;

  @NotNull
  @JoinColumn(name = "conta_id")
  @ManyToOne
  private AccountBank bank;

  public CreditCard(
    String name,
    BigDecimal limit,
    BigDecimal balance,
    int closingDay,
    int dueDate,
    AccountBank bank
  ) {
    this.name = name;
    this.limit = limit;
    this.balance = balance;
    this.closingDay = closingDay;
    this.dueDate = dueDate;
    this.bank = bank;
  }

  public CreditCard(String name, AccountBank bank) {
    this.name = name;
    this.bank = bank;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    CreditCard that = (CreditCard) o;
    return Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }

  public void deposit(BigDecimal value) {
    if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
      throw new BusinessRuleException(
        "O valor informado deve ser maior que zero."
      );
    }
    this.balance = this.balance.add(value);
  }

  public void withdraw(BigDecimal value) {
    if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
      throw new BusinessRuleException(
        "O valor informado deve ser maior que zero."
      );
    }
    this.balance = this.balance.subtract(value);
  }
}
