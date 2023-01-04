package com.bank.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.bank.dto.CreditDebit;
import com.bank.dto.MoneyTransfer;
import com.bank.entity.Account;
import com.bank.entity.AccountType;
import com.bank.entity.Address;
import com.bank.entity.Customer;
import com.bank.entity.Transaction;
import com.bank.entity.TransactionType;
import com.bank.repository.AccountRepository;
import com.bank.repository.CustomerRepository;
import com.bank.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private CustomerRepository customerRepository;

	@MockBean
	private AccountRepository accountRepository;

	@MockBean
	private TransactionRepository transactionRepository;

	Address address = new Address("34-9", "hyd", "ts", "987789");
	Customer customer1 = new Customer("731163625713", "teja", "2002-10-20", "9283773654", "teja@gmail.com",
			"987678098076", address);
	Customer customer3 = new Customer("831163625713", "teja1", "2002-10-10", "9283773654", "teja@gmail.com",
			"987678098076", address);

	Customer customer2 = new Customer("731163625714", "sai", "2002-10-20", "9883773654", "sai@gmail.com",
			"777678098076", address);
	Account account1 = new Account("63aae0328a1acb3d34d568f5", "731163625713", "3714762657302", AccountType.SAVING,
			"SBI21315", 25000.0, true);
	Account account2 = new Account("63aae0328a1acb3d34d679f6", "731163625713", "9814762657301", AccountType.SALARY,
			"SBI21315", 35000.0, true);
	Account account3 = new Account("63aae0328a1acb3d34d679f6", "831163625713", "5414762657301", AccountType.SALARY,
			"SBI21315", 35000.0, false);
	Instant date1 = Instant.parse("2022-12-04T17:21:18.139Z");
	Instant date2 = Instant.parse("2022-12-03T17:21:18.139Z");
	Transaction transaction1 = new Transaction("63ac7b0ec00a170f750646b8", "731163625713", "3714762657302", date1,
			25000.0, TransactionType.DEPOSIT);
	Transaction transaction2 = new Transaction("63ac7b0ec00a170f750646b9", "731163625713", "3714762657302", date2,
			27000.0, TransactionType.DEPOSIT);

	@Test
	void getByIdTest() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		Mockito.when(transactionRepository.findById("63ac7b0ec00a170f750646b8")).thenReturn(Optional.of(transaction1));
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/v1/customers/731163625713/accounts/3714762657302/transactions/63ac7b0ec00a170f750646b8")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.amount", is(25000.0)));
	}

	// invlalid account number
	@Test
	void invalidGetByIdTest() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		Mockito.when(transactionRepository.findById("63ac7b0ec00a170f750646b8")).thenReturn(Optional.of(transaction1));
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/v1/customers/731163625713/accounts/8714762657302/transactions/63ac7b0ec00a170f750646b8")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	// customer not match account
	@Test
	void invalidCustomerIdTest() throws Exception {
		Mockito.when(customerRepository.findById("831163625713")).thenReturn(Optional.of(customer3));
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		Mockito.when(transactionRepository.findById("63ac7b0ec00a170f750646b8")).thenReturn(Optional.of(transaction1));
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/v1/customers/831163625713/accounts/3714762657302/transactions/63ac7b0ec00a170f750646b8")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	@Test
	void invalidAccountNumberListTest() throws Exception {
		Mockito.when(customerRepository.findById("831163625713")).thenReturn(Optional.of(customer3));
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/831163625713/accounts/3714762657302/transactions")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	@Test
	void getRecentTest() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/customers/731163625713/accounts/3714762657302/transactions/recent")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	// invalid account
	@Test
	void invalidAccountGetRecentTest() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/customers/731163625713/accounts/8714762657302/transactions/recent")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	// customer not match account
	@Test
	void invalidCustomerIdGetRecentTest() throws Exception {
		Mockito.when(customerRepository.findById("831163625713")).thenReturn(Optional.of(customer3));
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/customers/831163625713/accounts/3714762657302/transactions/recent")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	void depositTest() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		CreditDebit dto = new CreditDebit(25000);
		String content = objectMapper.writeValueAsString(dto);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/731163625713/accounts/3714762657302/transactions/deposit").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.amount", is(25000.0)));
	}

	// customer not match account
	@Test
	void invalidCustomerIdDepositTest() throws Exception {
		Customer customer4 = new Customer("221163625713", "teja", "2002-10-20", "9283773654", "teja@gmail.com",
				"987678098076", address);
		Customer customer = new Customer("731163625713", "teja", "2002-10-20", "9283773654", "teja@gmail.com",
				"987678098076", address);
		Account account = new Account("63aae0328a1acb3d34d568f5", "731163625713", "3714762657302", AccountType.SAVING,
				"SBI21315", 25000.0, true);
		Mockito.when(customerRepository.findById("221163625713")).thenReturn(Optional.of(customer4));
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account);
		CreditDebit dto = new CreditDebit(25000);
		String content = objectMapper.writeValueAsString(dto);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/v1/customers/221163625713/accounts/3714762657302/transactions/deposit").content(content)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(mockRequest).andExpect(status().isBadRequest());
	}

	// account is null
	@Test
	void invalidAccountNumberDepositTest() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("8814762657302")).thenReturn(null);
		CreditDebit dto = new CreditDebit(25000);
		String content = objectMapper.writeValueAsString(dto);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/v1/customers/731163625713/accounts/8814762657302/transactions/deposit").content(content)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(mockRequest).andExpect(status().isBadRequest());
	}

	// in active account
	@Test
	void inActiveAccountDepositTest() throws Exception {
		Mockito.when(customerRepository.findById("831163625713")).thenReturn(Optional.of(customer3));
		Mockito.when(accountRepository.findByAccountNumber("5414762657301")).thenReturn(account3);
		CreditDebit dto = new CreditDebit(25000);
		String content = objectMapper.writeValueAsString(dto);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/v1/customers/831163625713/accounts/5414762657301/transactions/deposit").content(content)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(mockRequest).andExpect(status().isBadRequest());
	}

	@Test
	void withdrawalTest() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		CreditDebit dto = new CreditDebit(25000);
		String content = objectMapper.writeValueAsString(dto);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/731163625713/accounts/3714762657302/transactions/deposit").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.amount", is(25000.0)));
	}

	// customer not match account
	@Test
	void invalidCustomerIdWithdrawalTest() throws Exception {
		Customer customer = new Customer("731163625713", "teja", "2002-10-20", "9283773654", "teja@gmail.com",
				"987678098076", null);
		Account account = new Account("63aae0328a1acb3d34d568f5", "731163625713", "3714762657302", AccountType.SAVING,
				"SBI21315", 25000.0, true);
		Mockito.when(customerRepository.findById("831163625713")).thenReturn(Optional.of(customer3));
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account);
		CreditDebit dto = new CreditDebit(25000);
		String content = objectMapper.writeValueAsString(dto);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/831163625713/accounts/3714762657302/transactions/withdrawal").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	// account is null
	@Test
	void invalidAccountNumberWithdrawalTest() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("8814762657302")).thenReturn(null);
		CreditDebit dto = new CreditDebit(25000);
		String content = objectMapper.writeValueAsString(dto);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/731163625713/accounts/8814762657302/transactions/withdrawal").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	// in active account
	@Test
	void inActiveAccountWithdrawalTest() throws Exception {
		Mockito.when(customerRepository.findById("831163625713")).thenReturn(Optional.of(customer3));
		Mockito.when(accountRepository.findByAccountNumber("5414762657301")).thenReturn(account3);
		CreditDebit dto = new CreditDebit(25000);
		String content = objectMapper.writeValueAsString(dto);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/831163625713/accounts/5414762657301/transactions/withdrawal").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	// insufficient balance
	@Test
	void insuffucientBalanceWithdrawalTest() throws Exception {
		Account account5 = new Account("63aae0328a1acb3d34d568f5", "831163625713", "4414762657302", AccountType.SAVING,
				"SBI21315", 0, true);
		Mockito.when(customerRepository.findById("831163625713")).thenReturn(Optional.of(customer3));
		Mockito.when(accountRepository.findByAccountNumber("4414762657302")).thenReturn(account5);
		CreditDebit dto = new CreditDebit(25000);
		String content = objectMapper.writeValueAsString(dto);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/831163625713/accounts/4414762657302/transactions/withdrawal").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	void transfer() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		Mockito.when(accountRepository.findByAccountNumber("9814762657301")).thenReturn(account2);
		MoneyTransfer dto = new MoneyTransfer(34000.0, "9814762657301");
		String content = objectMapper.writeValueAsString(dto);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/731163625713/accounts/3714762657302/transactions/transfer").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	// sender account is not active
	@Test
	void invactiveSenderAccountTransfer() throws Exception {
		Mockito.when(customerRepository.findById("831163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("5414762657301")).thenReturn(account3);
		Mockito.when(accountRepository.findByAccountNumber("9814762657301")).thenReturn(account2);
		MoneyTransfer dto = new MoneyTransfer(34000.0, "9814762657301");
		String content = objectMapper.writeValueAsString(dto);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/831163625713/accounts/5414762657301/transactions/transfer").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	// sender account is not active
	@Test
	void invactiveReceiverAccountTransfer() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		Mockito.when(accountRepository.findByAccountNumber("5414762657301")).thenReturn(account3);
		MoneyTransfer dto = new MoneyTransfer(34000.0, "5414762657301");
		String content = objectMapper.writeValueAsString(dto);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/731163625713/accounts/3714762657302/transactions/transfer").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	void listTest() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/731163625713/accounts/3714762657302/transactions")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	// wrong transaction id
	@Test
	void InvalidId() throws Exception {
		Customer customer = new Customer("731163625713", "teja", "2002-10-20", "9283773654", "teja@gmail.com",
				"987678098076", null);
		Account account = new Account("63aae0328a1acb3d34d568f5", "731163625713", "3714762657302", AccountType.SAVING,
				"SBI21315", 25000.0, true);
		Instant date = Instant.parse("2022-12-28T17:21:18.139Z");
		Transaction transaction = new Transaction("63ac7b0ec00a170f750646b9", "731163625713", "3714762657302", date,
				27000.0, TransactionType.DEPOSIT);
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account);
		Mockito.when(transactionRepository.findById("63ac7b0ec00a170f750646b9")).thenReturn(Optional.of(transaction));
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/v1/customers/731163625713/accounts/3714762657302/transactions/13ac7b0ec00a170f750646b9")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	// wrong acount number
	@Test
	void listWithInvalidAccountNumber() throws Exception {
		Customer customer = new Customer("731163625713", "teja", "2002-10-20", "9283773654", "teja@gmail.com",
				"987678098076", null);
		Account account = new Account("63aae0328a1acb3d34d568f5", "731163625713", "3714762657302", AccountType.SAVING,
				"SBI21315", 25000.0, true);
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/731163625713/accounts/9714762657302/transactions")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	// depositing amount 0
	@Test
	void invalidAmountForDeposit() throws Exception {
		Customer customer = new Customer("731163625713", "teja", "2002-10-20", "9283773654", "teja@gmail.com",
				"987678098076", null);
		Account account = new Account("63aae0328a1acb3d34d568f5", "731163625713", "3714762657302", AccountType.SAVING,
				"SBI21315", 25000.0, true);
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account);
		CreditDebit debit = new CreditDebit(0);
		String content = objectMapper.writeValueAsString(debit);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/731163625713/accounts/3714762657302/transactions/deposit").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

	}

	// withdrawing amount 0
	@Test
	void invalidAmountForWithDrawal() throws Exception {
		Customer customer = new Customer("731163625713", "teja", "2002-10-20", "9283773654", "teja@gmail.com",
				"987678098076", null);
		Account account = new Account("63aae0328a1acb3d34d568f5", "731163625713", "3714762657302", AccountType.SAVING,
				"SBI21315", 25000.0, true);
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account);
		CreditDebit debit = new CreditDebit(0);
		String content = objectMapper.writeValueAsString(debit);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/731163625713/accounts/3714762657302/transactions/withdrawal").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

	}

	// transferring amount 0
	@Test
	void invalidAmountForTransfer() throws Exception {
		Customer customer = new Customer("731163625713", "teja", "2002-10-20", "9283773654", "teja@gmail.com",
				"987678098076", null);
		Account account = new Account("63aae0328a1acb3d34d568f5", "731163625713", "3714762657302", AccountType.SAVING,
				"SBI21315", 25000.0, true);
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account);
		MoneyTransfer transfer = new MoneyTransfer(0, "9814762657301");
		String content = objectMapper.writeValueAsString(transfer);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/731163625713/accounts/3714762657302/transactions/transfer").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

	}

	@Test
	void deleteTest() throws Exception {
		Customer customer = new Customer("731163625713", "teja", "2002-10-20", "9283773654", "teja@gmail.com",
				"987678098076", null);
		Account account = new Account("63aae0328a1acb3d34d568f5", "731163625713", "3714762657302", AccountType.SAVING,
				"SBI21315", 25000.0, true);
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account);
		mockMvc.perform(MockMvcRequestBuilders
				.delete("/api/v1/customers/731163625713/accounts/3714762657302/transactions/delete")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	// account is null
	@Test
	void invalidAccountDeleteTest() throws Exception {
		Customer customer = new Customer("731163625713", "teja", "2002-10-20", "9283773654", "teja@gmail.com",
				"987678098076", null);
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(null);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.delete("/api/v1/customers/731163625713/accounts/3714762657302/transactions/delete");
		mockMvc.perform(mockRequest).andExpect(status().isBadRequest());
	}

	// invalid customer id
	@Test
	void invalidCustomerIdDeleteTest() throws Exception {
		Customer customer = new Customer("731163625713", "teja", "2002-10-20", "9283773654", "teja@gmail.com",
				"987678098076", null);
		Account account = new Account("63aae0328a1acb3d34d568f5", "731163625713", "3714762657302", AccountType.SAVING,
				"SBI21315", 25000.0, true);
		Mockito.when(customerRepository.findById("831163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account);
		mockMvc.perform(MockMvcRequestBuilders
				.delete("/api/v1/customers/831163625713/accounts/3714762657302/transactions/delete")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}
}
