 package com.bank.controller;

import java.io.NotActiveException;
import java.util.List;

import javax.naming.InsufficientResourcesException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bank.dto.CreditDebit;
import com.bank.dto.MoneyTransfer;
import com.bank.entity.Transaction;
import com.bank.exception.CustomerNotMatchAccount;
import com.bank.service.TransactionService;

@RestController
@RequestMapping("/api/v1/customers/")
public class TransactionController {
	
	@Autowired
	private TransactionService service;
	

	@GetMapping("/{customerId}/accounts/{accountNumber}/transactions")
	public List<Transaction> getByAccountNumber(@PathVariable String customerId, @PathVariable String accountNumber) throws CustomerNotMatchAccount{
		return service.list(customerId, accountNumber);
	}
	
	@GetMapping("/{customerId}/accounts/{accountNumber}/transactions/{id}")
	public Transaction getById(@PathVariable String customerId, @PathVariable String accountNumber, @PathVariable String id){
		return service.getById(customerId, accountNumber, id);
	}
	
	@GetMapping("/{customerId}/accounts/{accountNumber}/transactions/recent")
	public List<Transaction> getRecent(@PathVariable String customerId, @PathVariable String accountNumber) throws CustomerNotMatchAccount{
		return service.getRecentTransactions(customerId, accountNumber);
	}
	
	@PostMapping("/{customerId}/accounts/{accountNumber}/transactions/deposit")
	public Transaction deposit(@PathVariable String customerId, @PathVariable String accountNumber, @RequestBody CreditDebit credit) throws CustomerNotMatchAccount, NotActiveException {
		return service.deposit(customerId, accountNumber, credit);
	}

	@PostMapping("/{customerId}/accounts/{accountNumber}/transactions/withdrawal")
	public Transaction withdrawal(@PathVariable String customerId, @PathVariable String accountNumber, @RequestBody CreditDebit debit) throws NotActiveException, NotActiveException, CustomerNotMatchAccount, InsufficientResourcesException {
		return service.withdrawal(customerId, accountNumber, debit);
	}

	@PostMapping("/{customerId}/accounts/{accountNumber}/transactions/transfer")
	public List<Transaction> transfer(@PathVariable String customerId, @PathVariable String accountNumber, @RequestBody MoneyTransfer transferObj) throws Exception {
		return service.moneyTransfer(customerId, accountNumber, transferObj);
	}
	
	@DeleteMapping("/{customerId}/accounts/{accountNumber}/transactions/delete")
	public String deleteByAccountNumber(@PathVariable String customerId, @PathVariable String accountNumber) throws CustomerNotMatchAccount {
		return service.deleteByAccountNumber(customerId, accountNumber);
	}
	
}
