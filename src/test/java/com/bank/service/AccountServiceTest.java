package com.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.bank.entity.Account;
import com.bank.entity.AccountType;
import com.bank.entity.Address;
import com.bank.entity.Customer;
import com.bank.repository.AccountRepository;
import com.bank.repository.CustomerRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountServiceTest {

	@Autowired
	private AccountService accountService;

	@MockBean
	private CustomerRepository customerRepository;

	@MockBean
	private AccountRepository accountRepository;

	Address address = Address.builder().city("hyd").houseNumber("56/7").pincode("456543").state("ts").build();
	Customer customer1 = Customer.builder().customerId("731163625713").name("teja").dob("2002-10-20")
			.phone("9283773654").email("teja@gmail.com").aadhar("987678098076").address(address).build();
	Account account1 = Account.builder().aid("63aae0328a1acb3d34d568f5").customerId("731163625713")
			.accountNumber("3714762657302").type(AccountType.SAVING).ifscCode("SBI21315").accountBalance(35000.0)
			.active(true).build();
	Account account2 = Account.builder().aid("63aae0328a1acb3d34d679f6").customerId("731163625713")
			.accountNumber("9814762657301").type(AccountType.SALARY).ifscCode("SBI21315").accountBalance(35000.0)
			.active(false).build();

	@Test
	void testCreateAccount() throws Exception {
		Mockito.when(accountService.create(account1)).thenReturn(account1);
		assertEquals("SBI21315", account1.getIfscCode());
	}

	@Test
	void testListAccount() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		List<Account> account = new ArrayList<>();
		account.add(account1);
		account.add(account2);
		Mockito.when(accountService.list("731163625713")).thenReturn(account);
		assertEquals(2, account.size());
	}

	@Test
	void testGetByAccountNumber() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		Mockito.when(accountService.getByAccountNumber("731163625713", "3714762657302")).thenReturn(account1);
		assertEquals(35000.0, account1.getAccountBalance());
	}

	@Test
	void testActivateAccount() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("9814762657301")).thenReturn(account2);
		Mockito.when(accountService.activate("731163625713", "9814762657301")).thenReturn(account2);
		assertEquals(true, account1.isActive());
	}
	
	@Test
	void testDeActivateAccount() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		Mockito.when(accountService.deactivate("731163625713", "3714762657302")).thenReturn(account1);
		assertEquals(false, account1.isActive());
	}

	@Test
	void testDeleteAccount() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		Account account = accountService.delete("731163625713", "3714762657302");
		assertEquals("SBI21315", account.getIfscCode());
	}
	
}
