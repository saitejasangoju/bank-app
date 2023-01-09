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
import com.bank.dto.AccountDto;
import com.bank.entity.Account;
import com.bank.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = BankApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class AccountControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private ObjectMapper objectMapper;

	private String cid = "821810274100";
	private static String aid = "";
	private AccountDto dtoAccount;

	@Test
	@Order(1)
	void createTest() throws Exception {
		dtoAccount = AccountDto.builder().customerId(cid).ifscCode("SBI21315").type("CURRENT").accountBalance(65000.0)
				.build();
		String content = objectMapper.writeValueAsString(dtoAccount);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customers/" + cid + "/accounts")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk()).andExpect(jsonPath("$.accountBalance", is(65000.0)));
	}

	@Test
	@Order(2)
	void listTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/" + cid + "/accounts")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$[1].accountBalance", is(65000.0)));
	}

	@Test
	@Order(3)
	void getTest() throws Exception {
		List<Account> list = accountRepository.findAll();
		Account account = list.get(list.size() - 1);
		aid = account.getAccountNumber();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/" + cid + "/accounts/" + aid)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.accountBalance", is(65000.0)));
	}

	@Test
	@Order(4)
	void deActivateTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/customers/" + cid + "/accounts/" + aid + "/deactivate"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.active", is(false)));
	}
	
	@Test
	@Order(5)
	void deActivateAccount() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.put("/api/v1/customers/" + cid + "/accounts/" + aid + "/deactivate").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	@Order(6)
	void notActiveAccount() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/v1/customers/" +cid+ "/accounts/" + aid).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(7)
	void ativateTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/customers/" + cid + "/accounts/" + aid + "/activate"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.active", is(true)));
	}

	@Test
	@Order(8)
	void InvalidCustomerIdDeActivateTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/customers/888163625713/accounts/" + aid + "/deactivate")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(9)
	void InvalidCustomerIdActivateTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/customers/888163625713/accounts/" + aid + "/activate")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(10)
	void nullAccountActivateTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/customers/" + cid + "/accounts/3714762657302/activate")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(11)
	void nullAccountDeActivateTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/customers/" + cid + "/accounts/3714762657302/deactivate")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	@Order(12)
	void activateAccountWhichAlreadyActive() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.put("/api/v1/customers/" + cid + "/accounts/" + aid + "/activate").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(13)
	void deleteAccountWithInvalidCustomerId() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.delete("/api/v1/customers/831163625713/accounts/" + aid).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test 
	@Order(14)
	void listAccountsWithInvalidCustomerId() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/v1/customers/831163625713/accounts").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	@Order(15)
	void createAccountWithInvalidCustomerId() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/831163625713/accounts").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	@Order(16)
	void deleteTest() throws Exception {
		List<Account> list = accountRepository.findAll();
		Account account = list.get(list.size() - 1);
		aid = account.getAccountNumber();
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/customers/" + cid + "/accounts/" + aid)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

}
