package com.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.bank.entity.Customer;

//@Repository
//public interface CustomerRepository extends MongoRepository<Customer, String>{
//	
//	Customer findByAadhar(String aadhar);
//	Customer findByCustomerIdOrAadhar (String customerId, String aadhar);
//	Customer findByCustomerIdAndAadharAndName(String customerId, String aadhar, String name);
//}

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String>{
	
	Customer findByAadhar(String aadhar);
	Customer findByCustomerIdOrAadhar (String customerId, String aadhar);
	Customer findByCustomerIdAndAadharAndName(String customerId, String aadhar, String name);
}
