package com.bank.controller;

import static org.hamcrest.CoreMatchers.is;
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

import com.bank.dto.CustomerUpdateDto;
import com.bank.entity.Address;
import com.bank.entity.Customer;
import com.bank.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest { 

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	private CustomerRepository customerRepository;

	Address address = new Address("23/4", "hyd", "telangana", "987789");
	Customer customer1 = new Customer("731163625713", "teja", "2002-10-20", "9283773654", "teja@gmail.com",
			"987678098076", address);
	Customer customer2 = new Customer("3135429456789", "sai", "2002-11-05", "8987773654", "sai@gmail.com",
			"879678098076", address);
	CustomerUpdateDto customerUpdateDto = new CustomerUpdateDto("teja@gmail.com", "9283773654", address);

	@Test
	void listTest() throws Exception {
		List<Customer> list = Arrays.asList(customer1, customer2);
		Mockito.when(customerRepository.findAll()).thenReturn(list);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].name", is("teja")));
	}

	@Test
	void createTest() throws Exception {
		Mockito.when(customerRepository.save(customer1)).thenReturn(customer1);
		String content = objectMapper.writeValueAsString(customer1);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customers").content(content)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
	}

	@Test
	void deleteTest() throws Exception {
		Mockito.when(customerRepository.findById(customer2.getCustomerId()))
				.thenReturn(java.util.Optional.of(customer2));
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/customers/3135429456789")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	void updateTest() throws Exception {
		Customer updatedCustomer = new Customer("731163625713", "teja", "2002-10-20", "8987773654", "sai@gmail.com",
				"987678098076", address);
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(java.util.Optional.ofNullable(customer1));
		String content = objectMapper.writeValueAsString(updatedCustomer);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/v1/customers/731163625713")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(content);
		mockMvc.perform(mockRequest).andExpect(status().isAccepted())
				.andExpect(jsonPath("$.email", is("sai@gmail.com")));
	}
	
	// negative test cases
	
	Customer aadharAlreadyExist = new Customer("731163625713", "teja", "2002-10-20", "9283773654", "teja@gmail.com",
			"987678098076", address);
	
	@Test
	void aadharAlreadyExist() throws Exception {
		Mockito.when(customerRepository.findByAadhar("987678098076")).thenReturn(aadharAlreadyExist);
		String content = objectMapper.writeValueAsString(aadharAlreadyExist);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers").content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
	
	Customer invalidAadharNumber = new Customer("897163625713", "teja", "2002-10-20", "9283773654", "teja@gmail.com",
			"099343758322", address);
	@Test
	void invalidAadharNumber() throws Exception {
		Mockito.when(customerRepository.findByAadhar("099343758322")).thenReturn(invalidAadharNumber);
		String content = objectMapper.writeValueAsString(invalidAadharNumber);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers").content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
	
	Customer invalidName = new Customer("297163625713", "te", "2002-10-20", "9283773654", "teja@gmail.com",
			"399343758322", address);
	@Test
	void invalidName() throws Exception {
		Mockito.when(customerRepository.findByAadhar("399343758322")).thenReturn(invalidName);
		String content = objectMapper.writeValueAsString(invalidName);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers").content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(406));
	}
	
	Address address1 = new Address("34-2", "hyd", "ts", "32497");
	Customer invalidPinCode = new Customer("907163625713", "teja", "2002-10-20", "9283773654", "teja@gmail.com",
			"546343758322", address1);
	@Test
	void invalidPinCode() throws Exception {
		Mockito.when(customerRepository.findByAadhar("546343758322")).thenReturn(invalidPinCode);
		String content = objectMapper.writeValueAsString(invalidPinCode);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers").content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(406));
	}
	
	Customer invalidPhoneNumber = new Customer("437163625713", "teja", "2002-10-20", "983773654", "teja@gmail.com",
			"776343758322", address);
	@Test
	void invalidPhoneNumber() throws Exception {
		Mockito.when(customerRepository.findByAadhar("776343758322")).thenReturn(invalidPhoneNumber);
		String content = objectMapper.writeValueAsString(invalidPhoneNumber);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers").content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(406));
	}
	
	Customer invalidEmail = new Customer("437163625713", "teja", "2002-10-20", "9983773654", "tejamail.com", 
			"776343758322", address);
	@Test
	void invalidEmail() throws Exception {
		Mockito.when(customerRepository.findByAadhar("776343758322")).thenReturn(invalidEmail);
		String content = objectMapper.writeValueAsString(invalidEmail);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers").content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(406));
	}
}













