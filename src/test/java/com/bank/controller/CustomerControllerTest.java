package com.bank.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.bank.dto.CustomerDto;
import com.bank.dto.CustomerUpdateDto;
import com.bank.entity.Address;
import com.bank.entity.Customer;
import com.bank.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	private CustomerService customerService;

	Address address = Address.builder().city("hyd").houseNumber("23-8").pincode("989898").state("ts").build();
	CustomerDto customer1Dto = CustomerDto.builder().name("teja").dob("2000-04-26").phone("9283773654")
			.email("teja@gmail.com").aadhar("987678098076").address(address).build();
	Customer customer2 = Customer.builder().customerId("3135429456789").name("saiteja").dob("2002-11-05")
			.phone("8987773654").email("sai@gmail.com").aadhar("879678098076").address(address).build();
	CustomerUpdateDto customerUpdateDto = CustomerUpdateDto.builder().email("teja@gmail.com").phone("9283773654")
			.address(address).build();

	@Test
	void createTest() throws Exception {
		String content = objectMapper.writeValueAsString(customer1Dto);
		Mockito.when(customerService.create(any(Customer.class))).thenReturn(customer2);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customers").content(content)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.name", is("saiteja")));
	}

	@Test
	void getByCustomerIdAndAadharAndNameTest() throws Exception {
		Customer c1 = Customer.builder().customerId("8835429456789").name("teja").dob("2002-11-05").phone("7787773654")
				.email("teja@gmail.com").aadhar("449678098076").address(address).build();
		Mockito.when(customerService.getByCustomerIdAndAadharAndName("8835429456789", "449678098076", "teja")).thenReturn(c1);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/8835429456789/aadhar/449678098076/name/teja").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.name", is("teja")));
	}
	
	@Test
	void getByCustomerIdOrAadhar() throws Exception {
		Customer c1 = Customer.builder().customerId("8835429456789").name("teja").dob("2002-11-05").phone("7787773654")
				.email("teja@gmail.com").aadhar("449678098076").address(address).build();
		Mockito.when(customerService.getByCustomerIdOrAadhar("8835429456789", "749678098076")).thenReturn(c1);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/8835429456789/aadhar/749678098076").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.name", is("teja")));
	}
	
	
	@Test
	void listTest() throws Exception {
		Customer c1 = Customer.builder().customerId("8835429456789").name("teja").dob("2002-11-05").phone("7787773654")
				.email("teja@gmail.com").aadhar("449678098076").address(address).build();
		Customer c2 = Customer.builder().customerId("3135429456789").name("saiteja").dob("2002-11-05")
				.phone("8987773654").email("sai@gmail.com").aadhar("879678098076").address(address).build();
		List<Customer> list = new ArrayList<>();
		list.add(c1);
		list.add(c2);
		Mockito.when(customerService.list()).thenReturn(list);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].name", is("teja")));
	}
	

	@Test
	void updateTest() throws Exception {
		customer2.setPhone(customerUpdateDto.getPhone());
		customer2.setEmail(customerUpdateDto.getEmail());
		customer2.setAddress(customerUpdateDto.getAddress());
		Mockito.when(customerService.update(eq("3135429456789"), any(CustomerUpdateDto.class))).thenReturn(customer2);
		String content = objectMapper.writeValueAsString(customerUpdateDto);
		ResultActions res = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/customers/3135429456789")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isAccepted()).andExpect(jsonPath("$.phone", is("9283773654")));
		System.out.println(res);
	}
	
	@Test
	void deleteTest() throws Exception {
		Mockito.when(customerService.delete("3135429456789")).thenReturn(customer2);
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/customers/3135429456789")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.phone", is("8987773654")));
	}

}
