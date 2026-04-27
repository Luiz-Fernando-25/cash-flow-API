package org.example.services.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.example.domain.enums.TransactionStatus;
import org.example.domain.enums.TransactionType;
import org.example.domain.models.AbstractAccount;
import org.example.domain.models.AbstractTransaction;
import org.example.domain.models.Category;
import org.example.domain.models.CreditCard;
import org.example.domain.models.TransactionCreditCard;
import org.example.domain.models.TransactionInput;
import org.example.domain.models.TransactionOutput;
import org.example.repositories.AccountRepository;
import org.example.repositories.CategoryRepository;
import org.example.repositories.CreditCardRepository;
import org.example.repositories.TransactionRepository;
import org.example.services.AccountService;
import org.example.services.TransactionService;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {

  private final TransactionRepository repoTransaction;
  private final AccountRepository repoAccount;
  private final CreditCardRepository repoCreditCard;
  private final CategoryRepository repoCategory;
  private final AccountService servAccount;

  public TransactionServiceImpl(
    TransactionRepository repoTransaction,
    AccountRepository repoAccount,
    CreditCardRepository repoCreditCard,
    CategoryRepository repoCategory,
    AccountService servAccount
  ) {
    this.repoTransaction = repoTransaction;
    this.repoAccount = repoAccount;
    this.repoCreditCard = repoCreditCard;
    this.repoCategory = repoCategory;
    this.servAccount = servAccount;
  }

  private void validateBasicData(
    BigDecimal value,
    String description,
    Date date
  ) {
    if (
      value == null || value.compareTo(BigDecimal.ZERO) <= 0
    ) throw new RuntimeException(
      "O Valor da transação tem que ser um valor positovo e não nulo!"
    );
    if (
      description == null || description.trim().isEmpty()
    ) throw new RuntimeException("A descrição tem que ter um valor valido!");
    if (date == null) date = new Date();
  }

  private Category validateCategory(Integer categoryId) {
    Category category = repoCategory
      .findById(categoryId)
      .orElseThrow(() ->
        new RuntimeException("O id da categoria não foi encontrado.")
      );
    return category;
  }

  private AbstractAccount validateAccount(Integer accountId) {
    AbstractAccount account = repoAccount
      .findById(accountId)
      .orElseThrow(() ->
        new RuntimeException("O id da conta não foi encontrado.")
      );
    return account;
  }

  private CreditCard validateCreditCard(Integer creditCardId) {
    CreditCard creditCard = repoCreditCard
      .findById(creditCardId)
      .orElseThrow(() ->
        new RuntimeException("O id da conta não foi encontrado.")
      );
    return creditCard;
  }

  @Override
  public AbstractTransaction create(
    BigDecimal value,
    String description,
    Date date,
    TransactionStatus status,
    Integer categoryId,
    TransactionType transactionType,
    Integer accountId
  ) {
    validateBasicData(value, description, date);
    Category category = validateCategory(categoryId);
    AbstractAccount account = validateAccount(accountId);

    AbstractTransaction transaction;
    if (TransactionType.ENTRADA == transactionType) {
      transaction = new TransactionInput(
        value,
        description,
        date,
        TransactionStatus.PENDENTE,
        category,
        transactionType,
        account
      );
    } else if (TransactionType.SAIDA == transactionType) {
      transaction = new TransactionOutput(
        value,
        description,
        date,
        TransactionStatus.PENDENTE,
        category,
        transactionType,
        account
      );
    } else {
      throw new RuntimeException("O tipo de transação não é um tipo valido!");
    }
    if (TransactionStatus.EFETIVADA == status) {
      transaction.setStatus(status);
      if (TransactionType.ENTRADA == transactionType) {
        servAccount.deposit(accountId, value);
      } else {
        servAccount.withdraw(accountId, value);
      }
    }
    repoTransaction.save(transaction);
    return transaction;
  }

  @Override
  public void createCreditCardTransaction(
    BigDecimal value,
    String description,
    Date date,
    Integer categoryId,
    Integer accountId,
    Integer cardId
  ) {
    validateBasicData(value, description, date);
    Category category = validateCategory(categoryId);
    AbstractAccount account = validateAccount(accountId);
    CreditCard creditCard = validateCreditCard(cardId);

    Date todayDate = new Date();
    Calendar cal = Calendar.getInstance();
    cal.setTime(todayDate);
    int todayDay = cal.get(Calendar.DAY_OF_MONTH);
    if (todayDay > creditCard.getClosingDay()) {
      cal.add(Calendar.MONTH, 1);
    }
    todayDate = cal.getTime();

    AbstractTransaction transaction = new TransactionCreditCard(
      value,
      description,
      date,
      TransactionStatus.PENDENTE,
      category,
      TransactionType.SAIDA,
      account,
      creditCard,
      todayDate
    );

    repoTransaction.save(transaction);
  }

  @Override
  public List<AbstractTransaction> searchTransactions(
    TransactionStatus status,
    Integer categoryId,
    TransactionType transactionType,
    Integer accountId,
    Integer cardId
  ) {
    return repoTransaction
      .findAll()
      .stream()
      .filter(st -> status == null || st.getStatus() == status)
      .filter(
        cId ->
          categoryId == null || cId.getCategory().getId().equals(categoryId)
      )
      .filter(
        cT -> transactionType == null || cT.getType().equals(transactionType)
      )
      .filter(
        ac -> accountId == null || ac.getAccount().getId().equals(accountId)
      )
      .filter(
        t ->
          cardId == null ||
          (t instanceof TransactionCreditCard tc &&
            tc.getCreditCard().getId().equals(cardId))
      )
      .toList();
  }

  @Override
  public void changeValue(Integer transactionId, BigDecimal value) {
    AbstractTransaction transaction = repoTransaction
      .findById(transactionId)
      .orElseThrow(() -> new RuntimeException("O id de transação é invalido"));
    if (
      value == null || value.compareTo(BigDecimal.ZERO) <= 0
    ) throw new RuntimeException(
      "O Valor da transação tem que ser um valor positovo e não nulo!"
    );
    if (transaction.getStatus() == TransactionStatus.EFETIVADA) {
      changeStatus(transactionId, TransactionStatus.PENDENTE);
      transaction.setTransactionValue(value);
      transaction.setStatus(TransactionStatus.PENDENTE);
      repoTransaction.save(transaction);
      changeStatus(transactionId, TransactionStatus.EFETIVADA);
    } else {
      transaction.setTransactionValue(value);
      repoTransaction.save(transaction);
    }
  }

  @Override
  public void changeDescription(Integer transactionId, String description) {
    AbstractTransaction transaction = repoTransaction
      .findById(transactionId)
      .orElseThrow(() -> new RuntimeException("O id de transação é invalido"));
    if (
      description == null || description.trim().isEmpty()
    ) throw new RuntimeException("A descrição tem que ter um valor valido!");

    transaction.setDescription(description);
    repoTransaction.save(transaction);
  }

  @Override
  public void changeDate(Integer transactionId, Date date) {
    AbstractTransaction transaction = repoTransaction
      .findById(transactionId)
      .orElseThrow(() -> new RuntimeException("O id de transação é invalido"));
    if (date == null) date = new Date();
    transaction.setDate(date);
    repoTransaction.save(transaction);
  }

  @Override
  public void changeStatus(Integer transactionId, TransactionStatus status) {
    AbstractTransaction transaction = repoTransaction
      .findById(transactionId)
      .orElseThrow(() -> new RuntimeException("O id de transação é invalido"));
    if (transaction.getStatus() == status) return;
    if (TransactionStatus.EFETIVADA == status) {
      if (TransactionType.ENTRADA == transaction.getType()) {
        servAccount.deposit(
          transaction.getAccount().getId(),
          transaction.getTransactionValue()
        );
      } else {
        servAccount.withdraw(
          transaction.getAccount().getId(),
          transaction.getTransactionValue()
        );
      }
    } else {
      if (TransactionType.ENTRADA == transaction.getType()) {
        servAccount.withdraw(
          transaction.getAccount().getId(),
          transaction.getTransactionValue()
        );
      } else {
        servAccount.deposit(
          transaction.getAccount().getId(),
          transaction.getTransactionValue()
        );
      }
    }
    transaction.setStatus(status);
    repoTransaction.save(transaction);
  }

  @Override
  public void changeCategory(Integer transactionId, Integer categoryId) {
    AbstractTransaction transaction = repoTransaction
      .findById(transactionId)
      .orElseThrow(() -> new RuntimeException("O id de transação é invalido"));
    Category category = validateCategory(categoryId);
    transaction.setCategory(category);
    repoTransaction.save(transaction);
  }

  @Override
  public void changeAccount(Integer transactionId, Integer accountId) {
    AbstractTransaction transaction = repoTransaction
      .findById(transactionId)
      .orElseThrow(() -> new RuntimeException("O id de transação é invalido"));
    AbstractAccount account = validateAccount(accountId);
    transaction.setAccount(account);
    repoTransaction.save(transaction);
  }

  @Override
  public void changeCreditCard(Integer transactionId, Integer creditCardId) {
    AbstractTransaction transaction = repoTransaction
      .findById(transactionId)
      .orElseThrow(() -> new RuntimeException("O id de transação é invalido"));
    CreditCard creditCard = validateCreditCard(creditCardId);
    if (transaction instanceof TransactionCreditCard transactionCreditCard) {
      transactionCreditCard.setCreditCard(creditCard);
      repoTransaction.save(transactionCreditCard);
    }
  }

  @Override
  public void changeDateBuy(Integer transactionId, Date dateBuy) {
    AbstractTransaction transaction = repoTransaction
      .findById(transactionId)
      .orElseThrow(() -> new RuntimeException("O id de transação é invalido"));
    if (transaction instanceof TransactionCreditCard transactionCreditCard) {
      transactionCreditCard.setDueDate(dateBuy);
      repoTransaction.save(transactionCreditCard);
    }
  }

  @Override
  public void remove(Integer transactionId) {
    if (transactionId == null || transactionId < 0) throw new RuntimeException(
      "Transação não encontrada para o ID informado."
    );
    changeStatus(transactionId, TransactionStatus.PENDENTE);
    repoTransaction.deleteById(transactionId);
  }
}
