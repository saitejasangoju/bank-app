package com.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import com.bank.BankApplication;
import com.bank.entity.Account;
import com.bank.entity.AccountType;
import com.bank.repository.AccountRepository;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = BankApplication.class)
@TestPropertySource(locations = "classpath:application.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountServiceIntegrationTest {
	
	@Autowired
	private AccountService accountService;

	@Autowired
	private AccountRepository accountRepository;
	
	private String cid = "561778824517";
	private Account account;
	
	@Test
	@Order(1)
	void createTest() throws Exception {
		account = Account.builder().customerId(cid).ifscCode("SBI21315").type(AccountType.CURRENT).accountBalance(65000.0).active(true).build();
		Account newAccount = accountService.create(account);
		assertEquals(65000.0, newAccount.getAccountBalance());
		
	}
	
	@Test
	@Order(2)
	void listTest() throws Exception {
		List<Account> list = accountService.list(cid);
		assertEquals(3, list.size());
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
	void ativateTest() throws Exception {
		List<Account> list = accountRepository.findAll();
		Account acc = list.get(list.size() - 1);
		String accNumber = acc.getAccountNumber();
		Account activatedAccount = accountService.activate(cid, accNumber);
		assertEquals(true, activatedAccount.isActive());
	}

	@Test
	@Order(6)
	void deleteTest() throws Exception {
		List<Account> list = accountRepository.findAll();
		Account acc = list.get(list.size() - 1);
		String accNumber = acc.getAccountNumber();
		Account deletedAccount = accountService.delete(cid, accNumber);
		assertEquals("561778824517", deletedAccount.getCustomerId());
	}
	
}
