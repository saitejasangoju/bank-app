package com.bank.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.dto.CustomerUpdateDto;
import com.bank.entity.Customer;
import com.bank.exception.AgeNotSatisfiedException;
import com.bank.repository.CustomerRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomerService {
	
	@Autowired
	private Utility util;
	
	@Autowired
	private CustomerRepository customerRepo;
	
	// get customer by id
	public Customer getById(String customerId) {
		return customerRepo.findById(customerId).orElseThrow(() -> new IllegalArgumentException("Customer doesn't exist"));
	}

	// listing all customers
	public List<Customer> list() {
		log.info("fetched all the details ");
		return customerRepo.findAll();
	}

	// create customer
	public Customer create(Customer customer) throws Exception {
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
		if(existingCustomer != null)
			throw new IllegalArgumentException("Aadhar already exist");
		if(customer.getAadhar().startsWith("0"))
			throw new IllegalArgumentException("Invalid Aadhar number");
		customer.setCustomerId(util.generateId());
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
	
	public Customer getByCustomerIdAndAadharAndName(String customerId, String aadhar, String name) throws NoSuchElementException {
		Customer customer = customerRepo.findByCustomerIdAndAadharAndName(customerId, aadhar, name);
		if(customer == null) {
			throw new NoSuchElementException("Customer Doesn't exist");
		}
		return customer;
	}
	
	public Customer getByCustomerIdOrAadhar(String customerId, String aadhar) throws NoSuchElementException {
		Customer customer = customerRepo.findByCustomerIdOrAadhar(customerId, aadhar);
		if(customer == null) {
			throw new NoSuchElementException("Customer Doesn't exist");
		}
		return customer;
	}
	

}
