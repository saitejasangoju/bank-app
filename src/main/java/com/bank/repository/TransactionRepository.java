package com.bank.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.bank.entity.Transaction;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String>{

}
