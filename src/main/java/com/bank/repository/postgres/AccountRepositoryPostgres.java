package com.bank.repository.postgres;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.entity.Account;
import com.bank.repository.AccountRepository;

@ConditionalOnProperty(value = "switch.database.to", havingValue = "postgres")
@Repository
public interface AccountRepositoryPostgres extends JpaRepository<Account, String>, AccountRepository{

}
