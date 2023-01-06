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
import com.bank.dto.CustomerDto;
import com.bank.dto.CustomerUpdateDto;
import com.bank.entity.Address;
import com.bank.entity.Customer;
import com.bank.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = BankApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@Slf4j
class CustomerControllerIntegrationTest {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired 
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;
	
	private String cid = "";
	private CustomerDto customer;
	private Address address;

	@Test
	@Order(1)
	void createTest() throws Exception {
		address = Address.builder().city("hyd").houseNumber("23-8").pincode("989898").state("ts").build();
		customer = CustomerDto.builder().name("sandeep").dob("2000-04-26").aadhar("451235886543").email("sandeep@gmail.com")
				.phone("8883773654").address(address).build();
		String content = objectMapper.writeValueAsString(customer);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customers").accept(MediaType.APPLICATION_JSON)
				.content(content).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
				.andExpect(jsonPath("$.name", is("sandeep")));
	}

	@Test
	@Order(2)
	void updateTest() throws Exception {
		address = Address.builder().city("chennai").state("tamilnadu").pincode("987543").houseNumber("98/7").build();
		CustomerUpdateDto updatedCustomer = CustomerUpdateDto.builder().address(address).email("sangoju@gmail.com").phone("9010572614").address(address).build();
		String content = objectMapper.writeValueAsString(updatedCustomer);
		List<Customer> list = customerRepository.findAll();
		Customer customer = list.get(list.size() - 1);
		cid = customer.getCustomerId();
		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/customers/"+cid).accept(MediaType.APPLICATION_JSON)
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
