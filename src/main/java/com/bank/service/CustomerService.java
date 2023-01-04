package com.bank.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.dto.CustomerUpdateDto;
import com.bank.entity.Customer;
import com.bank.exception.AgeNotSatisfiedException;
import com.bank.exception.MethodArgumentNotValidException;
import com.bank.repository.CustomerRepository;
import com.bank.util.Utility;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomerService {
	
	@Autowired
	private Utility util;
	@Autowired
	private CustomerRepository customerRepo;

	// getting current date and time
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	LocalDateTime now = LocalDateTime.now();

	// listing all customers
	public List<Customer> list() {
		log.info("fetched all the details ");
		return customerRepo.findAll();
	}

	// create customer
	public Customer create(Customer customer) throws Exception, MethodArgumentNotValidException {
		// checking the age criteria using date of birth
		LocalDate dateOfBirth = LocalDate.parse(customer.getDob());
		LocalDate currDate = LocalDate.now();
		Period period = Period.between(currDate, dateOfBirth);
		int age = Math.abs(period.getYears());
		if (age < 18) {
			throw new AgeNotSatisfiedException("Sorry, You don't have enough age to open account. ");
		}
		Customer existingCustomer = customerRepo.findByAadhar(customer.getAadhar());
		// checking for valid aadhar number
		if(customer.getAadhar().startsWith("0"))
			throw new NoSuchElementException("Invalid Aadhar Number");
		// checking for existing customer using aadhar
		if (existingCustomer != null) 
			throw new IllegalArgumentException("Aadhar Number is already exist.");
		customer.setCustomerId(util.generateAccountNumber());
		customerRepo.save(customer);
		log.info("New customer is added successfully");
		return customer;
	}

	// delete customer permanently
	public Customer delete(String customerId) {
		Customer customer = customerRepo.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException("Customer doesn't exist with id " + customerId));
		customerRepo.delete(customer);
		log.info("deleted the customer of id : " + customerId);
		return customer;
	}

	// update all the editable details
	public Customer update(String customerId, CustomerUpdateDto customer) {
		Customer existingCustomer = customerRepo.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException("Customer doesn't exist with id " + customerId));
		existingCustomer.setEmail(customer.getEmail());
		existingCustomer.setPhone(customer.getPhone());
		existingCustomer.setAddress(customer.getAddress());
		log.info("details are modified of customer id : " + customerId);
		customerRepo.save(existingCustomer);
		return existingCustomer;
	}

}
