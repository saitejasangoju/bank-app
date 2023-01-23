package com.bank.repository.postgres;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.entity.Transaction;
import com.bank.repository.TransactionRepository;

@ConditionalOnProperty(value="switch.database.to", havingValue = "postgres")
@Repository
public interface TransactionRepositoryPostgres extends JpaRepository<Transaction, Long>, TransactionRepository{

}
