package org.example.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.example.domain.enums.TransactionStatus;
import org.example.domain.enums.TransactionType;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionCreditCard extends TransactionOutput {

  @JoinColumn(name = "cartao_id")
  @ManyToOne
  private CreditCard creditCard;

  @Column(name = "data_vencimento")
  private Date dueDate;

  public TransactionCreditCard(
    BigDecimal transactionValue,
    String description,
    Date date,
    TransactionStatus status,
    Category category,
    TransactionType type,
    AbstractAccount account,
    CreditCard creditCard,
    Date dueDate
  ) {
    super(transactionValue, description, date, status, category, type, account);
    this.creditCard = creditCard;
    this.dueDate = dueDate;
  }

  public TransactionCreditCard(
    Integer id,
    BigDecimal transactionValue,
    String description,
    Date date,
    TransactionStatus status,
    Category category,
    TransactionType type,
    AbstractAccount account,
    CreditCard creditCard,
    Date dueDate
  ) {
    super(
      id,
      transactionValue,
      description,
      date,
      status,
      category,
      type,
      account
    );
    this.creditCard = creditCard;
    this.dueDate = dueDate;
  }
}
