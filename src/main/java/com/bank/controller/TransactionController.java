package com.bank.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.bank.entity.Transaction;
import com.bank.service.BankService;

@RestController

public class TransactionController {
	
	@Autowired
	private BankService service;
	

	@GetMapping("/api/v1/customers/{customerId}/accounts/{accountNumber}/transactions")
	public List<Transaction> getTransactionsByAccountNumber(@PathVariable String customerId, @PathVariable String accountNumber){
		return service.getLastTwoDaysTransactions(customerId, accountNumber);
	}
	
	@GetMapping("/api/v1/customers/{customerId}/accounts/{accountNumber}/transactions/{id}")
	public Transaction getTransactionsById(@PathVariable String id){
		return service.getTransactionsById(id);
	}
	
}
