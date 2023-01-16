package com.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.bank.BankApplication;
import com.bank.dto.AccountDto;
import com.bank.entity.Account;
import com.bank.entity.AccountType;
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
	
	private static String cid = "178214655563";
	private static String accNumber = "";
	private AccountDto account;
	
	@Test
	@Order(1)
	void createAccountTest() throws Exception {
		account = AccountDto.builder().customerId(cid).ifscCode("SBI21315").type(AccountType.CURRENT).accountBalance(65000.0).build();
		Account newAccount = accountService.create(account);
		assertEquals(65000.0, newAccount.getAccountBalance());
	}
	
	@Test
	@Order(2)
	void listAccountsTest() throws Exception {
		List<Account> list = accountService.list(cid);
		assertEquals(1, list.size());
	}

	@Test
	@Order(3)
	void getByAccountNumberTest() throws Exception {
		List<Account> list = accountRepository.findAll();
		Account acc = list.get(list.size() - 1);
		accNumber = acc.getAccountNumber();
		Account getAccount = accountService.getByAccountNumber(cid, accNumber);
		assertEquals(65000.0, getAccount.getAccountBalance());
	}
	
	@Test
	@Order(4)
	void customerNotMatchAccountNumberForDeActivate() throws Exception {
		assertThrows(NoSuchElementException.class, () -> accountService.deactivate(cid, "124608332868"));
	}
	
	@Test
	@Order(4)
	void customerNotMatchAccountNumberForActivate() throws Exception {
		assertThrows(NoSuchElementException.class, () -> accountService.activate(cid, "124608332868"));
	}
	
	@Test
	@Order(4)
	void deActivateTest() throws Exception {
		Account deActivatedAccount = accountService.deactivate(cid, accNumber);
		assertEquals(false, deActivatedAccount.isActive());
	}
	
	@Test
	@Order(5)
	void notActiveAccount() throws Exception {
		assertThrows(NoSuchElementException.class, () -> accountService.getByAccountNumber(cid, accNumber));
	}

	@Test
	@Order(6)
	void ativateAccountTest() throws Exception {
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
		assertThrows(NoSuchElementException.class, () -> accountService.delete("321428876400", accNumber));
	}
	
	@Test
	@Order(11)
	void deleteAccountTest() throws Exception {
		Account deletedAccount = accountService.delete(cid, accNumber);
		assertEquals("178214655563", deletedAccount.getCustomerId());
	}
	
}
