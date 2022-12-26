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

import com.bank.dto.DepositMethod;
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
	
	@PutMapping("/{customerId}")
	public Customer update(@PathVariable String customerId, @RequestBody Customer customer) {
		return service.update(customerId, customer);
	}
	 
	@DeleteMapping("/{customerId}")
	public void delete(@PathVariable String customerId) {
		service.delete(customerId);
	}
	
	@GetMapping("/account/{accountNumber}")
	public Account getAccount(@PathVariable String accountNumber) throws Exception {
		return service.getAccount(accountNumber);
	}

//	@PutMapping("/{customerId}/account/{accountNumber}/deposit")
//	public String deposit(@PathVariable String customerId, @PathVariable String accountNumber, @RequestBody Customer customer) throws Exception {
//		return service.deposit(customerId, accountNumber, customer);
//	}
	@PutMapping("/account/deposit")
	public String deposit(@RequestBody DepositMethod depositObj) throws Exception {
		return service.deposit(depositObj);
	}
	
	@PutMapping("/{customerId}/account/{accountNumber}/withdrawal")
	public String withdrawal(@PathVariable String customerId, @PathVariable String accountNumber, @RequestBody Customer customer) throws Exception {
		return service.withdrawal(customerId, accountNumber, customer);
	}
	
	@PutMapping("/{customerId}/account/{accountNumber}/deactivate")
	public String deActivate(@PathVariable String customerId, @PathVariable String accountNumber) {
		return service.deActivate(customerId, accountNumber);
	}
	
	@PutMapping("/{customerId}/account/{accountNumber}/activate")
	public String activate(@PathVariable String customerId, @PathVariable String accountNumber) {
		return service.activate(customerId, accountNumber);
	}
	
	@GetMapping("/{customerId}/account/{accountNumber}/transaction")
	public List<Transaction> getAllTransaction(@PathVariable String customerId, @PathVariable String accountNumber) {
		return service.getAllTransactions(customerId, accountNumber);
	}
	
	@PutMapping("/{customerId}/account/{accountNumber}/transfer")
	public String transfer(@PathVariable String customerId,  @PathVariable String accountNumber, @RequestBody Customer ReceivingCustomer) throws Exception {
		return service.moneyTransfer(customerId, accountNumber, ReceivingCustomer);
		
	}

}