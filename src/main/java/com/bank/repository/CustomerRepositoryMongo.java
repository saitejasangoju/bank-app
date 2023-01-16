package com.bank.repository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.bank.entity.Customer;


@ConditionalOnProperty(value = "switch.database.to", havingValue = "mongo")
@Repository
public interface CustomerRepositoryMongo extends MongoRepository<Customer, String>, CustomerRepository{
	
//	Customer findByAadhar(String aadhar);
//	Customer findByCustomerIdOrAadhar (String customerId, String aadhar);
//	Customer findByCustomerIdAndAadharAndName(String customerId, String aadhar, String name);
}

