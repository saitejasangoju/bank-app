package com.bank.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.bank.dto.AccountDto;
import com.bank.entity.Account;
import com.bank.entity.AccountType;
import com.bank.entity.Address;
import com.bank.entity.Customer;
import com.bank.service.AccountService;
import com.bank.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private AccountService accountService;

	Address address = Address.builder().city("hyd").houseNumber("56/7").pincode("456543").state("ts").build();
	Customer customer1 = Customer.builder().customerId("731163625713").name("teja").dob("2002-10-20")
			.phone("9283773654").email("teja@gmail.com").aadhar("987678098076").address(address).build();
	Account account1 = Account.builder().customerId("731163625713")
			.accountNumber("3714762657302").type(AccountType.SALARY).ifscCode("SBI21").accountBalance(35000.0)
			.active(true).build();
	Account account2 = Account.builder().customerId("731163625713")
			.accountNumber("9814762657301").type(AccountType.SALARY).ifscCode("SBI21").accountBalance(35000.0)
			.active(false).build();

	@Test
	void testCreateAccount() throws Exception {
		AccountDto accountDto = AccountDto.builder().customerId("731163625713").accountBalance(35000.0)
				.ifscCode("SBI21").type(AccountType.SALARY).build();
		Mockito.when(accountService.create(any(AccountDto.class))).thenReturn(account1);
		String content = objectMapper.writeValueAsString(accountDto);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customers/731163625713/accounts").content(content)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.accountBalance", is(35000.0)));
	}

	@Test
	void testListAccounts() throws Exception {
		List<Account> list = Arrays.asList(account1, account2);
		Mockito.when(accountService.list("731163625713")).thenReturn(list);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/731163625713/accounts")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].type", is("SALARY")));
	}

	@Test
	void testGetByAccountNumber() throws Exception {
		Mockito.when(accountService.getByAccountNumber("731163625713", "3714762657302")).thenReturn(account1);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/731163625713/accounts/3714762657302")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.accountNumber", is("3714762657302")));
	}

	@Test
	void testActivateAccount() throws Exception {
		account2.setActive(true);
		Mockito.when(accountService.activate("731163625713", "9814762657301")).thenReturn(account2);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.put("/api/v1/customers/731163625713/accounts/9814762657301/activate")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(mockRequest).andExpect(status().isOk()).andExpect(jsonPath("$.active", is(true)));
	}

	@Test
	void testDeActivateAccount() throws Exception {
		account1.setActive(false);
		Mockito.when(accountService.deactivate("731163625713", "3714762657302")).thenReturn(account1);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.put("/api/v1/customers/731163625713/accounts/3714762657302/deactivate")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(mockRequest).andExpect(status().isOk()).andExpect(jsonPath("$.active", is(false)));
	}

	@Test
	void testDeleteAccount() throws Exception {
		Mockito.when(accountService.delete("731163625713", "9814762657301")).thenReturn(account2);
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/customers/731163625713/accounts/9814762657301")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.accountNumber", is("9814762657301")));
	}	
}
