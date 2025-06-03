package com.bank.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import com.bank.service.AccountService;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/accounts")
public class AccountController {

	@Autowired
	private AccountService service;

	@Autowired
	private ModelMapper modelMapper;

	@PostMapping
	public ResponseEntity<Account> create(@RequestBody @Valid AccountDto accountDto) {
		return ResponseEntity.ok(service.create(modelMapper.map(accountDto, Account.class)));
	}

	@GetMapping
	public ResponseEntity<List<Account>> list(@PathVariable String customerId) throws Exception {
		return ResponseEntity.ok(service.list(customerId));
	}

	@GetMapping("/{accountNumber}")
	public ResponseEntity<Account> get(@PathVariable String customerId, @PathVariable String accountNumber)
			throws Exception {
		return ResponseEntity.ok(service.getByAccountNumber(customerId, accountNumber));
	}

	@DeleteMapping("/{accountNumber}")
	public ResponseEntity<Account> delete(@PathVariable String customerId, @PathVariable String accountNumber)
			throws Exception {
		service.delete(customerId, accountNumber);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{accountNumber}/activate")
	public ResponseEntity<Account> activate(@PathVariable String customerId, @PathVariable String accountNumber)
			throws Exception {
		return ResponseEntity.ok(service.activate(customerId, accountNumber));
	}

	@PutMapping("/{accountNumber}/deactivate")
	public ResponseEntity<Account> deactivate(@PathVariable String customerId, @PathVariable String accountNumber)
			throws Exception {
		return ResponseEntity.ok(service.deactivate(customerId, accountNumber));
	}
}
