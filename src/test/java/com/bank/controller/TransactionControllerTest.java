package com.bank.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.bank.dto.CreditDebit;
import com.bank.dto.MoneyTransfer;
import com.bank.entity.Account;
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
	
	Customer customer1 = new Customer("731163625713", "teja", "2002-10-20", "9283773654", "teja@gmail.com", "987678098076",
			null);
	Customer customer2 = new Customer("731163625714", "sai", "2002-10-20", "9883773654", "sai@gmail.com", "777678098076",
			null);
	Account account1 = new Account("63aae0328a1acb3d34d568f5", "731163625713", "3714762657302", "saving", "SBI21315",
			25000.0, true);
	Account account2 = new Account("63aae0328a1acb3d34d679f6", "731163625713", "9814762657301", "salary", "SBI21315",
			35000.0, true);
	Instant date1 = Instant.parse("2022-12-28T17:21:18.139Z");
	Instant date2 = Instant.parse("2022-12-27T17:21:18.139Z");
	Transaction transaction1 = new Transaction("63ac7b0ec00a170f750646b8", "731163625713", "3714762657302", date1, 25000.0, TransactionType.DEPOSIT); 
	Transaction transaction2 = new Transaction("63ac7b0ec00a170f750646b9", "731163625713", "3714762657302", date2, 27000.0, TransactionType.DEPOSIT);
	@Test
	void getByIdTest() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		Mockito.when(transactionRepository.findById("63ac7b0ec00a170f750646b8")).thenReturn(Optional.of(transaction1));
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/v1/customers/731163625713/accounts/3714762657302/transactions/63ac7b0ec00a170f750646b8")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpectAll(status().isOk());
	}
	
	@Test
	void listTest() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/v1/customers/731163625713/accounts/3714762657302/transactions")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	void getRecentTransactionsTest() throws Exception {
		List<Transaction> list = Arrays.asList(transaction1, transaction2);
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		Mockito.when(transactionRepository.findByAccountNumber("3714762657302")).thenReturn(list);
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/v1/customers/731163625713/accounts/3714762657302/transactions/recent")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].id", is("63ac7b0ec00a170f750646b8")))
				.andExpect(jsonPath("$[1].id", is("63ac7b0ec00a170f750646b9")))
				.andExpect(status().isOk());
	}
	
	@Test
	void depositTest() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		CreditDebit dto = new CreditDebit(25000);
		String content = objectMapper.writeValueAsString(dto);
		Mockito.when(transactionRepository.save(transaction1)).thenReturn(transaction1);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/731163625713/accounts/3714762657302/transactions/deposit")
				.content(content)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	void withdrawalTest() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		CreditDebit dto = new CreditDebit(25000);
		String content = objectMapper.writeValueAsString(dto);
		Mockito.when(transactionRepository.save(transaction1)).thenReturn(transaction1);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/731163625713/accounts/3714762657302/transactions/deposit")
				.content(content)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	void transfer() throws Exception {
		List<Transaction> list = Arrays.asList(transaction1, transaction2);
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		Mockito.when(customerRepository.findById("731163625714")).thenReturn(Optional.of(customer2));
		Mockito.when(accountRepository.findByAccountNumber("9814762657301")).thenReturn(account2);
		MoneyTransfer dto = new MoneyTransfer(34000.0, "3714762657302");
		String content = objectMapper.writeValueAsString(dto);
		Mockito.when(transactionRepository.save(transaction1)).thenReturn(transaction1);
		Mockito.when(transactionRepository.save(transaction2)).thenReturn(transaction2);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/731163625713/accounts/3714762657302/transactions/deposit")
				.content(content)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

}














