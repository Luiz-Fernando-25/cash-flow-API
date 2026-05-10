package org.example.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.example.domain.enums.AccountType;
import org.example.domain.models.AbstractAccount;
import org.example.domain.models.AccountBank;
import org.example.domain.models.AccountWallet;
import org.example.exceptions.BusinessRuleException;
import org.example.exceptions.ResourceNotFoundException;
import org.example.repositories.AccountRepository;
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
public class AccountServiceImplTest {

  @Mock
  private AccountRepository accountRepository;

  @InjectMocks
  private AccountServiceImpl accountService;

  @ParameterizedTest
  @DisplayName("Deve lançar exceção ao tentar criar conta com nome inválido")
  @NullAndEmptySource
  @ValueSource(strings = { " " })
  void shouldThrowExceptionWhenAccountNameIsInvalid(String name) {
    BusinessRuleException exception = assertThrows(
      BusinessRuleException.class,
      () -> {
        accountService.create(name, AccountType.BANCO);
      }
    );

    assertEquals("O nome não pode ser vazio!", exception.getMessage());
    verify(accountRepository, never()).save(any());
  }

  @Test
  @DisplayName("Deve lançar exceção ao tentar criar conta com tipo inválido")
  void shouldThrowExceptionWhenAccountTypeIsInvalid() {
    BusinessRuleException exception = assertThrows(
      BusinessRuleException.class,
      () -> {
        accountService.create("Conta", null);
      }
    );

    assertEquals(
      "O tipo de conta fornecedido é inexistente!",
      exception.getMessage()
    );
    verify(accountRepository, never()).save(any());
  }

  @Test
  @DisplayName("Deve criar conta de banco com sucesso")
  void shouldCreateAccountBankSuccessfully() {
    AbstractAccount account = new AccountBank("Banco");
    account.setType(AccountType.BANCO);
    when(accountRepository.save(any())).thenReturn(account);

    AbstractAccount createdAccount = accountService.create(
      "Banco",
      AccountType.BANCO
    );

    assertEquals("Banco", createdAccount.getAccountName());
    assertEquals(AccountType.BANCO, createdAccount.getType());
    verify(accountRepository, atLeastOnce()).save(any());
  }

  @Test
  @DisplayName("Deve criar conta de carteira com sucesso")
  void shouldCreateAccountWalletSuccessfully() {
    AbstractAccount account = new AccountWallet("Carteira");
    account.setType(AccountType.CARTEIRA);
    when(accountRepository.save(any())).thenReturn(account);

    AbstractAccount createdAccount = accountService.create(
      "Carteira",
      AccountType.CARTEIRA
    );

    assertEquals("Carteira", createdAccount.getAccountName());
    assertEquals(AccountType.CARTEIRA, createdAccount.getType());
    verify(accountRepository, atLeastOnce()).save(any());
  }

  @Test
  @DisplayName("Deve lançar exceção ao tentar buscar conta inexistente")
  void shouldThrowExceptionWhenAccountNotFound() {
    when(accountRepository.findById(1)).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(
      ResourceNotFoundException.class,
      () -> {
        accountService.findById(1);
      }
    );

    assertEquals("Conta com ID 1 não encontrada", exception.getMessage());
    verify(accountRepository, atLeastOnce()).findById(1);
  }

  @Test
  @DisplayName("Deve buscar conta com sucesso por ID")
  void shouldFindAccountSuccessfullyById() {
    AbstractAccount account = new AccountBank("Banco");
    account.setType(AccountType.BANCO);
    when(accountRepository.findById(1)).thenReturn(Optional.of(account));

    AbstractAccount foundAccount = accountService.findById(1);

    assertEquals(account, foundAccount);
    assertEquals("Banco", foundAccount.getAccountName());
    assertEquals(AccountType.BANCO, foundAccount.getType());
    verify(accountRepository, atLeastOnce()).findById(1);
  }

  @ParameterizedTest
  @DisplayName("Deve lançar exceção ao tentar atualizar conta com nome inválido")
  @NullAndEmptySource
  @ValueSource(strings = { "  " })
  void shouldThrowExceptionWhenUpdateAccountNameIsInvalid(String name) {
    AbstractAccount account = new AccountBank("Banco");
    when(accountRepository.findById(1)).thenReturn(Optional.of(account));

    BusinessRuleException exception = assertThrows(
      BusinessRuleException.class,
      () -> {
        accountService.update(1, name);
      }
    );

    assertEquals("O nome não pode ser vazio!", exception.getMessage());
    verify(accountRepository, never()).save(any());
  }

