package com.bank.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.bank.entity.Transaction;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String>{

	List<Transaction> findByAccountNumber(String accountNumber);
	
	List<Transaction> findByCustomerIdAndAccountNumber(String customerId, String accountNumber);
	
	List<Transaction> findByCustomerIdAndAccountNumberAndDateGreaterThanEqual(String customerId, String accountNumber, Instant date);
	
	void deleteByAccountNumber(String accountNumber);
}
