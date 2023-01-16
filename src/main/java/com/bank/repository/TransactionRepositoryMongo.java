package com.bank.repository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.bank.entity.Transaction;


@ConditionalOnProperty(value = "switch.database.to", havingValue = "mongo")
@Repository
public interface TransactionRepositoryMongo extends MongoRepository<Transaction, Long>, TransactionRepository{

//	List<Transaction> findByAccountNumber(String accountNumber);
//	Transaction findById(Long id);
}