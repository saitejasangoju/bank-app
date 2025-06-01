package com.bank.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.dto.CustomerUpdateDto;
import com.bank.entity.Customer;
import com.bank.exception.AgeNotSatisfiedException;
import com.bank.exception.MethodArgumentNotValidException;
import com.bank.repository.CustomerRepository;
import com.bank.util.BankUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomerService {

	@Autowired
	private BankUtil util;

	@Autowired
	private CustomerRepository customerRepo;

	public List<Customer> list() {
		log.debug("Get all customers");
		return customerRepo.findAll();
	}

	public Customer create(Customer customer) throws Exception, MethodArgumentNotValidException {
		LocalDate dateOfBirth = LocalDate.parse(customer.getDob());
		LocalDate currDate = LocalDate.now();
		Period period = Period.between(currDate, dateOfBirth);
		int age = Math.abs(period.getYears());
		if (age < 18) {
			throw new AgeNotSatisfiedException("Sorry, You don't have enough age to open account. ");
		}
		Customer existingCustomer = customerRepo.findByAadhar(customer.getAadhar());
		if (existingCustomer != null)
			throw new Exception("Customer already exists.");
		customer.setCustomerId(util.generateAccountNumber());
		customerRepo.save(customer);
		log.info("New customer added successfully with AADHAR NUMBER {}", customer.getAadhar());
		return customer;
	}

	public void delete(String customerId) throws Exception {
		log.debug("Delete customer with id {}", customerId);
		Customer customer = util.validateCustomer(customerId);
		customerRepo.delete(customer);
	}

	public Customer update(String customerId, CustomerUpdateDto customerDto) throws Exception {
		log.info("Updating customer with id {} ", customerId);
		Customer existingCustomer = util.validateCustomer(customerId);
		existingCustomer.setEmail(customerDto.getEmail());
		existingCustomer.setPhone(customerDto.getPhone());
		existingCustomer.setAddress(customerDto.getAddress());
		customerRepo.save(existingCustomer);
		return existingCustomer;
	}

}
