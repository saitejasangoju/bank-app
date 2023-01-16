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
import com.bank.repository.CustomerRepositoryMongo;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = BankApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class CustomerControllerIntegrationTest {

	@Autowired
	private CustomerRepositoryMongo customerRepository;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private static String cid = "";
	private Customer customer;
	private Address address;

	@Test
	@Order(1)
	void testCreateCustomer() throws Exception {
		address = Address.builder().city("hyd").houseNumber("23-8").pincode("989898").state("ts").build();
		customer = Customer.builder().name("sandeep").dob("2000-04-26").aadhar("451235886543")
				.email("sandeep@gmail.com").phone("8883773654").address(address).build();
		String content = objectMapper.writeValueAsString(customer);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customers").accept(MediaType.APPLICATION_JSON)
				.content(content).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
				.andExpect(jsonPath("$.name", is("sandeep")));
	}

	@Test
	@Order(2)
	void testUpdateCustomer() throws Exception {
		address = Address.builder().city("chennai").state("tamilnadu").pincode("987543").houseNumber("98/7").build();
		CustomerUpdateDto updatedCustomer = CustomerUpdateDto.builder().address(address).email("sangoju@gmail.com")
				.phone("9010572614").address(address).build();
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
	void testListCustomers() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers").accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$[2].phone", is("9010572614")));
	}

	@Test
	@Order(4)
	void testWithExistingAadhar() throws Exception {
		Customer aadharAlreadyExist = Customer.builder().name("teja").dob("2002-11-05").phone("9283773654")
				.email("teja@gmail.com").aadhar("451235886543").address(address).build();
		String content = objectMapper.writeValueAsString(aadharAlreadyExist);
		try {
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customers").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Order(5)
	void testWIthInvalidAadharNumber() throws Exception {
		address = Address.builder().city("hyd").houseNumber("23-8").pincode("989898").state("ts").build();
		customer = Customer.builder().name("sandeep").dob("2000-04-26").aadhar("051235886543")
				.email("sandeep@gmail.com").phone("8883773654").address(address).build();
		String content = objectMapper.writeValueAsString(customer);
		try {
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customers").accept(MediaType.APPLICATION_JSON)
				.content(content).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Order(6)
	void testWithInvalidName() throws Exception {
		Customer invalidName = Customer.builder().name("te").dob("2002-11-05").phone("9283773654").email("teja@gmail.com").aadhar("899343758322")
				.address(address).build();
		String content = objectMapper.writeValueAsString(invalidName);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers").content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	@Order(7)
	void testWithInvalidPinCode() throws Exception {
		Address address1 = Address.builder().houseNumber("34-2").city("hyd").state("ts").pincode("32497").build();
		Customer invalidPinCode = Customer.builder().name("teja").dob("2002-11-05").phone("9283773654").email("teja@gmail.com").aadhar("899343758322")
				.address(address1).build();
		String content = objectMapper.writeValueAsString(invalidPinCode);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers").content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(8)
	void testWithInvalidPhoneNumber() throws Exception {
		Customer invalidPhoneNumber = Customer.builder().name("teja").dob("2002-11-05").phone("283773654").email("teja@gmail.com").aadhar("899343758322")
				.address(address).build();
		String content = objectMapper.writeValueAsString(invalidPhoneNumber);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers").content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	@Order(9)
	void testWithInvalidEmail() throws Exception {
		Customer invalidEmail = Customer.builder().name("teja").dob("2002-11-05").phone("283773654").email("tejagmail.com").aadhar("899343758322")
				.address(address).build();
		String content = objectMapper.writeValueAsString(invalidEmail);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers").content(content).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	@Order(10)
	void testDeleteCustomer() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.delete("/api/v1/customers/" + cid).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

}
