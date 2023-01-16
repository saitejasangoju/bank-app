package com.bank.repository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.bank.entity.Account;

@ConditionalOnProperty(value = "switch.database.to", havingValue = "mongo")
@Repository
public interface AccountRepositoryMongo extends MongoRepository<Account, String>, AccountRepository{

	Account findByAccountNumber(String accountNumber);
}
