package com.bank.repository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.entity.Customer;

@ConditionalOnProperty(value = "switch.database.to", havingValue = "postgres")
@Repository
public interface CustomerRepositoryPostgres extends JpaRepository<Customer, String>, CustomerRepository{
	
//	Customer findByAadhar(String aadhar);
//	Customer findByCustomerIdOrAadhar (String customerId, String aadhar);
//	Customer findByCustomerIdAndAadharAndName(String customerId, String aadhar, String name);
}