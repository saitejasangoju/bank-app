package com.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.bank.dto.CreditDebit;
import com.bank.dto.MoneyTransfer;
import com.bank.entity.Account;
import com.bank.entity.AccountType;
import com.bank.entity.Address;
import com.bank.entity.Customer;
import com.bank.entity.Transaction;
import com.bank.entity.TransactionType;
import com.bank.exception.NotActiveException;
import com.bank.repository.AccountRepository;
import com.bank.repository.CustomerRepository;
import com.bank.repository.TransactionRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionServiceTest {

	@Autowired
	private TransactionService transactionService;

	@MockBean
	private TransactionRepository transactionRepository;

	@MockBean
	private CustomerRepository customerRepository;

	@MockBean
	private AccountRepository accountRepository;

	Address address = Address.builder().houseNumber("34-9").city("hyd").state("ts").pincode("987789").build();
	Customer customer1 = Customer.builder().customerId("731163625713").name("teja").dob("2002-11-05")
			.phone("9283773654").email("teja@gmail.com").aadhar("987678098076").address(address).build();
	Customer customer3 = Customer.builder().customerId("831163625713").name("tejasango").dob("2002-10-10")
			.phone("9283773654").email("teja@gmail.com").aadhar("987678098076").address(address).build();
	Customer customer2 = Customer.builder().customerId("731163625714").name("sai").dob("2002-10-20").phone("9883773654")
			.email("sai@gmail.com").aadhar("777678098076").address(address).build();
	Account account1 = Account.builder().aid("63aae0328a1acb3d34d568f5").customerId("731163625713")
			.accountNumber("3714762657302").type(AccountType.SAVING).ifscCode("SBI21315").accountBalance(25000.0)
			.active(true).build();
	Account account2 = Account.builder().aid("63aae0328a1acb3d34d568f9").customerId("881163625713")
			.accountNumber("8714762657302").type(AccountType.SAVING).ifscCode("SBI21315").accountBalance(25000.0)
			.active(true).build();
	Account account3 = Account.builder().aid("63aae0328a1acb3d34d679f6").customerId("831163625713")
			.accountNumber("5414762657301").type(AccountType.SALARY).ifscCode("SBI21315").accountBalance(35000.0)
			.active(false).build();
	Instant date1 = Instant.parse("2022-12-04T17:21:18.139Z");
	Instant date2 = Instant.parse("2022-12-03T17:21:18.139Z");
	Transaction transaction1 = Transaction.builder().id("63ac7b0ec00a170f750646b8").customerId("731163625713")
			.accountNumber("3714762657302").date(date1).amount(25000.0).type(TransactionType.DEPOSIT).build();
	Transaction transaction2 = Transaction.builder().id("63ac7b0ec00a170f750646b9").customerId("731163625713")
			.accountNumber("3714762657302").date(date2).amount(3700.0).type(TransactionType.DEPOSIT).build();
	Transaction transaction3 = Transaction.builder().id("63ac7b0ec00a170f750646b7").customerId("881163625713")
			.accountNumber("8714762657302").date(date2).amount(3700.0).type(TransactionType.WITHDRAW).build();

	@Test
	void testDeposit() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		CreditDebit deposit = CreditDebit.builder().amount(25000.0).build();
		Mockito.when(transactionService.deposit("731163625713", "3714762657302", deposit)).thenReturn(transaction1);
		assertEquals(25000.0, transaction1.getAmount());
	}
	
	@Test
	void inActiveAccountForDeposit() throws Exception {
		Mockito.when(customerRepository.findById("831163625713")).thenReturn(Optional.of(customer3));
		Mockito.when(accountRepository.findByAccountNumber("5414762657301")).thenReturn(account3);
		CreditDebit deposit = CreditDebit.builder().amount(25000.0).build();
		assertThrows(NotActiveException.class, () -> transactionService.deposit("831163625713", "5414762657301", deposit));

	}

	@Test
	void testWithdrawal() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		CreditDebit withdraw = CreditDebit.builder().amount(3700.0).build();
		Mockito.when(transactionService.withdrawal("731163625713", "3714762657302", withdraw)).thenReturn(transaction2);
		assertEquals(3700.0, transaction2.getAmount());
	}
	
	@Test
	void invalidAmountForWithdrawal() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Account account = Account.builder().aid("3jdkjlal3u327djlh78o3").accountNumber("3714762657302").customerId("731163625713")
					.accountBalance(2300.0).active(true).type(AccountType.SALARY).build();
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account);
		CreditDebit withdraw = CreditDebit.builder().amount(0).build();
		assertThrows(IllegalArgumentException.class, () -> transactionService.withdrawal("731163625713", "3714762657302", withdraw));
	}
	
	@Test
	void insufficentBalanceForWithdrawal() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Account account = Account.builder().aid("3jdkjlal3u327djlh78o3").accountNumber("3714762657302").customerId("731163625713")
					.accountBalance(0).active(true).type(AccountType.SALARY).build();
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account);
		CreditDebit withdraw = CreditDebit.builder().amount(3700.0).build();
		assertThrows(IllegalArgumentException.class, () -> transactionService.withdrawal("731163625713", "3714762657302", withdraw));
	}
	
	

	@Test
	void testGetById() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		Mockito.when(transactionRepository.findById("63ac7b0ec00a170f750646b8")).thenReturn(Optional.of(transaction1));
		Transaction transaction = transactionService.getById("731163625713", "3714762657302", "63ac7b0ec00a170f750646b8");
		assertEquals(25000.0, transaction.getAmount());
	}
	
	
	@Test
	void testTransactionsList() throws Exception { 
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		List<Transaction> list = new ArrayList<>();
		list.add(transaction1);
		list.add(transaction2);
		Mockito.when(transactionService.list("731163625713", "3714762657302")).thenReturn(list);
		assertEquals(2, list.size());
	}
	
	@Test
	void testGetRecentTransactions() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		List<Transaction> list = new ArrayList<>();
		list.add(transaction1);
		list.add(transaction2);
		Mockito.when(transactionService.getRecentTransactions("731163625713", "3714762657302")).thenReturn(list);
		assertEquals(2, list.size());
		
	}

	@Test
	void testMoneyTransfer() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		Mockito.when(accountRepository.findByAccountNumber("8714762657302")).thenReturn(account2);
		MoneyTransfer transfer = MoneyTransfer.builder().amount(3700.0).receiver("8714762657302").build();
		List<Transaction> list = new ArrayList<>();
		list.add(transaction2);
		list.add(transaction3);
		Mockito.when(transactionService.moneyTransfer("731163625713", "3714762657302", transfer)).thenReturn(list);
		assertEquals(2, list.size());
		
	}
	
	@Test
	void insufficientAmountForTransfer() throws Exception {
		Account senderAccount = Account.builder().aid("63aae0328a1acb3d34d568f5").customerId("731163625713")
				.accountNumber("2214762657302").type(AccountType.SAVING).ifscCode("SBI21315").accountBalance(25000.0)
				.active(true).build();
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("2214762657302")).thenReturn(senderAccount);
		Mockito.when(accountRepository.findByAccountNumber("8714762657302")).thenReturn(account2);
		MoneyTransfer transfer = MoneyTransfer.builder().amount(26000.0).receiver("8714762657302").build();
		assertThrows(IllegalArgumentException.class, () -> transactionService.moneyTransfer("731163625713", "2214762657302", transfer));
		
	}
	
}
