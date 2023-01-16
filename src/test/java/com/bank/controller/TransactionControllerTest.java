package com.bank.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
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
import com.bank.entity.AccountType;
import com.bank.entity.Address;
import com.bank.entity.Customer;
import com.bank.entity.Transaction;
import com.bank.entity.TransactionType;
import com.bank.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private TransactionService transactionService;

	Address address = Address.builder().houseNumber("34-9").city("hyd").state("ts").pincode("987789").build();
	Customer customer1 = Customer.builder().customerId("731163625713").name("teja").dob("2002-11-05")
			.phone("9283773654").email("teja@gmail.com").aadhar("987678098076").address(address).build();
	Customer customer3 = Customer.builder().customerId("831163625713").name("tejasango").dob("2002-10-10")
			.phone("9283773654").email("teja@gmail.com").aadhar("987678098076").address(address).build();
	Customer customer2 = Customer.builder().customerId("731163625714").name("sai").dob("2002-10-20").phone("9883773654")
			.email("sai@gmail.com").aadhar("777678098076").address(address).build();
	Account account1 = Account.builder().customerId("731163625713")
			.accountNumber("3714762657302").type(AccountType.SAVINGS).ifscCode("SBI21315").accountBalance(25000.0)
			.active(true).build();
	Account account2 = Account.builder().customerId("731163625713")
			.accountNumber("8714762657302").type(AccountType.SAVINGS).ifscCode("SBI21315").accountBalance(25000.0)
			.active(true).build();
	Account account3 = Account.builder().customerId("831163625713")
			.accountNumber("5414762657301").type(AccountType.SALARY).ifscCode("SBI21315").accountBalance(35000.0)
			.active(false).build();
	Instant date1 = Instant.parse("2022-12-04T17:21:18.139Z");
	Instant date2 = Instant.parse("2022-12-03T17:21:18.139Z");
	Transaction transaction1 = Transaction.builder().id(2337382L).customerId("731163625713")
			.accountNumber("3714762657302").date(date1).amount(2500.0).type(TransactionType.DEPOSIT).build();
	Transaction transaction2 = Transaction.builder().id(3442121L).customerId("731163625713")
			.accountNumber("3714762657302").date(date2).amount(2700.0).type(TransactionType.WITHDRAW).build();

	
	@Test
	void depositTest() throws Exception {
		CreditDebit dto = CreditDebit.builder().amount(2500).build();
		Mockito.when(transactionService.deposit(eq("731163625713") ,eq("3714762657302"), any(CreditDebit.class))).thenReturn(transaction1);
		String content = objectMapper.writeValueAsString(dto);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/731163625713/accounts/3714762657302/transactions/deposit").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.amount", is(2500.0)));
	}

	@Test
	void withdrawalTest() throws Exception {
		CreditDebit dto = CreditDebit.builder().amount(2700).build();
		Mockito.when(transactionService.withdrawal(eq("731163625713") ,eq("3714762657302"), any(CreditDebit.class))).thenReturn(transaction2);
		String content = objectMapper.writeValueAsString(dto);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/731163625713/accounts/3714762657302/transactions/withdrawal").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.amount", is(2700.0)));
	}
	
	@Test
	void transfer() throws Exception {
		MoneyTransfer dto = MoneyTransfer.builder().amount(3500.0).receiver("8714762657302").build();
		List<Transaction> list = Arrays.asList(transaction1, transaction2);
		Mockito.when(transactionService.moneyTransfer(eq("731163625713") ,eq("3714762657302"), any(MoneyTransfer.class))).thenReturn(list);
		String content = objectMapper.writeValueAsString(dto);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/731163625713/accounts/3714762657302/transactions/transfer").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
	
	@Test
	void getRecentTest() throws Exception {
		List<Transaction> list = Arrays.asList(transaction1, transaction2);
		Mockito.when(transactionService.getRecentTransactions("731163625713", "3714762657302")).thenReturn(list);
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/customers/731163625713/accounts/3714762657302/transactions/recent")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].amount", is(2500.0)));
	}
	
	@Test
	void listTest() throws Exception {
		List<Transaction> list = Arrays.asList(transaction1, transaction2);
		Mockito.when(transactionService.list("731163625713", "3714762657302")).thenReturn(list);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/731163625713/accounts/3714762657302/transactions")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$[1].amount", is(2700.0)));
	}

	
	@Test
	void getByIdTest() throws Exception {
		Mockito.when(transactionService.getById("731163625713", "3714762657302", 2337382L)).thenReturn(transaction1);
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/v1/customers/731163625713/accounts/3714762657302/transactions/2337382")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.amount", is(2500.0)));
	}

	@Test
	void deleteTest() throws Exception {
		Mockito.when(transactionService.deleteByAccountNumber("731163625713", "3714762657302")).thenReturn("Deleted Successfully");
		mockMvc.perform(MockMvcRequestBuilders
				.delete("/api/v1/customers/731163625713/accounts/3714762657302/transactions/delete")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
	
}
