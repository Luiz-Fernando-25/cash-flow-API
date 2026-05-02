package org.example.services.impl;

import jakarta.transaction.Transactional;
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
import org.example.dtos.TransactionUpdateDTO;
import org.example.exceptions.BusinessRuleException;
import org.example.exceptions.ResourceNotFoundException;
import org.example.repositories.AccountRepository;
import org.example.repositories.CategoryRepository;
import org.example.repositories.CreditCardRepository;
import org.example.repositories.TransactionRepository;
import org.example.services.AccountService;
import org.example.services.CreditCardService;
import org.example.services.TransactionService;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {

  private final TransactionRepository repoTransaction;
  private final AccountRepository repoAccount;
  private final CreditCardRepository repoCreditCard;
  private final CategoryRepository repoCategory;
  private final AccountService servAccount;
  private final CreditCardService servCreditCard;

  public TransactionServiceImpl(
    TransactionRepository repoTransaction,
    AccountRepository repoAccount,
    CreditCardRepository repoCreditCard,
    CategoryRepository repoCategory,
    AccountService servAccount,
    CreditCardService servCreditCard
  ) {
    this.repoTransaction = repoTransaction;
    this.repoAccount = repoAccount;
    this.repoCreditCard = repoCreditCard;
    this.repoCategory = repoCategory;
    this.servAccount = servAccount;
    this.servCreditCard = servCreditCard;
  }

  private void validateBasicData(
    BigDecimal value,
    String description,
    Date date
  ) {
    if (
      value == null || value.compareTo(BigDecimal.ZERO) <= 0
    ) throw new BusinessRuleException(
      "O Valor da transação tem que ser um valor positovo e não nulo!"
    );
    if (
      description == null || description.trim().isEmpty()
    ) throw new BusinessRuleException(
      "A descrição tem que ter um valor valido!"
    );
    if (date == null) date = new Date();
  }

  private Category validateCategory(Integer categoryId) {
    Category category = repoCategory
      .findById(categoryId)
      .orElseThrow(() ->
        new ResourceNotFoundException(
          "A categoria com o Id " + categoryId + " não foi encontrada."
        )
      );
    return category;
  }

  private AbstractAccount validateAccount(Integer accountId) {
    AbstractAccount account = repoAccount
      .findById(accountId)
      .orElseThrow(() ->
        new ResourceNotFoundException(
          "A conta com o Id " + accountId + " não foi encontrada."
        )
      );
    return account;
  }

  private CreditCard validateCreditCard(Integer creditCardId) {
    CreditCard creditCard = repoCreditCard
      .findById(creditCardId)
      .orElseThrow(() ->
        new ResourceNotFoundException(
          "O Cartão de crédito com o ID " +
            creditCardId +
            " não foi encontrado."
        )
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
      transaction.setType(TransactionType.ENTRADA);
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
      transaction.setType(TransactionType.SAIDA);
    } else {
      throw new BusinessRuleException(
        "O tipo de transação não é um tipo valido!"
      );
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
  public AbstractTransaction createCreditCardTransaction(
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

    applyCreditCard(transaction);
    return transaction;
  }

  @Override
  public AbstractTransaction findById(Integer transactionId) {
    return repoTransaction
      .findById(transactionId)
      .orElseThrow(() ->
        new ResourceNotFoundException(
          "Transação com ID " + transactionId + " não encontrada"
        )
      );
  }

  @Override
  @Transactional
  public AbstractTransaction update(
    Integer transactionId,
    TransactionUpdateDTO dto
  ) {
    AbstractTransaction transaction = this.findById(transactionId);

    if (transaction.getStatus() == TransactionStatus.EFETIVADA) {
      revertFinancials(transaction);
    } else {
      revertCreditCard(transaction);
    }

    if (transaction instanceof TransactionCreditCard tcc) {
      if (dto.creditCardId() != null) tcc.setCreditCard(
        servCreditCard.findById(dto.creditCardId())
      );
      if (dto.dueDate() != null) tcc.setDueDate(dto.dueDate());
      transaction = tcc;
    }

    if (dto.transactionValue() != null) {
      if (
        dto.transactionValue().compareTo(BigDecimal.ZERO) <= 0
      ) throw new BusinessRuleException(
        "O valor da transação deve ser positivo"
      );

      transaction.setTransactionValue(dto.transactionValue());
    }

    if (dto.description() != null && !dto.description().trim().isEmpty()) {
      transaction.setDescription(dto.description());
    }

    if (dto.date() != null) {
      transaction.setDate(dto.date());
    }

    if (dto.categoryId() != null) {
      Category category = validateCategory(dto.categoryId());
      transaction.setCategory(category);
    }

    if (dto.accountId() != null) {
      transaction.setAccount(servAccount.findById(dto.accountId()));
    }

    TransactionStatus finalStatus =
      dto.status() != null ? dto.status() : transaction.getStatus();

    if (finalStatus == TransactionStatus.EFETIVADA) {
      applyFinancials(transaction);
      transaction.setStatus(TransactionStatus.EFETIVADA);
    } else {
      transaction.setStatus(TransactionStatus.PENDENTE);
      applyCreditCard(transaction);
    }

    return repoTransaction.save(transaction);
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

  private void applyFinancials(AbstractTransaction transaction) {
    if (transaction.getType() == TransactionType.ENTRADA) {
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
  }

  private void revertFinancials(AbstractTransaction transaction) {
    if (transaction.getType() == TransactionType.ENTRADA) {
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

  private void applyCreditCard(AbstractTransaction transaction) {
    if (transaction instanceof TransactionCreditCard tcc) {
      servCreditCard.deposit(
        tcc.getCreditCard().getId(),
        tcc.getTransactionValue()
      );
    }
  }

  private void revertCreditCard(AbstractTransaction transaction) {
    if (transaction instanceof TransactionCreditCard tcc) {
      servCreditCard.withdraw(
        tcc.getCreditCard().getId(),
        tcc.getTransactionValue()
      );
    }
  }

  @Override
  @Transactional
  public List<AbstractTransaction> updateBatch(
    List<TransactionUpdateDTO> dtos
  ) {
    return dtos
      .stream()
      .map(dto -> this.update(dto.id(), dto))
      .toList();
  }

  @Override
  public void remove(Integer transactionId) {
    AbstractTransaction transaction = this.findById(transactionId);
    if (transaction.getStatus() == TransactionStatus.EFETIVADA) {
      revertFinancials(transaction);
    } else {
      revertCreditCard(transaction);
    }
    repoTransaction.delete(transaction);
  }
}
