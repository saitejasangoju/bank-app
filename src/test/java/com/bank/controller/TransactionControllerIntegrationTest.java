package com.bank.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.bank.BankApplication;
import com.bank.dto.CreditDebit;
import com.bank.dto.MoneyTransfer;
import com.bank.entity.Account;
import com.bank.entity.AccountType;
import com.bank.entity.Address;
import com.bank.entity.Customer;
import com.bank.entity.Transaction;
import com.bank.entity.TransactionType;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = BankApplication.class)
@TestPropertySource(locations = "classpath:application.properties")
@AutoConfigureMockMvc
class TransactionControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	Address address = new Address("23/4", "hyd", "telangana", "987789");
	Customer customer = new Customer("731163625713", "teja", "2002-10-20", "9283773654", "teja@gmail.com",
			"987678098543", address);

	Account account = new Account("63aae0328a1acb3d34d568f5", "731163625713", "3714762657302", AccountType.SAVING,
			"SBI21315", 25000.0, true);
	Instant date = Instant.parse("2022-12-27T17:21:18.139Z");
	Transaction transaction = new Transaction("63ac7b0ec00a170f750646b8", "731163625713", "3714762657302", date,
			25000.0, TransactionType.DEPOSIT);

	
	@Test
	void depositTest() throws Exception {
		CreditDebit deposit = new CreditDebit(23000.0);
		String content = objectMapper.writeValueAsString(deposit);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/731163625713/accounts/3714762657302/transactions/deposit").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.amount", is(23000.0)));
	}
	
	@Test
	void getByIdTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/v1/customers/731163625713/accounts/3714762657302/transactions/63ac7f8fc00a170f750646c1")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.amount", is(18000.0)));
	}
	
	@Test
	void getByAccountNumber() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/731163625713/accounts/3714762657302/transactions")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	void getRecentTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/731163625713/accounts/3714762657302/transactions")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	void withdrawalTest() throws Exception {
		CreditDebit withdraw = new CreditDebit(21000);
		String content = objectMapper.writeValueAsString(withdraw);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/731163625713/accounts/3714762657302/transactions/withdrawal").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.amount", is(21000.0)));
	}

	@Test
	void transferTest() throws Exception {
		MoneyTransfer transfer = new MoneyTransfer(3200.0, "3714762657301");
		String content = objectMapper.writeValueAsString(transfer);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/731163625713/accounts/3714762657302/transactions/transfer").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}


}