  @Test
  @DisplayName("Deve atualizar conta com sucesso")
  void shouldUpdateAccountSuccessfully() {
    AbstractAccount account = new AccountBank("Banco");
    when(accountRepository.findById(1)).thenReturn(Optional.of(account));
    when(accountRepository.save(any())).thenReturn(account);

    AbstractAccount updatedAccount = accountService.update(1, "Banco Atualizado");

    assertEquals("Banco Atualizado", updatedAccount.getAccountName());
    verify(accountRepository, atLeastOnce()).save(any());
  }

  @Test
  @DisplayName("Deve lançar exceção ao tentar depositar valor nulo ou menor igual a zero")
  void shouldThrowExceptionWhenDepositValueIsInvalid() {
    BusinessRuleException exceptionNull = assertThrows(
      BusinessRuleException.class,
      () -> accountService.deposit(1, null)
    );
    assertEquals("O valor do depósito deve ser maior que zero.", exceptionNull.getMessage());

    BusinessRuleException exceptionZero = assertThrows(
      BusinessRuleException.class,
      () -> accountService.deposit(1, BigDecimal.ZERO)
    );
    assertEquals("O valor do depósito deve ser maior que zero.", exceptionZero.getMessage());
    
    verify(accountRepository, never()).save(any());
  }

  @Test
  @DisplayName("Deve depositar com sucesso")
  void shouldDepositSuccessfully() {
    AbstractAccount account = new AccountBank("Banco");
    when(accountRepository.findById(1)).thenReturn(Optional.of(account));

    accountService.deposit(1, new BigDecimal("100.0"));

    verify(accountRepository, atLeastOnce()).save(any());
  }

  @Test
  @DisplayName("Deve lançar exceção ao tentar sacar valor nulo ou menor igual a zero")
  void shouldThrowExceptionWhenWithdrawValueIsInvalid() {
    BusinessRuleException exceptionNull = assertThrows(
      BusinessRuleException.class,
      () -> accountService.withdraw(1, null)
    );
    assertEquals("O valor do saque deve ser maior que zero.", exceptionNull.getMessage());

    BusinessRuleException exceptionZero = assertThrows(
      BusinessRuleException.class,
      () -> accountService.withdraw(1, BigDecimal.ZERO)
    );
    assertEquals("O valor do saque deve ser maior que zero.", exceptionZero.getMessage());
    
    verify(accountRepository, never()).save(any());
  }

  @Test
  @DisplayName("Deve sacar com sucesso")
  void shouldWithdrawSuccessfully() {
    AbstractAccount account = new AccountBank("Banco");
    // Adicionamos um saldo inicial falso para permitir o saque, a lógica do domínio pode barrar se não houver saldo
    account.deposit(new BigDecimal("200.0")); 
    when(accountRepository.findById(1)).thenReturn(Optional.of(account));

    accountService.withdraw(1, new BigDecimal("100.0"));

    verify(accountRepository, atLeastOnce()).save(any());
  }

  @Test
  @DisplayName("Deve buscar todas as contas com sucesso")
  void shouldSearchAllAccountsSuccessfully() {
    List<AbstractAccount> accounts = new ArrayList<>();
    accounts.add(new AccountBank("Banco"));
    accounts.add(new AccountWallet("Carteira"));
    when(accountRepository.findAll()).thenReturn(accounts);

    List<AbstractAccount> foundAccounts = accountService.searchAccounts(null);

    assertEquals(2, foundAccounts.size());
    verify(accountRepository, atLeastOnce()).findAll();
  }

  @Test
  @DisplayName("Deve buscar contas por tipo com sucesso")
  void shouldSearchAccountsByTypeSuccessfully() {
    List<AbstractAccount> accounts = new ArrayList<>();
    AbstractAccount bank = new AccountBank("Banco");
    bank.setType(AccountType.BANCO);
    AbstractAccount wallet = new AccountWallet("Carteira");
    wallet.setType(AccountType.CARTEIRA);
    accounts.add(bank);
    accounts.add(wallet);
    when(accountRepository.findAll()).thenReturn(accounts);

    List<AbstractAccount> foundAccounts = accountService.searchAccounts(AccountType.BANCO);

    assertEquals(1, foundAccounts.size());
    assertEquals(AccountType.BANCO, foundAccounts.get(0).getType());
    verify(accountRepository, atLeastOnce()).findAll();
  }

  @Test
  @DisplayName("Deve remover conta com sucesso")
  void shouldRemoveAccountSuccessfully() {
    AbstractAccount account = new AccountBank("Banco");
    when(accountRepository.findById(1)).thenReturn(Optional.of(account));

    accountService.remove(1);

    verify(accountRepository, atLeastOnce()).delete(any());
  }
}
