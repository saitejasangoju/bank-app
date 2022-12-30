package com.bank.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.bank.BankApplication;
import com.bank.entity.Account;
import com.bank.entity.AccountType;
import com.bank.entity.Customer;
import com.bank.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
				classes = BankApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
@Slf4j
public class AccountControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private ObjectMapper objectMapper;

	Account account1 = new Account("63aae0328a1acb3d34d568f5", "731163625713", "3714762657302", AccountType.SAVING, "SBI21315",
			25000.0, true);
	Account account2 = new Account("63aae0328a1acb3d34d679f6", "731163625713", "9814762657301", AccountType.SALARY, "SBI21315",
			35000.0, false);
	
	@Test
	void createTest() throws Exception {
		String content = objectMapper.writeValueAsString(account1);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/7615855264001/accounts")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(content))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accountBalance", is(25000.0)));
	}
	
	@Test
	void listTest() throws Exception {
		List<Account> list = accountRepository.findAll();
		log.info("accounts list : " + list);
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/v1/customers/7615855264001/accounts"))	
				.andExpect(status().isOk());
		}
	
	
	@Test
	void getTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/v1/customers/7615855264001/accounts/3714762657302"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.ifscCode", is("SBI21315")));
	}
	
	@Test
	void deActivateTest() throws Exception {
		account1.setActive(false);
		accountRepository.save(account1);
		mockMvc.perform(MockMvcRequestBuilders
				.put("/api/v1/customers/7615855264001/accounts/3714762657302/deactivate")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.active", is(false)));
	}
	
	@Test
	void ativateTest() throws Exception {
		account1.setActive(true);
		accountRepository.save(account1);
		mockMvc.perform(MockMvcRequestBuilders
				.put("/api/v1/customers/7615855264001/accounts/3714762657302/activate")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.active", is(true)));
	}
	
	@Test
	void deleteTest() throws Exception {
		accountRepository.delete(account1);
		mockMvc.perform(MockMvcRequestBuilders
				.delete("/api/v1/customers/7615855264001/accounts/3714762657302")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	

	
}
