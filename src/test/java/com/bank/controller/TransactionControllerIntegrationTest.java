package com.bank.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.List;
import org.junit.jupiter.api.Order;
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
import com.bank.entity.Transaction;
import com.bank.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = BankApplication.class)
@TestPropertySource(locations = "classpath:application.properties")
@AutoConfigureMockMvc
class TransactionControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private TransactionRepository transactionRepo;

	private String cid = "731163625713";
	private String aid = "1621177018108";
	private String tid = "";
	
	@Test
	@Order(1)
	void depositTest() throws Exception {
		CreditDebit deposit = new CreditDebit(23000.0);
		String content = objectMapper.writeValueAsString(deposit);
		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/customers/"+cid+"/accounts/"+aid+"/transactions/deposit")
						.content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.amount", is(23000.0)));
	}

	@Test
	@Order(2)
	void getRecentTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/"+cid+"/accounts/"+aid+"/transactions/recent")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	@Order(3)
	void withdrawalTest() throws Exception {
		CreditDebit withdraw = new CreditDebit(21000);
		String content = objectMapper.writeValueAsString(withdraw);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/"+cid+"/accounts/"+aid+"/transactions/withdrawal").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.amount", is(21000.0)));
	}

	@Test
	@Order(4)
	void transferTest() throws Exception {
		MoneyTransfer transfer = new MoneyTransfer(3200.0, "1621177018108");
		String content = objectMapper.writeValueAsString(transfer);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/"+cid+"/accounts/"+aid+"/transactions/transfer").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
	
	@Test
	@Order(5)
	void getByAccountNumber() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/"+cid+"/accounts/"+aid+"/transactions")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
		
	}
	
	@Test
	@Order(6)
	void getByIdTest() throws Exception {
		List<Transaction> list = transactionRepo.findByAccountNumber(aid);
		Transaction trans;
		trans= list.get(list.size() - 1);
		tid = trans.getId();
		System.out.println(tid);
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/v1/customers/"+cid+"/accounts/"+aid+"/transactions/"+tid)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

}
