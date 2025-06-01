package com.bank.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import com.bank.service.TransactionService;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/accounts/{accountNumber}/transactions")
public class TransactionController {

	@Autowired
	private TransactionService service;

	@GetMapping
	public ResponseEntity<List<Transaction>> getByAccountNumber(@PathVariable String customerId, @PathVariable String accountNumber)
			throws Exception {
		return ResponseEntity.ok(service.list(customerId, accountNumber));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Transaction> getById(@PathVariable String customerId, @PathVariable String accountNumber,
			@PathVariable String id) throws Exception {
		return ResponseEntity.ok(service.getById(customerId, accountNumber, id));
	}

	@GetMapping("/recent")
	public ResponseEntity<List<Transaction>> getRecent(@PathVariable String customerId, @PathVariable String accountNumber)
			throws Exception {
		return ResponseEntity.ok(service.getRecentTransactions(customerId, accountNumber));
	}

	@PostMapping("/deposit")
	public ResponseEntity<Transaction> deposit(@PathVariable String customerId, @PathVariable String accountNumber,
			@RequestBody CreditDebit credit) throws Exception {
		return ResponseEntity.ok(service.deposit(customerId, accountNumber, credit));
	}

	@PostMapping("/withdrawal")
	public ResponseEntity<Transaction> withdrawal(@PathVariable String customerId, @PathVariable String accountNumber,
			@RequestBody CreditDebit debit) throws Exception {
		return ResponseEntity.ok(service.withdrawal(customerId, accountNumber, debit));
	}

	@PostMapping("/transfer")
	public ResponseEntity<List<Transaction>> transfer(@PathVariable String customerId, @PathVariable String accountNumber,
			@RequestBody MoneyTransfer transferObj) throws Exception {
		return ResponseEntity.ok(service.moneyTransfer(customerId, accountNumber, transferObj));
	}

	@DeleteMapping
	public ResponseEntity<String> deleteByAccountNumber(@PathVariable String customerId, @PathVariable String accountNumber)
			throws Exception {
		return ResponseEntity.ok(service.deleteByAccountNumber(customerId, accountNumber));
	}

}
