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
import com.bank.service.BankService;


@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {
	
	@Autowired
	private BankService service;
	
	@GetMapping
	public List<Account> getAll(){
		return service.getAccounts();
	}
	
	@GetMapping("/{accountNumber}")
	public Account getOne(@PathVariable String accountNumber) {
		return service.getAccountByAccountNumber(accountNumber);
	}
	
	@PostMapping
	public Account create(@RequestBody Account account) {
		return service.createAccount(account);
	}
	
	@DeleteMapping("/{accountNumber}")
	public Account delete(@PathVariable String accountNumber) {
		return service.deleteAccount(accountNumber);
	}
	
	@PutMapping("/{customerId}/accounts/{accountNumber}/deactivate")
	public Account accountDeactivate(@PathVariable String customerId, @PathVariable String accountNumber) throws Exception {
		return service.accountDeactivate(customerId, accountNumber);
	}
	
	@PutMapping("/{customerId}/accounts/{accountNumber}/activate")
	public Account accountActivate(@PathVariable String customerId, @PathVariable String accountNumber) throws Exception {
		return service.accountActivate(customerId, accountNumber);
	}

}
