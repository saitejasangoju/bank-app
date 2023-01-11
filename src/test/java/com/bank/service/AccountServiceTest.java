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

import com.bank.dto.AccountDto;
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
	Customer customer1 = Customer.builder().customerId("381705176241").name("teja").dob("2002-10-20")
			.phone("9283773654").email("teja@gmail.com").aadhar("987678098076").address(address).build();
	Account account1 = Account.builder().customerId("381705176241")
			.accountNumber("3714762657302").type(AccountType.SAVINGS).ifscCode("SBI21315").accountBalance(35000.0)
			.active(true).build();
	Account account2 = Account.builder().customerId("381705176241")
			.accountNumber("9814762657301").type(AccountType.SALARY).ifscCode("SBI21315").accountBalance(35000.0)
			.active(false).build();
	AccountDto accountDto = AccountDto.builder().customerId("381705176241").accountBalance(35000.0).ifscCode("SBI21315").type(AccountType.SAVINGS).build();
	
	@Test
	void testCreateAccount() throws Exception {
		Mockito.when(customerRepository.findById("381705176241")).thenReturn(Optional.of(customer1));
		Mockito.when(accountService.create(accountDto)).thenReturn(account1);
		assertEquals("SBI21315", account1.getIfscCode());
	}

	@Test
	void testListAccount() throws Exception {
		Mockito.when(customerRepository.findById("381705176241")).thenReturn(Optional.of(customer1));
		List<Account> account = new ArrayList<>();
		account.add(account1);
		account.add(account2);
		Mockito.when(accountService.list("381705176241")).thenReturn(account);
		assertEquals(2, account.size());
	}

	@Test
	void testGetByAccountNumber() throws Exception {
		Mockito.when(customerRepository.findById("381705176241")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		Mockito.when(accountService.getByAccountNumber("381705176241", "3714762657302")).thenReturn(account1);
		assertEquals(35000.0, account1.getAccountBalance());
	}

	@Test
	void testActivateAccount() throws Exception {
		Mockito.when(customerRepository.findById("381705176241")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("9814762657301")).thenReturn(account2);
		Mockito.when(accountService.activate("381705176241", "9814762657301")).thenReturn(account2);
		assertEquals(true, account1.isActive());
	}
	
	@Test
	void testDeActivateAccount() throws Exception {
		Mockito.when(customerRepository.findById("381705176241")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		Mockito.when(accountService.deactivate("381705176241", "3714762657302")).thenReturn(account1);
		assertEquals(false, account1.isActive());
	}

	@Test
	void testDeleteAccount() throws Exception {
		Mockito.when(customerRepository.findById("381705176241")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		Account account = accountService.delete("381705176241", "3714762657302");
		assertEquals("SBI21315", account.getIfscCode());
	}
	
}
