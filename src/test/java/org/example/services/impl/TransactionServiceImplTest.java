package org.example.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.example.domain.enums.CategoryType;
import org.example.domain.enums.TransactionStatus;
import org.example.domain.enums.TransactionType;
import org.example.domain.models.AbstractAccount;
import org.example.domain.models.AbstractTransaction;
import org.example.domain.models.AccountBank;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

  @Mock
  private TransactionRepository repoTransaction;
  @Mock
  private AccountRepository repoAccount;
  @Mock
  private CreditCardRepository repoCreditCard;
  @Mock
  private CategoryRepository repoCategory;
  @Mock
  private AccountService servAccount;
  @Mock
  private CreditCardService servCreditCard;

  @InjectMocks
  private TransactionServiceImpl transactionService;

  @Test
  @DisplayName("Deve criar transação normal de entrada pendente com sucesso")
  void shouldCreateNormalTransactionInputPendingSuccessfully() {
    Category category = new Category("Salário", CategoryType.RECEITA);
    when(repoCategory.findById(1)).thenReturn(Optional.of(category));

    AbstractAccount account = new AccountBank("Banco");
    when(repoAccount.findById(1)).thenReturn(Optional.of(account));

    AbstractTransaction created = transactionService.create(
        BigDecimal.valueOf(100), "Salário", new Date(), TransactionStatus.PENDENTE, 1, TransactionType.ENTRADA, 1,
        null);

    assertNotNull(created);
    assertTrue(created instanceof TransactionInput);
    assertEquals(TransactionStatus.PENDENTE, created.getStatus());
    verify(repoTransaction, atLeastOnce()).save(any());
    verify(servAccount, never()).deposit(any(), any());
  }

  @Test
  @DisplayName("Deve criar transação normal de entrada efetivada com sucesso e debitar da conta")
  void shouldCreateNormalTransactionInputEfetivadaSuccessfully() {
    Category category = new Category("Salário", CategoryType.RECEITA);
    when(repoCategory.findById(1)).thenReturn(Optional.of(category));

    AbstractAccount account = new AccountBank("Banco");
    when(repoAccount.findById(1)).thenReturn(Optional.of(account));

    AbstractTransaction created = transactionService.create(
        BigDecimal.valueOf(100), "Salário", new Date(), TransactionStatus.EFETIVADA, 1, TransactionType.ENTRADA, 1,
        null);

    assertNotNull(created);
    assertEquals(TransactionStatus.EFETIVADA, created.getStatus());
    verify(servAccount, atLeastOnce()).deposit(1, BigDecimal.valueOf(100));
    verify(repoTransaction, atLeastOnce()).save(any());
  }

  @Test
  @DisplayName("Deve lançar exceção se transação for criada com valor nulo")
  void shouldThrowExceptionWhenCreateWithNullValue() {
    BusinessRuleException exception = assertThrows(
        BusinessRuleException.class,
        () -> transactionService.create(null, "Salário", new Date(), TransactionStatus.PENDENTE, 1,
            TransactionType.ENTRADA, 1, null));
    assertEquals("O Valor da transação tem que ser um valor positovo e não nulo!", exception.getMessage());
  }

  @Test
  @DisplayName("Deve criar transação de cartão de crédito com sucesso")
  void shouldCreateCreditCardTransactionSuccessfully() {
    Category category = new Category("Lazer", CategoryType.DESPESA);
    when(repoCategory.findById(1)).thenReturn(Optional.of(category));

    AbstractAccount account = new AccountBank("Banco");
    when(repoAccount.findById(1)).thenReturn(Optional.of(account));

    CreditCard card = new CreditCard("Cartão", BigDecimal.valueOf(1000), BigDecimal.ZERO, 10, 20,
        (AccountBank) account);
    card.setId(1);
    when(repoCreditCard.findById(1)).thenReturn(Optional.of(card));

    AbstractTransaction created = transactionService.create(
        BigDecimal.valueOf(100), "Lazer", new Date(), TransactionStatus.PENDENTE, 1, TransactionType.SAIDA, 1, 1);

    assertNotNull(created);
    assertTrue(created instanceof TransactionCreditCard);
    verify(servCreditCard, atLeastOnce()).deposit(eq(1), eq(BigDecimal.valueOf(100)));
    verify(repoTransaction, atLeastOnce()).save(any());
  }

  @Test
  @DisplayName("Deve atualizar transação efetivada revertendo e aplicando novo valor")
  void shouldUpdateEfetivadaTransactionSuccessfully() {
    AbstractAccount account = new AccountBank("Banco");
    account.setId(1);
    TransactionOutput transaction = new TransactionOutput(BigDecimal.valueOf(100), "Compra", new Date(),
        TransactionStatus.EFETIVADA, null, TransactionType.SAIDA, account);
    when(repoTransaction.findById(1)).thenReturn(Optional.of(transaction));

    TransactionUpdateDTO dto = new TransactionUpdateDTO(
        1, BigDecimal.valueOf(200), null, null, TransactionStatus.EFETIVADA, null, null, null, null, null);

    transactionService.update(1, dto);

    // Revert before update
    verify(servAccount, atLeastOnce()).deposit(1, BigDecimal.valueOf(100));
    // Apply after update
    verify(servAccount, atLeastOnce()).withdraw(1, BigDecimal.valueOf(200));
    verify(repoTransaction, atLeastOnce()).save(any());
  }

  @Test
  @DisplayName("Deve remover transação efetivada revertendo valor da conta")
  void shouldRemoveEfetivadaTransactionSuccessfully() {
    AbstractAccount account = new AccountBank("Banco");
    account.setId(1);
    TransactionOutput transaction = new TransactionOutput(BigDecimal.valueOf(100), "Compra", new Date(),
        TransactionStatus.EFETIVADA, null, TransactionType.SAIDA, account);
    when(repoTransaction.findById(1)).thenReturn(Optional.of(transaction));

    transactionService.remove(1);

    verify(servAccount, atLeastOnce()).deposit(1, BigDecimal.valueOf(100)); // Estorno de saída é um depósito
    verify(repoTransaction, atLeastOnce()).delete(any());
  }

  @Test
  @DisplayName("Deve listar e buscar transações com filtros")
  void shouldSearchTransactionsSuccessfully() {
    List<AbstractTransaction> transactions = List.of(
        new TransactionOutput(BigDecimal.valueOf(100), "Compra", new Date(), TransactionStatus.EFETIVADA, null,
            TransactionType.SAIDA, null));
    when(repoTransaction.findAll()).thenReturn(transactions);

    List<AbstractTransaction> found = transactionService.searchTransactions(TransactionStatus.EFETIVADA, null, null,
        null, null);

    assertEquals(1, found.size());
  }

  @Test
  @DisplayName("Deve lançar exceção ao criar com valor negativo")
  void shouldThrowExceptionWhenCreateWithNegativeValue() {
    BusinessRuleException exception = assertThrows(
        BusinessRuleException.class,
        () -> transactionService.create(BigDecimal.valueOf(-10), "Desc", new Date(), TransactionStatus.PENDENTE, 1,
            TransactionType.ENTRADA, 1, null));
    assertEquals("O Valor da transação tem que ser um valor positovo e não nulo!", exception.getMessage());
  }

  @Test
  @DisplayName("Deve lançar exceção ao criar com descrição vazia")
  void shouldThrowExceptionWhenCreateWithEmptyDescription() {
    BusinessRuleException exception = assertThrows(
        BusinessRuleException.class,
        () -> transactionService.create(BigDecimal.valueOf(10), "  ", new Date(), TransactionStatus.PENDENTE, 1,
            TransactionType.ENTRADA, 1, null));
    assertEquals("A descrição tem que ter um valor valido!", exception.getMessage());
  }

  @Test
  @DisplayName("Deve lançar exceção ao criar com categoria inexistente")
  void shouldThrowExceptionWhenCreateWithCategoryNotFound() {
    when(repoCategory.findById(1)).thenReturn(Optional.empty());
    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> transactionService.create(BigDecimal.valueOf(10), "Desc", new Date(), TransactionStatus.PENDENTE, 1,
            TransactionType.ENTRADA, 1, null));
    assertEquals("A categoria com o Id 1 não foi encontrada.", exception.getMessage());
  }

  @Test
  @DisplayName("Deve lançar exceção ao criar com conta inexistente")
  void shouldThrowExceptionWhenCreateWithAccountNotFound() {
    when(repoCategory.findById(1)).thenReturn(Optional.of(new Category("Salário", CategoryType.RECEITA)));
    when(repoAccount.findById(1)).thenReturn(Optional.empty());
    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> transactionService.create(BigDecimal.valueOf(10), "Desc", new Date(), TransactionStatus.PENDENTE, 1,
            TransactionType.ENTRADA, 1, null));
    assertEquals("A conta com o Id 1 não foi encontrada.", exception.getMessage());
  }

  @Test
  @DisplayName("Deve lançar exceção ao criar transação de cartão se cartão inexistente")
  void shouldThrowExceptionWhenCreateWithCardNotFound() {
    when(repoCategory.findById(1)).thenReturn(Optional.of(new Category("Lazer", CategoryType.DESPESA)));
    when(repoAccount.findById(1)).thenReturn(Optional.of(new AccountBank("Banco")));
    when(repoCreditCard.findById(1)).thenReturn(Optional.empty());
    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> transactionService.create(BigDecimal.valueOf(10), "Desc", new Date(), TransactionStatus.PENDENTE, 1,
            TransactionType.SAIDA, 1, 1));
    assertEquals("O Cartão de crédito com o ID 1 não foi encontrado.", exception.getMessage());
  }

  @Test
  @DisplayName("Deve criar transação normal de SAIDA efetivada chamando withdraw")
  void shouldCreateNormalTransactionOutputEfetivada() {
    when(repoCategory.findById(1)).thenReturn(Optional.of(new Category("Lazer", CategoryType.DESPESA)));
    when(repoAccount.findById(1)).thenReturn(Optional.of(new AccountBank("Banco")));

    AbstractTransaction created = transactionService.create(
        BigDecimal.valueOf(100), "Lazer", null, TransactionStatus.EFETIVADA, 1, TransactionType.SAIDA, 1, null);

    assertTrue(created instanceof TransactionOutput);
    assertNotNull(created.getDate());
    verify(servAccount, atLeastOnce()).withdraw(1, BigDecimal.valueOf(100));
  }

  @Test
  @DisplayName("Deve lançar exceção ao criar transação normal com tipo inválido")
  void shouldThrowExceptionWhenCreateNormalWithInvalidType() {
    when(repoCategory.findById(1)).thenReturn(Optional.of(new Category("Lazer", CategoryType.DESPESA)));
    when(repoAccount.findById(1)).thenReturn(Optional.of(new AccountBank("Banco")));

    BusinessRuleException exception = assertThrows(
        BusinessRuleException.class,
        () -> transactionService.create(BigDecimal.valueOf(100), "Lazer", null, TransactionStatus.PENDENTE, 1, null, 1,
            null));
    assertEquals("O tipo de transação não é um tipo valido!", exception.getMessage());
  }

  @Test
  @DisplayName("Deve criar transação de cartão se data for depois do fechamento")
  void shouldCreateCreditCardTransactionAfterClosing() {
    when(repoCategory.findById(1)).thenReturn(Optional.of(new Category("Lazer", CategoryType.DESPESA)));
    when(repoAccount.findById(1)).thenReturn(Optional.of(new AccountBank("Banco")));
    CreditCard card = new CreditCard("Cartão", BigDecimal.valueOf(1000), BigDecimal.ZERO, 28, 10,
        new AccountBank("Banco"));
    card.setId(1);
    when(repoCreditCard.findById(1)).thenReturn(Optional.of(card));

    AbstractTransaction created = transactionService.create(
        BigDecimal.valueOf(100), "Lazer", new Date(), TransactionStatus.PENDENTE, 1, TransactionType.SAIDA, 1, 1);

    assertTrue(created instanceof TransactionCreditCard);
  }

  @Test
  @DisplayName("Deve atualizar transação alterando todos os campos")
  void shouldUpdateTransactionFields() {
    AbstractAccount account = new AccountBank("Banco");
    account.setId(1);
    TransactionCreditCard transaction = new TransactionCreditCard(BigDecimal.valueOf(100), "Compra", new Date(),
        TransactionStatus.PENDENTE, new Category("Lazer", CategoryType.DESPESA), TransactionType.SAIDA, account,
        new CreditCard("Cartão", BigDecimal.ZERO, BigDecimal.ZERO, 10, 20, (AccountBank) account), new Date());
    when(repoTransaction.findById(1)).thenReturn(Optional.of(transaction));

    CreditCard newCard = new CreditCard("Novo Cartão", BigDecimal.ZERO, BigDecimal.ZERO, 10, 20, (AccountBank) account);
    newCard.setId(2);
    when(servCreditCard.findById(2)).thenReturn(newCard);

    when(repoCategory.findById(2)).thenReturn(Optional.of(new Category("Nova", CategoryType.DESPESA)));
    AbstractAccount newAccount = new AccountBank("Novo");
    newAccount.setId(2);
    when(servAccount.findById(2)).thenReturn(newAccount);

    Date newDate = new Date();
    TransactionUpdateDTO dto = new TransactionUpdateDTO(
        1, BigDecimal.valueOf(200), "Nova Compra", newDate, TransactionStatus.PENDENTE, 2, TransactionType.SAIDA, 2, 2,
        newDate);

    transactionService.update(1, dto);

    assertEquals("Nova Compra", transaction.getDescription());
    assertEquals(BigDecimal.valueOf(200), transaction.getTransactionValue());
    assertEquals(newDate, transaction.getDate());
    assertEquals("Nova", transaction.getCategory().getName());
    assertEquals(newAccount, transaction.getAccount());
    assertEquals(newCard, transaction.getCreditCard());
    assertEquals(newDate, transaction.getDueDate());
  }

  @Test
  @DisplayName("Deve lançar exceção ao atualizar transação com valor negativo")
  void shouldThrowExceptionWhenUpdateWithNegativeValue() {
    AbstractAccount account = new AccountBank("Banco");
    account.setId(1);
    TransactionOutput transaction = new TransactionOutput(BigDecimal.valueOf(100), "Compra", new Date(),
        TransactionStatus.PENDENTE, null, TransactionType.SAIDA, account);
    when(repoTransaction.findById(1)).thenReturn(Optional.of(transaction));

    TransactionUpdateDTO dto = new TransactionUpdateDTO(
        1, BigDecimal.valueOf(-200), null, null, null, null, null, null, null, null);

    BusinessRuleException exception = assertThrows(
        BusinessRuleException.class,
        () -> transactionService.update(1, dto));
    assertEquals("O valor da transação deve ser positivo", exception.getMessage());
  }

  @Test
  @DisplayName("Deve atualizar transações em lote")
  void shouldUpdateBatch() {
    AbstractAccount account = new AccountBank("Banco");
    account.setId(1);
    TransactionOutput t1 = new TransactionOutput(BigDecimal.valueOf(100), "C1", new Date(), TransactionStatus.PENDENTE,
        null, TransactionType.SAIDA, account);
    TransactionOutput t2 = new TransactionOutput(BigDecimal.valueOf(200), "C2", new Date(), TransactionStatus.PENDENTE,
        null, TransactionType.SAIDA, account);
    when(repoTransaction.findById(1)).thenReturn(Optional.of(t1));
    when(repoTransaction.findById(2)).thenReturn(Optional.of(t2));

    TransactionUpdateDTO dto1 = new TransactionUpdateDTO(1, null, "C1_NEW", null, null, null, null, null, null, null);
    TransactionUpdateDTO dto2 = new TransactionUpdateDTO(2, null, "C2_NEW", null, null, null, null, null, null, null);

    transactionService.updateBatch(List.of(dto1, dto2));

    assertEquals("C1_NEW", t1.getDescription());
    assertEquals("C2_NEW", t2.getDescription());
    verify(repoTransaction, atLeastOnce()).save(t1);
    verify(repoTransaction, atLeastOnce()).save(t2);
  }

  @Test
  @DisplayName("Deve remover transação de cartão pendente revertendo na fatura")
  void shouldRemoveCreditCardTransaction() {
    CreditCard card = new CreditCard("Cartão", BigDecimal.ZERO, BigDecimal.ZERO, 10, 20, new AccountBank("Banco"));
    card.setId(1);
    TransactionCreditCard transaction = new TransactionCreditCard(BigDecimal.valueOf(100), "Compra", new Date(),
        TransactionStatus.PENDENTE, null, TransactionType.SAIDA, null, card, new Date());
    when(repoTransaction.findById(1)).thenReturn(Optional.of(transaction));

    transactionService.remove(1);

    verify(servCreditCard, atLeastOnce()).withdraw(1, BigDecimal.valueOf(100)); // estorno
    verify(repoTransaction, atLeastOnce()).delete(transaction);
  }

  @Test
  @DisplayName("Deve buscar transações com todos os filtros combinados")
  void shouldSearchWithAllFilters() {
    Category cat = new Category("Cat", CategoryType.DESPESA);
    cat.setId(1);
    AbstractAccount acc = new AccountBank("Banco");
    acc.setId(1);
    CreditCard card = new CreditCard("Cartão", BigDecimal.ZERO, BigDecimal.ZERO, 10, 20, (AccountBank) acc);
    card.setId(1);
    TransactionCreditCard t1 = new TransactionCreditCard(BigDecimal.valueOf(100), "C1", new Date(),
        TransactionStatus.PENDENTE, cat, TransactionType.SAIDA, acc, card, new Date());
    TransactionOutput t2 = new TransactionOutput(BigDecimal.valueOf(200), "C2", new Date(), TransactionStatus.EFETIVADA,
        cat, TransactionType.SAIDA, acc);

    when(repoTransaction.findAll()).thenReturn(List.of(t1, t2));

    List<AbstractTransaction> found = transactionService.searchTransactions(TransactionStatus.PENDENTE, 1,
        TransactionType.SAIDA, 1, 1);

    assertEquals(1, found.size());
    assertEquals(t1, found.get(0));
  }

  @Test
  @DisplayName("Deve lançar exceção ao tentar buscar transação inexistente")
  void shouldThrowExceptionWhenFindByIdNotFound() {
    when(repoTransaction.findById(99)).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(
      ResourceNotFoundException.class,
      () -> transactionService.findById(99)
    );

    assertEquals("Transação com ID 99 não encontrada", exception.getMessage());
  }

  @Test
  @DisplayName("Deve atualizar transação de ENTRADA efetivada revertendo com withdraw")
  void shouldRevertEntradaWhenUpdating() {
    AbstractAccount account = new AccountBank("Banco");
    account.setId(1);
    TransactionInput transaction = new TransactionInput(
      BigDecimal.valueOf(100), "Salário", new Date(), TransactionStatus.EFETIVADA,
      null, TransactionType.ENTRADA, account
    );
    when(repoTransaction.findById(1)).thenReturn(Optional.of(transaction));

    TransactionUpdateDTO dto = new TransactionUpdateDTO(
      1, null, null, null, TransactionStatus.PENDENTE, null, null, null, null, null
    );

    transactionService.update(1, dto);

    // Revert de uma ENTRADA efetivada deve chamar withdraw
    verify(servAccount, atLeastOnce()).withdraw(1, BigDecimal.valueOf(100));
    verify(repoTransaction, atLeastOnce()).save(any());
  }

  @Test
  @DisplayName("Deve atualizar transação pendente (SAIDA) mudando para PENDENTE sem mexer na conta")
  void shouldUpdatePendingTransactionToStayPending() {
    AbstractAccount account = new AccountBank("Banco");
    account.setId(1);
    TransactionOutput transaction = new TransactionOutput(
      BigDecimal.valueOf(50), "Compra", new Date(), TransactionStatus.PENDENTE,
      null, TransactionType.SAIDA, account
    );
    when(repoTransaction.findById(1)).thenReturn(Optional.of(transaction));

    TransactionUpdateDTO dto = new TransactionUpdateDTO(
      1, BigDecimal.valueOf(75), null, null, TransactionStatus.PENDENTE, null, null, null, null, null
    );

    transactionService.update(1, dto);

    // Transação permanece PENDENTE, nenhum depósito/saque deve ser chamado
    verify(servAccount, never()).deposit(any(), any());
    verify(servAccount, never()).withdraw(any(), any());
    verify(repoTransaction, atLeastOnce()).save(any());
  }

  @Test
  @DisplayName("Deve atualizar transação pendente de ENTRADA mudando para EFETIVADA depositando na conta")
  void shouldUpdatePendingEntradaToEfetivada() {
    AbstractAccount account = new AccountBank("Banco");
    account.setId(1);
    TransactionInput transaction = new TransactionInput(
      BigDecimal.valueOf(100), "Salário", new Date(), TransactionStatus.PENDENTE,
      null, TransactionType.ENTRADA, account
    );
    when(repoTransaction.findById(1)).thenReturn(Optional.of(transaction));

    TransactionUpdateDTO dto = new TransactionUpdateDTO(
      1, null, null, null, TransactionStatus.EFETIVADA, null, null, null, null, null
    );

    transactionService.update(1, dto);

    // Aplicar ENTRADA efetivada deve chamar deposit
    verify(servAccount, atLeastOnce()).deposit(1, BigDecimal.valueOf(100));
    verify(repoTransaction, atLeastOnce()).save(any());
  }

}
