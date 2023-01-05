package com.bank.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class TransactionControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TransactionRepository transactionRepo;

	private String cid = "665224040112";
	private String aid = "315346735831";
	private String tid = "";

	@Test
	@Order(1)
	void depositTest() throws Exception {
		CreditDebit deposit = new CreditDebit(23000.0);
		String content = objectMapper.writeValueAsString(deposit);
		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/customers/" + cid + "/accounts/" + aid + "/transactions/deposit")
						.content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.amount", is(23000.0)));
	}

	@Test
	@Order(2)
	void withdrawalTest() throws Exception {
		CreditDebit withdraw = new CreditDebit(21000);
		String content = objectMapper.writeValueAsString(withdraw);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/" + cid + "/accounts/" + aid + "/transactions/withdrawal").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.amount", is(21000.0)));
	}

	@Test
	@Order(3)
	void transferTest() throws Exception {
		MoneyTransfer transfer = new MoneyTransfer(3200.0, "315346735831");
		String content = objectMapper.writeValueAsString(transfer);
		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/customers/" + cid + "/accounts/" + aid + "/transactions/transfer")
						.content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$[*].amount").value(Matchers.contains(3200.0, 3200.0)));
	}

	@Test
	@Order(4)
	void getRecentTest() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/customers/" + cid + "/accounts/" + aid + "/transactions/recent")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].amount").value(Matchers.contains(23000.0, 21000.0, 3200.0, 3200.0)));
	}

	@Test
	@Order(5)
	void getByAccountNumber() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/" + cid + "/accounts/" + aid + "/transactions")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$[*].accountNumber")
						.value(Matchers.contains("315346735831", "315346735831", "315346735831", "315346735831")));

	}

	@Test
	@Order(6)
	void getByIdTest() throws Exception {
		List<Transaction> list = transactionRepo.findByAccountNumber(aid);
		Transaction trans;
		trans = list.get(list.size() - 1);
		tid = trans.getId();
		System.out.println(tid);
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/customers/" + cid + "/accounts/" + aid + "/transactions/" + tid)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.customerId", is("665224040112")));
	}

	@Test
	@Order(7)
	void deleteByAccountNumberTest() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.delete("/api/v1/customers/" + cid + "/accounts/" + aid + "/transactions/delete")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

}
