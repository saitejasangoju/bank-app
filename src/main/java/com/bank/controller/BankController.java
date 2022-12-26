package com.bank.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.dto.DepositAndWithdrawal;
import com.bank.dto.MoneyTransfer;
import com.bank.entity.Account;
import com.bank.entity.Customer;
import com.bank.entity.Transaction;
import com.bank.service.BankService;

@RestController
@RequestMapping("/api/v1/customer")
public class BankController {
	
	@Autowired
	private BankService service;
	
	@GetMapping
	public List<Customer> getAll() {
		return service.getAll();
	}

	@PostMapping
	public Customer create(@RequestBody Customer customer) throws Exception {
		return service.create(customer);
	}
	
	@PutMapping("/{id}")
	public Customer update(@PathVariable String id, @RequestBody Customer customer) {
		return service.update(id, customer);
	}
	 
	@DeleteMapping("/{id}")
	public void delete(@PathVariable String customerId) {
		service.delete(customerId);
	}
	
	@GetMapping("/account/{accountNumber}")
	public Account getAccount(@PathVariable String accountNumber) throws Exception {
		return service.getAccount(accountNumber);
	}

	@PutMapping("/account/deposit")
	public String deposit(@RequestBody DepositAndWithdrawal obj) throws Exception {
		return service.deposit(obj);
	}
	
	@PutMapping("/account/withdrawal")
	public String withdrawal(@RequestBody DepositAndWithdrawal obj) throws Exception {
		return service.withdrawal(obj);
	}
	
	@PutMapping("/account/deactivate/{accountNumber}")
	public String deActivate(@PathVariable String accountNumber) {
		return service.deActivate(accountNumber);
	}
	
	@PutMapping("/account/activate/{accountNumber}")
	public String activate(@PathVariable String accountNumber) {
		return service.activate(accountNumber);
	}
	
	@GetMapping("/account/transaction/{accountNumber}")
	public List<Transaction> getAllTransaction(@PathVariable String accountNumber) {
		return service.getAllTransactions(accountNumber);
	}
	
	@PutMapping("/account/transfer")
	public String transfer(@RequestBody MoneyTransfer obj) throws Exception {
		return service.moneyTransfer(obj);
	}

}