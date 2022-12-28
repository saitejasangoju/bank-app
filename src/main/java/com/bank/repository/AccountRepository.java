package com.bank.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bank.entity.Account;

public interface AccountRepository extends MongoRepository<Account, String>{

	Account findByAccountNumber(String accountNumber);
}
