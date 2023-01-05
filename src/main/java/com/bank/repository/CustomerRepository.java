package com.bank.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.bank.entity.Customer;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String>{
	
	Customer findByAadhar(String aadhar);
}
