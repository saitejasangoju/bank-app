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
import com.bank.entity.AccountType;
import com.bank.entity.Address;
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
	
	Address address = Address.builder().city("hyd").houseNumber("56/7").pincode("456543").state("ts").build();
	Customer customer1 = Customer.builder().customerId("731163625713").name("teja").dob("2002-10-20").phone("9283773654").email("teja@gmail.com").aadhar("987678098076").address(address).build();
	Account account1 = Account.builder().aid("63aae0328a1acb3d34d568f5").customerId("731163625713").accountNumber("3714762657302").type(AccountType.SAVING).ifscCode("SBI21315").accountBalance(35000.0).active(true).build();
	Account account2 = Account.builder().aid("63aae0328a1acb3d34d679f6").customerId("731163625713").accountNumber("9814762657301").type(AccountType.SALARY).ifscCode("SBI21315").accountBalance(35000.0).active(false).build();
	
	@Test
	void listTest() throws Exception {
		List<Account> list = Arrays.asList(account1, account2);
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findAll()).thenReturn(list);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/731163625713/accounts")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].type", is("SAVING")));
	}
	
	@Test
	void getByAccountNumberTest() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/v1/customers/731163625713/accounts/3714762657302")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accountNumber", is("3714762657302")));
	}

	@Test
	void activateTest() throws Exception {
		Account updatedAccount = Account.builder().aid("63aae0328a1acb3d34d679f6").customerId("731163625713").accountNumber("9814762657301").type(AccountType.SALARY).ifscCode("SBI21315").accountBalance(35000.0).active(true).build();
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
	void createTest() throws Exception {
		Mockito.when(accountRepository.save(account1)).thenReturn(account1);
		String content = objectMapper.writeValueAsString(account1);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customers/7615855264001/accounts").content(content)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
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
	void deActivateTest() throws Exception {
		Account updatedAccount = Account.builder().aid("63aae0328a1acb3d34d679f6").customerId("731163625713").accountNumber("3714762657302").type(AccountType.SALARY).ifscCode("SBI21315").accountBalance(35000.0).active(false).build();
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		Mockito.when(accountRepository.save(updatedAccount)).thenReturn(updatedAccount);
		String content = objectMapper.writeValueAsString(updatedAccount);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.put("/api/v1/customers/731163625713/accounts/3714762657302/deactivate")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(content);
		mockMvc.perform(mockRequest).andExpect(status().isOk()).andExpect(jsonPath("$.active", is(false)));
	}
	
	//customer id and account number invalid
	@Test
	void InvalidCustomerIdDeActivateTest() throws Exception {
		Customer customer = Customer.builder().customerId("888163625713").name("teja").dob("2002-10-20").phone("9283773654").email("teja@gmail.com").aadhar("987678098076").address(address).build();
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(customerRepository.findById("888163625713")).thenReturn(Optional.of(customer));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.put("/api/v1/customers/888163625713/accounts/3714762657302/deactivate")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(mockRequest).andExpect(status().isBadRequest());
	}
	
	
	//customer id and account number invalid
	@Test
	void InvalidCustomerIdActivateTest() throws Exception {
		Customer customer = Customer.builder().customerId("888163625713").name("teja").dob("2002-10-20").phone("9283773654").email("teja@gmail.com").aadhar("987678098076").address(address).build();
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(customerRepository.findById("888163625713")).thenReturn(Optional.of(customer));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.put("/api/v1/customers/888163625713/accounts/3714762657302/activate")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(mockRequest).andExpect(status().isBadRequest());
	}
	

	// account is null
	@Test
	void nullAccountActivateTest() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(null);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.put("/api/v1/customers/731163625713/accounts/3714762657302/activate")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(mockRequest).andExpect(status().isBadRequest());
	}
	
	// account is null
	@Test
	void nullAccountDeActivateTest() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(null);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.put("/api/v1/customers/731163625713/accounts/3714762657302/deactivate")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(mockRequest).andExpect(status().isBadRequest());
	}
	
	
	
	// negative test cases
	
	
	// listing not active accounts
	@Test
	void notActiveAccount() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Account account = Account.builder().aid("63aae0328a1acb3d34d679f6").customerId("731163625713").accountNumber("3714762657302").type(AccountType.SALARY).ifscCode("SBI21315").accountBalance(35000.0).active(false).build();
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account);
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/v1/customers/731163625713/accounts/3714762657302").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
	
	//activating account which is already active
	@Test
	void activateAccount() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Account account = Account.builder().aid("63aae0328a1acb3d34d679f6").customerId("731163625713").accountNumber("3714762657302").type(AccountType.SALARY).ifscCode("SBI21315").accountBalance(35000.0).active(true).build();
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account);
		mockMvc.perform(MockMvcRequestBuilders
				.put("/api/v1/customers/731163625713/accounts/3714762657302/activate").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
	
	// de-activating account which is already not active
	@Test
	void deActivateAccount() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Account account = Account.builder().aid("63aae0328a1acb3d34d679f6").customerId("731163625713").accountNumber("3714762657302").type(AccountType.SALARY).ifscCode("SBI21315").accountBalance(35000.0).active(false).build();
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account);
		mockMvc.perform(MockMvcRequestBuilders
				.put("/api/v1/customers/731163625713/accounts/3714762657302/deactivate").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
	
	// invalid account number
	
	// customer id dont match account number 
	@Test
	void deleteAccount() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Account account = Account.builder().aid("63aae0328a1acb3d34d679f6").customerId("931163625713").accountNumber("3714762657302").type(AccountType.SALARY).ifscCode("SBI21315").accountBalance(35000.0).active(true).build();
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account);
		mockMvc.perform(MockMvcRequestBuilders
				.delete("/api/v1/customers/731163625713/accounts/3714762657302").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
	
	// wrong customer id
	@Test 
	void listAccounts() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/v1/customers/831163625713/accounts").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
	
	// wrong customer id
	@Test
	void createAccount() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/831163625713/accounts").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
	
	
}
