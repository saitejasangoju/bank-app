package com.bank.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.List;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.bank.BankApplication;
import com.bank.dto.CustomerUpdateDto;
import com.bank.entity.Address;
import com.bank.entity.Customer;
import com.bank.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = BankApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class CustomerControllerIntegrationTest {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	Address address = new Address("23/4", "hyd", "telangana", "987789");
	Customer customer = new Customer("731", "sandeep", "2000-04-26", "8883773654", "sandeep@gmail.com", "4512358786543",
			address);

	private String cid = "";

	@Test
	@Order(1)
	void createTest() throws Exception {
		String content = objectMapper.writeValueAsString(customer);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customers").accept(MediaType.APPLICATION_JSON)
				.content(content).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
				.andExpect(jsonPath("$.name", is("sandeep")));
	}

	@Test
	@Order(2)
	void updateTest() throws Exception {
		Address address = new Address("45/4", "hyd", "telangana", "987789");
		CustomerUpdateDto updatedCustomer = new CustomerUpdateDto("sangoju@gmail.com", "9010572614", address);
		String content = objectMapper.writeValueAsString(updatedCustomer);
		List<Customer> list = customerRepository.findAll();
		Customer customer = list.get(list.size() - 1);
		cid = customer.getCustomerId();
		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/customers/" + cid).accept(MediaType.APPLICATION_JSON)
				.content(content).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isAccepted())
				.andExpect(jsonPath("$.email", is("sangoju@gmail.com")));
	}

	@Test
	@Order(3)
	void listTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers").accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$[2].phone", is("9010572614")));

	}

	@Test
	@Order(4)
	void deleteTest() throws Exception {
		List<Customer> list = customerRepository.findAll();
		Customer customer = list.get(list.size() - 1);
		cid = customer.getCustomerId();
		mockMvc.perform(
				MockMvcRequestBuilders.delete("/api/v1/customers/" + cid).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

}
