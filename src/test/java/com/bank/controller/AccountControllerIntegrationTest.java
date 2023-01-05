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
import com.bank.dto.AccountDto;
import com.bank.entity.Account;
import com.bank.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = BankApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
@Slf4j
class AccountControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private ObjectMapper objectMapper;

//	Account account = new Account("63aae0328a1acb3d34d568f5", "731163625713", "853678380273", AccountType.SAVING,
//			"SBI21315", 25000.0, true);

	private String cid = "7615855264001";
	private String aid = "";
	
	@Test
	@Order(1)
	void createTest() throws Exception {
		AccountDto account = new AccountDto("7615855264001", "saving", "SBI21315", 78000.0);
		String content = objectMapper.writeValueAsString(account);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customers/"+cid+"/accounts")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());
	}

	@Test
	@Order(2)
	void listTest() throws Exception {
		List<Account> list = accountRepository.findAll();
		log.info("accounts list : " + list);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/"+cid+"/accounts")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	@Order(3)
	void getTest() throws Exception {
		List<Account> list = accountRepository.findAll();
		Account account;
		account = list.get(list.size() - 1);
		aid = account.getAccountNumber();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/"+cid+"/accounts/"+aid))
				.andExpect(status().isOk());
	}

	@Test
	@Order(4)
	void deActivateTest() throws Exception {
		List<Account> list = accountRepository.findAll();
		Account account;
		account = list.get(list.size() - 1);
		aid = account.getAccountNumber();
		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/customers/"+cid+"/accounts/"+aid+"/deactivate"))
				.andExpect(status().isOk());
	}

	@Test
	@Order(5)
	void ativateTest() throws Exception {
		List<Account> list = accountRepository.findAll();
		Account account;
		account = list.get(list.size() - 1);
		aid = account.getAccountNumber();
		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/customers/"+cid+"/accounts/"+aid+"/activate"))
				.andExpect(status().isOk());
	}

	@Test
	@Order(6)
	void deleteTest() throws Exception {
		List<Account> list = accountRepository.findAll();
		Account account;
		account = list.get(list.size() - 1);
		aid = account.getAccountNumber();
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/customers/"+cid+"/accounts/"+aid)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

}
