package com.bank.repository;

import java.util.List;

import com.bank.entity.Account;

public interface AccountRepository{
	
	Account save(Account account);
	List<Account> findAll();
	void delete(Account account);
	
	Account findByAccountNumber(String accountNumber);
}
