package org.example.domain.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "movimentacao")
@NoArgsConstructor
@AllArgsConstructor
public class Transfer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull
  @JoinColumn(name = "saida_id")
  @OneToOne
  private AbstractTransaction outputTransaction;

  @NotNull
  @JoinColumn(name = "entrada_id")
  @OneToOne
  private AbstractTransaction inputTransaction;

  public Transfer(
    AbstractTransaction outputTransaction,
    AbstractTransaction inputTransaction
  ) {
    this.outputTransaction = outputTransaction;
    this.inputTransaction = inputTransaction;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Transfer transfer = (Transfer) o;
    return Objects.equals(getId(), transfer.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }
}
