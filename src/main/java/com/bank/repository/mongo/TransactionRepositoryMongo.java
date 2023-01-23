package com.bank.repository.mongo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.bank.entity.Transaction;
import com.bank.repository.TransactionRepository;


@ConditionalOnProperty(value = "switch.database.to", havingValue = "mongo")
@Repository
public interface TransactionRepositoryMongo extends MongoRepository<Transaction, Long>, TransactionRepository{

}