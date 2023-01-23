package com.bank.repository.mongo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.bank.entity.Customer;
import com.bank.repository.CustomerRepository;

@ConditionalOnProperty(value = "switch.database.to", havingValue = "mongo")
@Repository
public interface CustomerRepositoryMongo extends MongoRepository<Customer, String>, CustomerRepository{
	
}

