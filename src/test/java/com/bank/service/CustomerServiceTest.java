package com.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bank.dto.CustomerDto;
import com.bank.dto.CustomerUpdateDto;
import com.bank.entity.Address;
import com.bank.entity.Customer;
import com.bank.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerServiceTest {

	@Autowired
	private CustomerService customerService;
	
	@MockBean
	private CustomerRepository customerRepository;

	Address address = Address.builder().city("hyd").houseNumber("23-8").pincode("989898").state("ts").build();
	CustomerDto customer1Dto = CustomerDto.builder().name("teja").dob("2000-04-26").phone("9283773654").email("teja@gmail.com").aadhar("987678098076")
			.address(address).build();
	Customer customer2 = Customer.builder().name("sai").dob("2002-11-05").phone("8987773654").email("sai@gmail.com").aadhar("879678098076")
			.address(address).build();
	CustomerUpdateDto customerUpdateDto = CustomerUpdateDto.builder().email("teja@gmail.com").phone("9283773654").address(address).build();

	
	@Test
	void testCreateCustomer() throws Exception {
		Mockito.when(customerRepository.findById("1234535672134")).thenReturn(Optional.of(customer2));
		Customer customer = customerService.create(customer2);
		assertEquals("sai@gmail.com", customer.getEmail());
	}

	@Test
	void testUpdateCustomer() throws Exception {
		Mockito.when(customerRepository.findById("1234535672134")).thenReturn(Optional.of(customer2));
		Customer customer = customerService.update("1234535672134", customerUpdateDto);
		assertEquals("teja@gmail.com", customer.getEmail());
	}
	
	@Test
	void testListCustomer() throws Exception {
		Customer customer1 = Customer.builder().name("sai").dob("2002-11-05").phone("8987773654").email("sai@gmail.com").aadhar("879678098076")
				.address(address).build();
		List<Customer> customer = new ArrayList<>();
		customer.add(customer1);
		customer.add(customer2);
		Mockito.when(customerService.list()).thenReturn(customer);
		assertEquals(2, customer.size());
	}
	
	@Test
	void testDeleteCustomer() throws Exception {
		Mockito.when(customerRepository.findById("1234535672134")).thenReturn(Optional.of(customer2));
		Customer customer = customerService.delete("1234535672134");
		assertEquals("sai", customer.getName());
	}
	
	@Test
	void testGetCustomerIdOrAadhar() throws Exception {
		Mockito.when(customerRepository.findByCustomerIdOrAadhar("1234535672134", "879678098076")).thenReturn(customer2);
		Customer customer = customerService.getByCustomerIdOrAadhar("1234535672134", "879678098076");
		assertEquals("879678098076", customer.getAadhar());
	}
	
	@Test
	void testGetCustomerIdAndAadharAndName() throws Exception {
		Mockito.when(customerRepository.findByCustomerIdAndAadharAndName("1234535672134", "879678098076", "sai")).thenReturn(customer2);
		Customer customer = customerService.getByCustomerIdAndAadharAndName("1234535672134", "879678098076", "sai");
		assertEquals("sai", customer.getName());
	}
	

	
	
}	
