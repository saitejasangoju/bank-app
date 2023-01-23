package com.bank.repository;

import java.util.List;

import com.bank.entity.Transaction;

public interface TransactionRepository {

	List<Transaction> findAll();
	Transaction save(Transaction transaction);
	void delete(Transaction transaction);
	
	Transaction findById(long id);
	List<Transaction> findByAccountNumber(String accountNumber);
}


