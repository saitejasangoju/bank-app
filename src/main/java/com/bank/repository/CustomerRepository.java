package com.bank.repository;

import java.util.List;
import java.util.Optional;

import com.bank.entity.Customer;

public interface CustomerRepository{
	
	Customer save(Customer customer);
	Optional<Customer> findById(String id);
	List<Customer> findAll();
	void delete(Customer customer);

	Customer findByAadhar(String aadhar);
	Customer findByCustomerIdOrAadhar (String customerId, String aadhar);
	Customer findByCustomerIdAndAadharAndName(String customerId, String aadhar, String name);
	
}
