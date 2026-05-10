package org.example.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.example.domain.models.AbstractAccount;
import org.example.domain.models.AccountBank;
import org.example.domain.models.AccountWallet;
import org.example.domain.models.CreditCard;
import org.example.dtos.CreditCardUpdateDTO;
import org.example.exceptions.BusinessRuleException;
import org.example.exceptions.ResourceNotFoundException;
import org.example.repositories.AccountRepository;
import org.example.repositories.CreditCardRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CreditCardServiceImplTest {

  @Mock
  private CreditCardRepository creditCardRepository;

  @Mock
  private AccountRepository accountRepository;

  @InjectMocks
  private CreditCardServiceImpl creditCardService;

  @ParameterizedTest
  @DisplayName("Deve lançar exceção ao tentar criar cartão com nome inválido")
  @NullAndEmptySource
  @ValueSource(strings = { " " })
  void shouldThrowExceptionWhenCreditCardNameIsInvalid(String name) {
    BusinessRuleException exception = assertThrows(
      BusinessRuleException.class,
      () -> {
        creditCardService.create(
          name,
          BigDecimal.valueOf(1000),
          BigDecimal.ZERO,
          10,
          20,
          1
        );
      }
    );

    assertEquals("O nome não pode ser vazio!", exception.getMessage());
    verify(creditCardRepository, never()).save(any());
  }

  @ParameterizedTest
  @DisplayName(
    "Deve lançar exceção ao tentar criar cartão com dia de fechamento inválido"
  )
  @ValueSource(ints = { 0, 29, 31, -1 })
  void shouldThrowExceptionWhenClosingDayIsInvalid(Integer day) {
    BusinessRuleException exception = assertThrows(
      BusinessRuleException.class,
      () -> {
        creditCardService.create(
          "Cartão",
          BigDecimal.valueOf(1000),
          BigDecimal.ZERO,
          day,
          20,
          1
        );
      }
    );

    assertEquals(
      "O dia tem que ser um número entre 1 e 28",
      exception.getMessage()
    );
    verify(creditCardRepository, never()).save(any());
  }

  @Test
  @DisplayName(
    "Deve lançar exceção ao tentar criar cartão se a conta não for encontrada"
  )
  void shouldThrowExceptionWhenAccountNotFound() {
    when(accountRepository.findById(1)).thenReturn(Optional.empty());

    BusinessRuleException exception = assertThrows(
      BusinessRuleException.class,
      () -> {
        creditCardService.create(
          "Cartão",
          BigDecimal.valueOf(1000),
          BigDecimal.ZERO,
          10,
          20,
          1
        );
      }
    );

    assertEquals("Banco com ID1 não encontrado", exception.getMessage());
    verify(creditCardRepository, never()).save(any());
  }

  @Test
  @DisplayName(
    "Deve lançar exceção ao tentar criar cartão se a conta não for Banco"
  )
  void shouldThrowExceptionWhenAccountIsNotBank() {
    AbstractAccount wallet = new AccountWallet("Carteira");
    when(accountRepository.findById(1)).thenReturn(Optional.of(wallet));

    BusinessRuleException exception = assertThrows(
      BusinessRuleException.class,
      () -> {
        creditCardService.create(
          "Cartão",
          BigDecimal.valueOf(1000),
          BigDecimal.ZERO,
          10,
          20,
          1
        );
      }
    );

    assertEquals("A conta informada não é um banco.", exception.getMessage());
    verify(creditCardRepository, never()).save(any());
  }

  @Test
  @DisplayName("Deve criar cartão de crédito com sucesso")
  void shouldCreateCreditCardSuccessfully() {
    AccountBank bank = new AccountBank("Banco");
    when(accountRepository.findById(1)).thenReturn(Optional.of(bank));

    CreditCard mockCard = new CreditCard(
      "Cartão",
      BigDecimal.valueOf(1000),
      BigDecimal.ZERO,
      10,
      20,
      bank
    );
    when(creditCardRepository.save(any())).thenReturn(mockCard);

    CreditCard createdCard = creditCardService.create(
      "Cartão",
      BigDecimal.valueOf(1000),
      BigDecimal.ZERO,
      10,
      20,
      1
    );

    assertEquals("Cartão", createdCard.getName());
    assertEquals(10, createdCard.getClosingDay());
    verify(creditCardRepository, atLeastOnce()).save(any());
  }

  @Test
  @DisplayName("Deve lançar exceção ao buscar cartão inexistente")
  void shouldThrowExceptionWhenFindByIdNotFound() {
    when(creditCardRepository.findById(1)).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(
      ResourceNotFoundException.class,
      () -> {
        creditCardService.findById(1);
      }
    );

    assertEquals("Cartão com ID 1 não encontrado", exception.getMessage());
  }

  @Test
  @DisplayName("Deve buscar cartão com sucesso por ID")
  void shouldFindByIdSuccessfully() {
    CreditCard mockCard = new CreditCard(
      "Cartão",
      BigDecimal.valueOf(1000),
      BigDecimal.ZERO,
      10,
      20,
      new AccountBank("Banco")
    );
    when(creditCardRepository.findById(1)).thenReturn(Optional.of(mockCard));

    CreditCard foundCard = creditCardService.findById(1);

    assertEquals("Cartão", foundCard.getName());
  }

  @Test
  @DisplayName("Deve listar todos os cartões com sucesso")
  void shouldListAllSuccessfully() {
    List<CreditCard> cards = List.of(
      new CreditCard(
        "Cartão",
        BigDecimal.valueOf(1000),
        BigDecimal.ZERO,
        10,
        20,
        new AccountBank("Banco")
      )
    );
    when(creditCardRepository.findAll()).thenReturn(cards);

    List<CreditCard> foundCards = creditCardService.listAll();

    assertEquals(1, foundCards.size());
  }

  @Test
  @DisplayName("Deve remover cartão com sucesso")
  void shouldRemoveSuccessfully() {
    CreditCard mockCard = new CreditCard(
      "Cartão",
      BigDecimal.valueOf(1000),
      BigDecimal.ZERO,
      10,
      20,
      new AccountBank("Banco")
    );
    when(creditCardRepository.findById(1)).thenReturn(Optional.of(mockCard));

    creditCardService.remove(1);

    verify(creditCardRepository, atLeastOnce()).delete(any());
  }

  @Test
  @DisplayName(
    "Deve lançar exceção ao depositar valor nulo ou menor igual a zero"
  )
  void shouldThrowExceptionWhenDepositInvalidValue() {
    BusinessRuleException exceptionNull = assertThrows(
      BusinessRuleException.class,
      () -> creditCardService.deposit(1, null)
    );
    assertEquals("Valor deve ser positivo.", exceptionNull.getMessage());

    BusinessRuleException exceptionZero = assertThrows(
      BusinessRuleException.class,
      () -> creditCardService.deposit(1, BigDecimal.ZERO)
    );
    assertEquals("Valor deve ser positivo.", exceptionZero.getMessage());
  }

  @Test
  @DisplayName("Deve depositar com sucesso")
  void shouldDepositSuccessfully() {
    CreditCard mockCard = new CreditCard(
      "Cartão",
      BigDecimal.valueOf(1000),
      BigDecimal.ZERO,
      10,
      20,
      new AccountBank("Banco")
    );
    when(creditCardRepository.findById(1)).thenReturn(Optional.of(mockCard));

    creditCardService.deposit(1, new BigDecimal("100"));

    verify(creditCardRepository, atLeastOnce()).save(any());
  }

  @Test
  @DisplayName("Deve lançar exceção ao sacar valor nulo ou menor igual a zero")
  void shouldThrowExceptionWhenWithdrawInvalidValue() {
    BusinessRuleException exceptionNull = assertThrows(
      BusinessRuleException.class,
      () -> creditCardService.withdraw(1, null)
    );
    assertEquals("Valor deve ser positivo.", exceptionNull.getMessage());

    BusinessRuleException exceptionZero = assertThrows(
      BusinessRuleException.class,
      () -> creditCardService.withdraw(1, BigDecimal.ZERO)
    );
    assertEquals("Valor deve ser positivo.", exceptionZero.getMessage());
  }

  @Test
  @DisplayName("Deve sacar com sucesso")
  void shouldWithdrawSuccessfully() {
    CreditCard mockCard = new CreditCard(
      "Cartão",
      BigDecimal.valueOf(1000),
      BigDecimal.ZERO,
      10,
      20,
      new AccountBank("Banco")
    );
    mockCard.deposit(new BigDecimal("200")); // Evitar erro de limite/saldo se houver
    when(creditCardRepository.findById(1)).thenReturn(Optional.of(mockCard));

    creditCardService.withdraw(1, new BigDecimal("100"));

    verify(creditCardRepository, atLeastOnce()).save(any());
  }

  @Test
  @DisplayName("Deve atualizar cartão com sucesso")
  void shouldUpdateSuccessfully() {
    CreditCard mockCard = new CreditCard(
      "Cartão",
      BigDecimal.valueOf(1000),
      BigDecimal.ZERO,
      10,
      20,
      new AccountBank("Banco")
    );
    when(creditCardRepository.findById(1)).thenReturn(Optional.of(mockCard));
    when(creditCardRepository.save(any())).thenReturn(mockCard);

    CreditCardUpdateDTO dto = new CreditCardUpdateDTO(
      "Novo Cartão",
      BigDecimal.valueOf(2000),
      null,
      5,
      15,
      null
    );

    CreditCard updatedCard = creditCardService.update(1, dto);

    assertEquals("Novo Cartão", updatedCard.getName());
    assertEquals(BigDecimal.valueOf(2000), updatedCard.getLimit());
    assertEquals(5, updatedCard.getClosingDay());
    assertEquals(15, updatedCard.getDueDate());
    verify(creditCardRepository, atLeastOnce()).save(any());
  }

  @Test
  @DisplayName("Deve lançar exceção ao atualizar com limite negativo")
  void shouldThrowExceptionWhenUpdateLimitIsNegative() {
    CreditCard mockCard = new CreditCard(
      "Cartão",
      BigDecimal.valueOf(1000),
      BigDecimal.ZERO,
      10,
      20,
      new AccountBank("Banco")
    );
    when(creditCardRepository.findById(1)).thenReturn(Optional.of(mockCard));

    CreditCardUpdateDTO dto = new CreditCardUpdateDTO(
      "Novo Cartão",
      BigDecimal.valueOf(-100),
      null,
      5,
      15,
      null
    );

    BusinessRuleException exception = assertThrows(
      BusinessRuleException.class,
      () -> creditCardService.update(1, dto)
    );

    assertEquals("O limite não pode ser negativo", exception.getMessage());
  }

  @Test
  @DisplayName("Deve atualizar o banco do cartão com sucesso")
  void shouldUpdateBankSuccessfully() {
    CreditCard mockCard = new CreditCard(
      "Cartão",
      BigDecimal.valueOf(1000),
      BigDecimal.ZERO,
      10,
      20,
      new AccountBank("Banco 1")
    );
    AccountBank newBank = new AccountBank("Banco 2");

    when(creditCardRepository.findById(1)).thenReturn(Optional.of(mockCard));
    when(accountRepository.findById(2)).thenReturn(Optional.of(newBank));
    when(creditCardRepository.save(any())).thenReturn(mockCard);

    CreditCardUpdateDTO dto = new CreditCardUpdateDTO(
      null,
      null,
      null,
      null,
      null,
      2
    );

    creditCardService.update(1, dto);

    verify(accountRepository, atLeastOnce()).findById(2);
    verify(creditCardRepository, atLeastOnce()).save(any());
  }

  @Test
  @DisplayName(
    "Deve criar cartão com limite e saldo nulos convertendo para ZERO"
  )
  void shouldCreateWithNullLimitAndBalance() {
    AccountBank bank = new AccountBank("Banco");
    when(accountRepository.findById(1)).thenReturn(Optional.of(bank));

    CreditCard mockCard = new CreditCard(
      "Cartão",
      BigDecimal.ZERO,
      BigDecimal.ZERO,
      10,
      20,
      bank
    );
    when(creditCardRepository.save(any())).thenReturn(mockCard);

    CreditCard createdCard = creditCardService.create(
      "Cartão",
      null,
      null,
      10,
      20,
      1
    );

    assertEquals(BigDecimal.ZERO, createdCard.getLimit());
    assertEquals(BigDecimal.ZERO, createdCard.getBalance());
    verify(creditCardRepository, atLeastOnce()).save(any());
  }

  @Test
  @DisplayName(
    "Deve lançar exceção ao atualizar cartão se o banco não for encontrado"
  )
  void shouldThrowExceptionWhenUpdateBankNotFound() {
    CreditCard mockCard = new CreditCard(
      "Cartão",
      BigDecimal.valueOf(1000),
      BigDecimal.ZERO,
      10,
      20,
      new AccountBank("Banco 1")
    );
    when(creditCardRepository.findById(1)).thenReturn(Optional.of(mockCard));
    when(accountRepository.findById(2)).thenReturn(Optional.empty());

    CreditCardUpdateDTO dto = new CreditCardUpdateDTO(
      null,
      null,
      null,
      null,
      null,
      2
    );

    BusinessRuleException exception = assertThrows(
      BusinessRuleException.class,
      () -> creditCardService.update(1, dto)
    );

    assertEquals("Banco com ID 2 não encontrado", exception.getMessage());
  }

  @Test
  @DisplayName(
    "Não deve atualizar o banco se a conta informada não for um banco"
  )
  void shouldNotUpdateBankWhenAccountIsNotBank() {
    CreditCard mockCard = new CreditCard(
      "Cartão",
      BigDecimal.valueOf(1000),
      BigDecimal.ZERO,
      10,
      20,
      new AccountBank("Banco 1")
    );
    AccountWallet wallet = new AccountWallet("Carteira");

    when(creditCardRepository.findById(1)).thenReturn(Optional.of(mockCard));
    when(accountRepository.findById(2)).thenReturn(Optional.of(wallet));
    when(creditCardRepository.save(any())).thenReturn(mockCard);

    CreditCardUpdateDTO dto = new CreditCardUpdateDTO(
      null,
      null,
      null,
      null,
      null,
      2
    );

    creditCardService.update(1, dto);

    // O save é chamado, mas a conta não é alterada pois a condição `instanceof AccountBank` é falsa.
    verify(creditCardRepository, atLeastOnce()).save(any());
  }
}
