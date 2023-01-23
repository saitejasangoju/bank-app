package com.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.bank.BankApplication;
import com.bank.dto.CustomerUpdateDto;
import com.bank.entity.Address;
import com.bank.entity.Customer;
import com.bank.exception.AgeNotSatisfiedException;
import com.bank.repository.mongo.CustomerRepositoryMongo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = BankApplication.class)
@TestPropertySource(locations = "classpath:application.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerServiceIntegrationTest {
	
	@Autowired
	private CustomerRepositoryMongo customerRepository;
	
	@Autowired
	private CustomerService customerService;
	
	private static String cid = "";
	
	private Address address = Address.builder().city("hyd").houseNumber("23-8").pincode("989898").state("ts").build();
	private Customer customer = Customer.builder().name("sandeep").dob("2000-04-26").aadhar("451235886543").email("sandeep@gmail.com")
			.phone("8883773654").address(address).build();
	
	@Test
	@Order(1)
	void testCreateCustomer() throws Exception {
		Customer newCustomer = customerService.create(customer);
		assertEquals("sandeep", newCustomer.getName());
	}
	
	@Test
	@Order(2)
	void testWithExistingAadhar() throws Exception {
		Customer existingAadhar =  Customer.builder().name("sandeep").dob("2000-04-26").aadhar("451235886543").email("sandeep@gmail.com")
				.phone("8883773654").address(address).build();
		assertThrows(IllegalArgumentException.class, () -> customerService.create(existingAadhar));
	}
	
	@Test
	@Order(3)
	void testAadharStartsWith0() throws Exception {
		Customer existingAadhar =  Customer.builder().name("sandeep").dob("2000-04-26").aadhar("051235886543").email("sandeep@gmail.com")
				.phone("8883773654").address(address).build();
		assertThrows(IllegalArgumentException.class, () -> customerService.create(existingAadhar));
	}
	
	@Test
	@Order(4)
	void invalidCustomerAge() throws Exception {
		Customer invalidCustomerAge =  Customer.builder().name("sandeep").dob("2009-04-26").aadhar("451235886543").email("sandeep@gmail.com")
				.phone("8883773654").address(address).build();
		assertThrows(AgeNotSatisfiedException.class, () -> customerService.create(invalidCustomerAge));
	}
	
	
	@Test
	@Order(5)
	void testListCustomer() throws Exception {
		List<Customer> list = customerService.list();
		assertEquals(4, list.size());
	}

	@Test
	@Order(6)
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
	@Order(7)
	void invalidCustomerIdForUpdate() throws Exception {
		CustomerUpdateDto updatedCustomer = CustomerUpdateDto.builder().address(address).email("sangoju@gmail.com").phone("9010572614").address(null).build();
		assertThrows(NoSuchElementException.class, () -> customerService.update("43633738766532", updatedCustomer));
	}
	
	@Test
	@Order(8)
	void invalidCustomerIdToDelete() throws Exception {
		assertThrows(NoSuchElementException.class, () -> customerService.delete("43633738766532"));
	}
	
	@Test
	@Order(9)
	void invalidCustomerIdAndAadharAndName() throws Exception {
		assertThrows(NoSuchElementException.class, () -> customerService.getByCustomerIdAndAadharAndName("43633738766532", "8374738672367", "sai"));
	}
	
	@Test
	@Order(10)
	void invalidCustomerIdOrAadhar() throws Exception {
		assertThrows(NoSuchElementException.class, () -> customerService.getByCustomerIdOrAadhar("43633738766532", "8374738672367"));
	}
	
	@Test
	@Order(11)
	void testGetById() throws Exception {
		Customer customer = customerService.getById(cid);
		assertEquals("sandeep", customer.getName());
	}
	
	@Test
	@Order(12)
	void testDeleteCustomer() throws Exception {
		List<Customer> list = customerRepository.findAll();
		Customer c = list.get(list.size() - 1);
		cid = c.getCustomerId();
		Customer deleted = customerService.delete(cid);
		assertEquals("sandeep", deleted.getName());
	}
}
