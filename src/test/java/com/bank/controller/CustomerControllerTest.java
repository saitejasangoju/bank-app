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

	Address address = new Address("23/4", "hyd", "telangana", 987789);
	Customer customer1 = new Customer("731163625713", "teja", "2002-10-20", "9283773654", "teja@gmail.com", "987678098076",
			address);
	Customer customer2 = new Customer("3135429456789", "sai", "2002-11-05", "8987773654", "sai@gmail.com", "879678098076",
			address);
	CustomerUpdateDto customerUpdateDto = new CustomerUpdateDto("teja@gmail.com", "9283773654", address);

	@Test
	void listTest() throws Exception {
		List<Customer> list = Arrays.asList(customer1, customer2);
		Mockito.when(customerRepository.findAll()).thenReturn(list);
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/v1/customers")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name", is("teja")));
	}
	
	@Test
	void createTest() throws Exception {
		Mockito.when(customerRepository.save(customer1)).thenReturn(customer1);
		String content = objectMapper.writeValueAsString(customer1);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
	}
	
	@Test
	void deleteTest() throws Exception {
		Mockito.when(customerRepository.findById(customer2.getCustomerId())).thenReturn(java.util.Optional.of(customer2));
		mockMvc.perform(MockMvcRequestBuilders
				.delete("/api/v1/customers/3135429456789")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	void updateTest() throws Exception {
		Customer updatedCustomer = new Customer("731163625713", "teja", "2002-10-20", "8987773654", "sai@gmail.com", "987678098076",address);
		Mockito.when(customerRepository.findById(customer1.getCustomerId())).thenReturn(java.util.Optional.ofNullable(customer1));
		Mockito.when(customerRepository.save(updatedCustomer)).thenReturn(updatedCustomer);
		String content = objectMapper.writeValueAsString(updatedCustomer);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.put("/api/v1/customers/731163625713")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(content);
		mockMvc.perform(mockRequest)
				.andExpect(status().isAccepted())
				.andExpect(jsonPath("$.email", is("sai@gmail.com")));
	}
}
