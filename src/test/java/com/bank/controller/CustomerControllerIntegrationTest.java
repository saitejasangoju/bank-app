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
import com.bank.entity.Address;
import com.bank.entity.Customer;
import com.bank.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
				classes = BankApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
@Slf4j
public class CustomerControllerIntegrationTest {
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	Address address = new Address("23/4", "hyd", "telangana", "987789");
	Customer customer = new Customer("731163625713", "teja", "2002-10-20", "9283773654", "teja@gmail.com", "987678098543",
			address);
	@Test
	void createTest() throws Exception {
		String content = objectMapper.writeValueAsString(customer);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers")
				.accept(MediaType.APPLICATION_JSON)
				.content(content)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name", is("teja")));
	}
	
	@Test
	void listTest() throws Exception {
		List<Customer> list = customerRepository.findAll();
		log.info("customers list {} ", list);
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/v1/customers")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[12].name", is("teja")));
	}
	
	@Test
	void updateTest() throws Exception {
		Customer updatedCustomer = new Customer("057721535405", "teja", "2002-10-20", "8987773654", "sangoju@gmail.com",
				"987678098543", address);
		customer.setEmail(updatedCustomer.getEmail());
		customerRepository.save(customer);
		String content = objectMapper.writeValueAsString(updatedCustomer);
		mockMvc.perform(MockMvcRequestBuilders
				.put("/api/v1/customers/057721535405")
				.accept(MediaType.APPLICATION_JSON)
				.content(content)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isAccepted())
				.andExpect(jsonPath("$[12].email", is("sangoju@gmail.com")));
	}
	
	@Test
	void deleteTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.delete("/api/v1/customers/481665052808")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

}
