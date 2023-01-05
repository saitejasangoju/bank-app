package com.bank.controller;

import java.io.NotActiveException;
import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.dto.AccountDto;
import com.bank.entity.Account;
import com.bank.exception.CustomerNotMatchAccount;
import com.bank.service.AccountService;


@RestController
@RequestMapping("/api/v1/customers")
public class AccountController {
	
	@Autowired
	private AccountService service;
	
	@Autowired
	ModelMapper modelMapper;
	
	@GetMapping("/{customerId}/accounts")
	public List<Account> list(@PathVariable String customerId){
		return service.list(customerId);
	}
	
	@GetMapping("/{customerId}/accounts/{accountNumber}")
	public Account get(@PathVariable String customerId, @PathVariable String accountNumber) throws CustomerNotMatchAccount {
		return service.getByAccountNumber(customerId, accountNumber);
	}
	
	@PostMapping("/{customerId}/accounts")
	public Account create(@RequestBody @Valid AccountDto accountDto) {
		return service.create(modelMapper.map(accountDto, Account.class));
	}
	
	@DeleteMapping("/{customerId}/accounts/{accountNumber}")
	public Account delete(@PathVariable String customerId, @PathVariable String accountNumber) throws CustomerNotMatchAccount {
		return service.delete(customerId, accountNumber);
	}
	
	@PutMapping("/{customerId}/accounts/{accountNumber}/activate")
	public Account activate(@PathVariable String customerId, @PathVariable String accountNumber) {
		return service.activate(customerId, accountNumber);
	}
	
	@PutMapping("/{customerId}/accounts/{accountNumber}/deactivate")
	public Account deactivate(@PathVariable String customerId, @PathVariable String accountNumber) throws NotActiveException {
		return service.deactivate(customerId, accountNumber);
	}
}
