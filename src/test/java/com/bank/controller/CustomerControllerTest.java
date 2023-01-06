package com.bank.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.bank.dto.CustomerDto;
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

	Address address = Address.builder().city("hyd").houseNumber("23-8").pincode("989898").state("ts").build();
	CustomerDto customer1Dto = CustomerDto.builder().name("teja").dob("2000-04-26").phone("9283773654").email("teja@gmail.com").aadhar("987678098076")
			.address(address).build();
	Customer customer2 = Customer.builder().name("sai").dob("2002-11-05").phone("8987773654").email("sai@gmail.com").aadhar("879678098076")
			.address(address).build();
	CustomerUpdateDto customerUpdateDto = CustomerUpdateDto.builder().email("teja@gmail.com").phone("9283773654").address(address).build();

	

	@Test
	void createTest() throws Exception {
		String content = objectMapper.writeValueAsString(customer1Dto);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customers").content(content)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.name", is("teja")));
	}

	@Test
	void deleteTest() throws Exception {
		Mockito.when(customerRepository.findById("3135429456789"))
				.thenReturn(Optional.of(customer2));
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/customers/3135429456789")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	void updateTest() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(java.util.Optional.ofNullable(customer2));
		String content = objectMapper.writeValueAsString(customerUpdateDto);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/v1/customers/731163625713")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(content);
		mockMvc.perform(mockRequest).andExpect(status().isAccepted())
				.andExpect(jsonPath("$.email", is("teja@gmail.com")));
	}
	
	@Test
	void listTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	// negative test cases
	
	@Test
	void aadharAlreadyExist() throws Exception {
		Customer aadharAlreadyExist = Customer.builder().name("teja").dob("2002-11-05").phone("9283773654").email("teja@gmail.com").aadhar("987678098076")
				.address(address).build();
		Mockito.when(customerRepository.findByAadhar("987678098076")).thenReturn(aadharAlreadyExist);
		String content = objectMapper.writeValueAsString(aadharAlreadyExist);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers").content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
	
	
	@Test
	void invalidAadharNumber() throws Exception {
		Customer invalidAadharNumber = Customer.builder().name("teja").dob("2002-11-05").phone("9283773654").email("teja@gmail.com").aadhar("099343758322")
				.address(address).build();
		Mockito.when(customerRepository.findByAadhar("099343758322")).thenReturn(invalidAadharNumber);
		String content = objectMapper.writeValueAsString(invalidAadharNumber);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers").content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
	
	
	@Test
	void invalidName() throws Exception {
		Customer invalidName = Customer.builder().name("te").dob("2002-11-05").phone("9283773654").email("teja@gmail.com").aadhar("899343758322")
				.address(address).build();
		Mockito.when(customerRepository.findByAadhar("399343758322")).thenReturn(invalidName);
		String content = objectMapper.writeValueAsString(invalidName);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers").content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(406));
	}
	

	@Test
	void invalidPinCode() throws Exception {
		Address address1 = Address.builder().houseNumber("34-2").city("hyd").state("ts").pincode("32497").build();
		Customer invalidPinCode = Customer.builder().name("teja").dob("2002-11-05").phone("9283773654").email("teja@gmail.com").aadhar("899343758322")
				.address(address1).build();
		Mockito.when(customerRepository.findByAadhar("546343758322")).thenReturn(invalidPinCode);
		String content = objectMapper.writeValueAsString(invalidPinCode);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers").content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(406));
	}
	

	@Test
	void invalidPhoneNumber() throws Exception {
		Customer invalidPhoneNumber = Customer.builder().name("teja").dob("2002-11-05").phone("283773654").email("teja@gmail.com").aadhar("899343758322")
				.address(address).build();
		Mockito.when(customerRepository.findByAadhar("776343758322")).thenReturn(invalidPhoneNumber);
		String content = objectMapper.writeValueAsString(invalidPhoneNumber);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers").content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(406));
	}
	
	@Test
	void invalidEmail() throws Exception {
		Customer invalidEmail = Customer.builder().name("teja").dob("2002-11-05").phone("283773654").email("tejagmail.com").aadhar("899343758322")
				.address(address).build();
		Mockito.when(customerRepository.findByAadhar("776343758322")).thenReturn(invalidEmail);
		String content = objectMapper.writeValueAsString(invalidEmail);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers").content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(406));
	}
	
	@Test
	void testInvalidPinCode() throws Exception {
		Address address1 = Address.builder().city("hyd").houseNumber("23-8").pincode("98998").state("ts").build();
		Customer checkingPinCode = Customer.builder().name("teja").dob("2002-11-05").phone("2837738654").email("teja@gmail.com").aadhar("449343758322")
				.address(address1).build();
		Mockito.when(customerRepository.findByAadhar("776343758322")).thenReturn(checkingPinCode);
		String content = objectMapper.writeValueAsString(checkingPinCode);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers").content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(406));
	}
	
	
}













