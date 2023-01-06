package com.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import com.bank.BankApplication;
import com.bank.entity.Account;
import com.bank.entity.AccountType;
import com.bank.entity.Address;
import com.bank.entity.Customer;
import com.bank.exception.CustomerNotMatchAccount;
import com.bank.repository.AccountRepository;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = BankApplication.class)
@TestPropertySource(locations = "classpath:application.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountServiceIntegrationTest {
	
	@Autowired
	private AccountService accountService;

	@Autowired
	private AccountRepository accountRepository;
	
	private String cid = "821810274100";
	private Account account;
	
	@Test
	@Order(1)
	void createAccountTest() throws Exception {
		account = Account.builder().customerId(cid).ifscCode("SBI21315").type(AccountType.CURRENT).accountBalance(65000.0).active(true).build();
		Account newAccount = accountService.create(account);
		assertEquals(65000.0, newAccount.getAccountBalance());
	}
	
	@Test
	@Order(2)
	void listAccountsTest() throws Exception {
		List<Account> list = accountService.list(cid);
		assertEquals(2, list.size());
	}

	@Test
	@Order(3)
	void getByAccountNumberTest() throws Exception {
		List<Account> list = accountRepository.findAll();
		Account acc = list.get(list.size() - 1);
		String accNumber = acc.getAccountNumber();
		Account getAccount = accountService.getByAccountNumber(cid, accNumber);
		assertEquals(65000.0, getAccount.getAccountBalance());
	}

	@Test
	@Order(4)
	void deActivateTest() throws Exception {
		List<Account> list = accountRepository.findAll();
		Account acc = list.get(list.size() - 1);
		String accNumber = acc.getAccountNumber();
		Account deActivatedAccount = accountService.deactivate(cid, accNumber);
		assertEquals(false, deActivatedAccount.isActive());
	}
	
	@Test
	@Order(5)
	void notActiveAccount() throws Exception {
		List<Account> list = accountRepository.findAll();
		Account acc = list.get(list.size() - 1);
		String accNumber = acc.getAccountNumber();
		assertThrows(NoSuchElementException.class, () -> accountService.getByAccountNumber(cid, accNumber));
	}

	@Test
	@Order(6)
	void ativateAccountTest() throws Exception {
		List<Account> list = accountRepository.findAll();
		Account acc = list.get(list.size() - 1);
		String accNumber = acc.getAccountNumber();
		Account activatedAccount = accountService.activate(cid, accNumber);
		assertEquals(true, activatedAccount.isActive());
	}
	
	@Test
	@Order(7)
	void invalidCustomerId() throws Exception {
		assertThrows(NoSuchElementException.class, () -> accountService.list("43633738766532"));
	}
	
	@Test
	@Order(8)
	void customerIdAndAccountNumberNotLinkedForGet() throws Exception {
		assertThrows(NoSuchElementException.class, () -> accountService.getByAccountNumber("43633738766532", "323232323232323"));
	}
	
	@Test
	@Order(10)
	void customerIdAndAccountNumberNotLinkedForDelete() throws Exception {
		List<Account> list = accountRepository.findAll();
		Account acc = list.get(list.size() - 1);
		String accNumber = acc.getAccountNumber();
		assertThrows(CustomerNotMatchAccount.class, () -> accountService.delete("321428876400", accNumber));
	}
	
	@Test
	@Order(11)
	void deleteAccountTest() throws Exception {
		List<Account> list = accountRepository.findAll();
		Account acc = list.get(list.size() - 1);
		String accNumber = acc.getAccountNumber();
		Account deletedAccount = accountService.delete(cid, accNumber);
		assertEquals("821810274100", deletedAccount.getCustomerId());
	}
	
}
