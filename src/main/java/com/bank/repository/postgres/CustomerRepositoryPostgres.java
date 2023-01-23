package com.bank.repository.postgres;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.entity.Customer;
import com.bank.repository.CustomerRepository;

@ConditionalOnProperty(value = "switch.database.to", havingValue = "postgres")
@Repository
public interface CustomerRepositoryPostgres extends JpaRepository<Customer, String>, CustomerRepository{
	
}