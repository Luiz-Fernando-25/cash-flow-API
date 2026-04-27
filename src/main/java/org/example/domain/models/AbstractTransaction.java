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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.example.domain.enums.TransactionStatus;
import org.example.domain.enums.TransactionType;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "transacao")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
  name = "tipo_transacao",
  discriminatorType = DiscriminatorType.STRING
)
public abstract class AbstractTransaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Integer id;

  @NotNull
  @Min(0)
  @Column(name = "valor")
  protected BigDecimal transactionValue;

  @NotBlank
  @Column(name = "descricao")
  protected String description;

  @NotNull
  @Column(name = "data")
  protected Date date;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  protected TransactionStatus status;

  @NotNull
  @JoinColumn(name = "categoria_id")
  @ManyToOne
  protected Category category;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_transacao", insertable = false, updatable = false)
  protected TransactionType type;

  @NotNull
  @JoinColumn(name = "conta_id")
  @ManyToOne
  protected AbstractAccount account;

  public AbstractTransaction(
    BigDecimal transactionValue,
    String description,
    Date date,
    TransactionStatus status,
    Category category,
    TransactionType type,
    AbstractAccount account
  ) {
    this.transactionValue = transactionValue;
    this.description = description;
    this.date = date;
    this.status = status;
    this.category = category;
    this.type = type;
    this.account = account;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    AbstractTransaction that = (AbstractTransaction) o;
    return Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }
}
