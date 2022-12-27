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
import com.bank.entity.Account;
import com.bank.entity.Customer;
import com.bank.service.BankService;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
	
	@Autowired
	private BankService service;
	
	@GetMapping
	public List<Customer> getAll() {
		return service.getCustomers();
	}

	@PostMapping
	public Customer create(@RequestBody Customer customer) throws Exception {
		return service.createCustomer(customer);
	}
	
	@PutMapping("/{customerId}")
	public Customer update(@PathVariable String customerId, @RequestBody Customer customer) {
		return service.updateCustomer(customerId, customer);
	}
	 
	@DeleteMapping("/{customerId}")
	public Customer delete(@PathVariable String customerId) {
		return service.deleteCustomer(customerId);
	}
	
	@GetMapping("/{customerId}/accounts")
	public List<Account> getCustomerAccounts(@PathVariable String customerId) throws Exception {
		return service.getCustomerAccounts(customerId);
	}
	
	@GetMapping("/{customerId}/accounts/{accountNumber}")
	public Account getCustomerAccountByAccountNumber(@PathVariable String customerId, @PathVariable String accountNumber) throws Exception {
		return service.getCustomerAccountByAccountNumber(customerId, accountNumber);
	}
}