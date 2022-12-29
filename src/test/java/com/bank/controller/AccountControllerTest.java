package com.bank.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.bank.entity.Account;
import com.bank.entity.Customer;
import com.bank.repository.AccountRepository;
import com.bank.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private AccountRepository accountRepository;
	
	@MockBean
	private CustomerRepository customerRepository;
	
	Customer customer1 = new Customer("731163625713", "teja", "2002-10-20", "9283773654", "teja@gmail.com", "987678098076",
			null);

	Account account1 = new Account("63aae0328a1acb3d34d568f5", "731163625713", "3714762657302", "saving", "SBI21315",
			25000.0, true);
	Account account2 = new Account("63aae0328a1acb3d34d679f6", "731163625713", "9814762657301", "salary", "SBI21315",
			35000.0, false);

	@Test
	void listTest() throws Exception {
		List<Account> list = Arrays.asList(account1, account2);
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findAll()).thenReturn(list);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/731163625713/accounts")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].accountType", is("saving")));
	}
	
	@Test
	void getByAccountNumberTest() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/v1/customers/731163625713/accounts/3714762657302")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void createTest() throws Exception {
		Mockito.when(accountRepository.save(account1)).thenReturn(account1);
		String content = objectMapper.writeValueAsString(account1);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customers/7615855264001/accounts").content(content)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
	
	@Test
	void deleteTest() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("9814762657301")).thenReturn(account2);
		mockMvc.perform(MockMvcRequestBuilders
				.delete("/api/v1/customers/731163625713/accounts/9814762657301")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void activateTest() throws Exception {
		Account updatedAccount = new Account("63aae0328a1acb3d34d679f6", "731163625713", "9814762657301", "salary",
				"SBI21315", 35000, true);
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("9814762657301")).thenReturn(account2);
		Mockito.when(accountRepository.save(updatedAccount)).thenReturn(updatedAccount);
		String content = objectMapper.writeValueAsString(updatedAccount);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.put("/api/v1/customers/731163625713/accounts/9814762657301/activate")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(content);
		mockMvc.perform(mockRequest).andExpect(status().isOk()).andExpect(jsonPath("$.active", is(true)));
	}
	
	@Test
	void deActivateTest() throws Exception {
		Account updatedAccount = new Account("63aae0328a1acb3d34d679f6", "731163625713", "3714762657302", "salary",
				"SBI21315", 35000, false);
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		Mockito.when(accountRepository.save(updatedAccount)).thenReturn(updatedAccount);
		String content = objectMapper.writeValueAsString(updatedAccount);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.put("/api/v1/customers/731163625713/accounts/3714762657302/deactivate")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(content);
		mockMvc.perform(mockRequest).andExpect(status().isOk()).andExpect(jsonPath("$.active", is(false)));
	}

}
