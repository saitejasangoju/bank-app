package com.bank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bank.entity.Account;

public interface AccountRepository extends MongoRepository<Account, String>{

	Optional<Account> findByAccountNumber(String accountNumber);
	
	List<Account> findByCustomerId(String customerId);	
}
