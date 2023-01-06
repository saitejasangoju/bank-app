package com.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.bank.BankApplication;
import com.bank.dto.CustomerUpdateDto;
import com.bank.entity.Address;
import com.bank.entity.Customer;
import com.bank.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = BankApplication.class)
@TestPropertySource(locations = "classpath:application.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerServiceIntegrationTest {
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private CustomerService customerService;
	
	private String cid = "";
	private Customer customer;
	private Address address;
	
	@Test
	@Order(1)
	void testCreateCustomer() throws Exception {
		address = Address.builder().city("hyd").houseNumber("23-8").pincode("989898").state("ts").build();
		customer = Customer.builder().name("sandeep").dob("2000-04-26").aadhar("451235886543").email("sandeep@gmail.com")
				.phone("8883773654").address(address).build();
		Customer newCustomer = customerService.create(customer);
		assertEquals("sandeep", newCustomer.getName());
	}
	
	@Test
	@Order(2)
	void testListCustomer() throws Exception {
		List<Customer> list = customerService.list();
		assertEquals(3, list.size());
	}

	@Test
	@Order(3)
	void testUpdateCustomer() throws Exception {
		address = Address.builder().city("chennai").state("tamilnadu").pincode("987543").houseNumber("98/7").build();
		CustomerUpdateDto updatedCustomer = CustomerUpdateDto.builder().address(address).email("sangoju@gmail.com").phone("9010572614").address(address).build();
		List<Customer> list = customerRepository.findAll();
		Customer c = list.get(list.size() - 1);
		cid = c.getCustomerId();
		Customer updated = customerService.update(cid, updatedCustomer);
		assertEquals("sangoju@gmail.com", updated.getEmail());
	}

	@Test
	@Order(4)
	void testDeleteCustomer() throws Exception {
		List<Customer> list = customerRepository.findAll();
		Customer c = list.get(list.size() - 1);
		cid = c.getCustomerId();
		Customer deleted = customerService.delete(cid);
		assertEquals("sandeep", deleted.getName());
	}
}
