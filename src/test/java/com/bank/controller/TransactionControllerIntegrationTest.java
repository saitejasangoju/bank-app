package com.bank.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.bank.BankApplication;
import com.bank.dto.CreditDebit;
import com.bank.dto.MoneyTransfer;
import com.bank.entity.Account;
import com.bank.entity.AccountType;
import com.bank.entity.Customer;
import com.bank.entity.Transaction;
import com.bank.entity.TransactionType;
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

	private static String cid = "821810274100";
	private static String aid = "1342120573283";
	private static String tid = "";

	@Test
	@Order(1)
	void depositTest() throws Exception {
		CreditDebit deposit = CreditDebit.builder().amount(21000.0).build();
		String content = objectMapper.writeValueAsString(deposit);
		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/customers/" + cid + "/accounts/" + aid + "/transactions/deposit")
						.content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.amount", is(21000.0)));
	}

	@Test
	@Order(2)
	void withdrawalTest() throws Exception {
		CreditDebit withdraw = CreditDebit.builder().amount(23000.0).build();
		String content = objectMapper.writeValueAsString(withdraw);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/" + cid + "/accounts/" + aid + "/transactions/withdrawal").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.amount", is(23000.0)));
	}

	@Test
	@Order(3)
	void transferTest1() throws Exception {
		MoneyTransfer transfer = MoneyTransfer.builder().amount(3200.0).receiver("124608332868").build();
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
				.andExpect(jsonPath("$[*].amount").value(Matchers.contains(21000.0, 23000.0, 3200.0)));
	}

	@Test
	@Order(5)
	void getListTransactions() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/" + cid + "/accounts/" + aid + "/transactions")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$[*].amount").value(Matchers.contains(21000.0, 23000.0, 3200.0)));

	}

	@Test
	@Order(6)
	void getByIdTest() throws Exception {
		List<Transaction> list = transactionRepo.findByAccountNumber(aid);
		Transaction transaction = list.get(list.size() - 1);
		tid = transaction.getId();
		System.out.println(tid);
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/customers/" + cid + "/accounts/" + aid + "/transactions/" + tid)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.customerId", is("821810274100")));
	}

	@Test
	@Order(7)
	void depositTest2() throws Exception {
		CreditDebit deposit = CreditDebit.builder().amount(21000.0).build();
		String content = objectMapper.writeValueAsString(deposit);
		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/customers/" + cid + "/accounts/" + aid + "/transactions/deposit")
						.content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.amount", is(21000.0)));
	}

	@Test
	@Order(8)
	void depositTest3() throws Exception {
		CreditDebit deposit = CreditDebit.builder().amount(21000.0).build();
		String content = objectMapper.writeValueAsString(deposit);
		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/customers/" + cid + "/accounts/" + aid + "/transactions/deposit")
						.content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.amount", is(21000.0)));
	}

	@Test
	@Order(9)
	void depositTest4() throws Exception {
		CreditDebit deposit = CreditDebit.builder().amount(21000.0).build();
		String content = objectMapper.writeValueAsString(deposit);
		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/customers/" + cid + "/accounts/" + aid + "/transactions/deposit")
						.content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.amount", is(21000.0)));
	}

	@Test
	@Order(10)
	void depositTest5() throws Exception {
		CreditDebit deposit = CreditDebit.builder().amount(21000.0).build();
		String content = objectMapper.writeValueAsString(deposit);
		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/customers/" + cid + "/accounts/" + aid + "/transactions/deposit")
						.content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.amount", is(21000.0)));
	}

	@Test
	@Order(11)
	void depositTest6() throws Exception {
		CreditDebit deposit = CreditDebit.builder().amount(21000.0).build();
		String content = objectMapper.writeValueAsString(deposit);
		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/customers/" + cid + "/accounts/" + aid + "/transactions/deposit")
						.content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.amount", is(21000.0)));
	}

	@Test
	@Order(12)
	void depositTest7() throws Exception {
		CreditDebit deposit = CreditDebit.builder().amount(21000.0).build();
		String content = objectMapper.writeValueAsString(deposit);
		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/customers/" + cid + "/accounts/" + aid + "/transactions/deposit")
						.content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.amount", is(21000.0)));
	}

	@Test
	@Order(13)
	void depositTest8() throws Exception {
		CreditDebit deposit = CreditDebit.builder().amount(21000.0).build();
		String content = objectMapper.writeValueAsString(deposit);
		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/customers/" + cid + "/accounts/" + aid + "/transactions/deposit")
						.content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.amount", is(21000.0)));
	}

	@Test
	@Order(14)
	void depositTest9() throws Exception {
		CreditDebit deposit = CreditDebit.builder().amount(21000.0).build();
		String content = objectMapper.writeValueAsString(deposit);
		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/v1/customers/" + cid + "/accounts/" + aid + "/transactions/deposit")
						.content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.amount", is(21000.0)));
	}

	@Test
	@Order(15)
	void getRecentAbove10Test() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/customers/" + cid + "/accounts/" + aid + "/transactions/recent")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$[*].amount").value(Matchers.contains(23000.0, 3200.0,
						21000.0, 21000.0, 21000.0, 21000.0, 21000.0, 21000.0, 21000.0, 21000.0)));
	}

	@Test
	@Order(16)
	void invalidGetByIdTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/v1/customers/" + cid + "/accounts/" + aid + "/transactions/63ac7b0ec00a170f750646b8")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	@Test
	@Order(17)
	void invalidCustomerIdTest() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/customers/831163625713/accounts/" + aid + "/transactions/" + tid)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(18)
	void invalidAccountNumberListTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/" + cid + "/accounts/3714762657302/transactions")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	@Test
	@Order(19)
	void invalidAccountGetRecentTest() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/customers/" + cid + "/accounts/8714762657302/transactions/recent")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	// customer not match account
	@Test
	@Order(20)
	void invalidCustomerIdGetRecentTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/8932423874/accounts/" + aid + "/transactions/recent")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	// customer not match account
	@Test
	@Order(21)
	void invalidCustomerIdDepositTest() throws Exception {
		CreditDebit dto = CreditDebit.builder().amount(2500).build();
		String content = objectMapper.writeValueAsString(dto);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/v1/customers/221163625713/accounts/" + aid + "/transactions/deposit").content(content)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(mockRequest).andExpect(status().isBadRequest());
	}

	// account is null
	@Test
	@Order(22)
	void invalidAccountNumberDepositTest() throws Exception {
		CreditDebit dto = CreditDebit.builder().amount(2500).build();
		String content = objectMapper.writeValueAsString(dto);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/v1/customers/" + cid + "/accounts/8814762657302/transactions/deposit").content(content)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(mockRequest).andExpect(status().isBadRequest());
	}

	// in active account
	@Test
	@Order(23)
	void inActiveAccountDepositTest() throws Exception {
		CreditDebit dto = CreditDebit.builder().amount(2500).build();
		String content = objectMapper.writeValueAsString(dto);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/v1/customers/321428876400/accounts/325301555884/transactions/deposit").content(content)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(mockRequest).andExpect(status().isBadRequest());
	}

	// customer not match account
	@Test
	@Order(24)
	void invalidCustomerIdWithdrawalTest() throws Exception {
		CreditDebit dto = CreditDebit.builder().amount(2500).build();
		String content = objectMapper.writeValueAsString(dto);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/8732873287328/accounts/" + aid + "/transactions/withdrawal").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	// account is null
	@Test
	@Order(25)
	void invalidAccountNumberWithdrawalTest() throws Exception {
		CreditDebit dto = CreditDebit.builder().amount(2500).build();
		String content = objectMapper.writeValueAsString(dto);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/" + cid + "/accounts/8814762657302/transactions/withdrawal").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	// in active account
	@Test
	@Order(26)
	void inActiveAccountWithdrawalTest() throws Exception {
		CreditDebit dto = CreditDebit.builder().amount(2500).build();
		String content = objectMapper.writeValueAsString(dto);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/321428876400/accounts/325301555884/transactions/withdrawal").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	// insufficient balance
	@Test
	@Order(27)
	void insuffucientBalanceWithdrawalTest() throws Exception {
		CreditDebit dto = CreditDebit.builder().amount(2500).build();
		String content = objectMapper.writeValueAsString(dto);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/321428876400/accounts/325301555884/transactions/withdrawal").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	// sender account is not active
	@Test
	@Order(28)
	void invactiveSenderAccountTransfer() throws Exception {
		MoneyTransfer dto = MoneyTransfer.builder().amount(3500.0).receiver(aid).build();
		String content = objectMapper.writeValueAsString(dto);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/321428876400/accounts/325301555884/transactions/transfer").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	// sender account is not active
	@Test
	@Order(29)
	void invactiveReceiverAccountTransfer() throws Exception {
		MoneyTransfer dto = MoneyTransfer.builder().amount(3500.0).receiver("325301555884").build();
		String content = objectMapper.writeValueAsString(dto);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/" + cid + "/accounts/" + aid + "/transactions/transfer").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	// wrong transaction id
	@Test
	@Order(30)
	void InvalidId() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/v1/customers/" + cid + "/accounts/" + aid + "/transactions/13ac7b0ec00a170f750646b9")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	// wrong acount number
	@Test
	@Order(31)
	void listWithInvalidAccountNumber() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/" + cid + "/accounts/9714762657302/transactions")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	// depositing amount 0
	@Test
	@Order(32)
	void invalidAmountForDeposit() throws Exception {
		CreditDebit credit = CreditDebit.builder().amount(0).build();
		String content = objectMapper.writeValueAsString(credit);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/" + cid + "/accounts/" + aid + "/transactions/deposit").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

	}

	// withdrawing amount 0
	@Test
	@Order(33)
	void invalidAmountForWithDrawal() throws Exception {
		CreditDebit debit = CreditDebit.builder().amount(0).build();
		String content = objectMapper.writeValueAsString(debit);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/" + cid + "/accounts/" + aid + "/transactions/withdrawal").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

	}

	// transferring amount 0
	@Test
	@Order(34)
	void invalidAmountForTransfer() throws Exception {
		MoneyTransfer transfer = MoneyTransfer.builder().amount(0).receiver("124608332868").build();
		String content = objectMapper.writeValueAsString(transfer);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/" + cid + "/accounts/" + aid + "/transactions/transfer").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

	}

	// account is null
	@Test
	@Order(35)
	void invalidAccountDeleteTest() throws Exception {
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.delete("/api/v1/customers/" + cid + "/accounts/3714762657302/transactions/delete");
		mockMvc.perform(mockRequest).andExpect(status().isBadRequest());
	}

	// invalid customer id
	@Test
	@Order(36)
	void invalidCustomerIdDeleteTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.delete("/api/v1/customers/831163625713/accounts/" + aid + "/transactions/delete")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	@Test
	@Order(37)
	void deleteByAccountNumber1Test() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.delete("/api/v1/customers/" + cid + "/accounts/" + aid + "/transactions/delete")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	@Order(38)
	void deleteByAccountNumber2Test() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.delete("/api/v1/customers/321428876400/accounts/124608332868/transactions/delete")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

}
