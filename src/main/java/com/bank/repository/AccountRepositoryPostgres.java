package com.bank.repository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.entity.Account;

@ConditionalOnProperty(value = "switch.database.to", havingValue = "postgres")
@Repository
public interface AccountRepositoryPostgres extends JpaRepository<Account, String>, AccountRepository{

//	Account findByAccountNumber(String accountNumber);
}
