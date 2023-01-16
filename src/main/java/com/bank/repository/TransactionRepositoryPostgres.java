package com.bank.repository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.entity.Transaction;

@ConditionalOnProperty(value="switch.database.to", havingValue = "postgres")
@Repository
public interface TransactionRepositoryPostgres extends JpaRepository<Transaction, Long>, TransactionRepository{
//	List<Transaction> findByAccountNumber(String accountNumber);
}
