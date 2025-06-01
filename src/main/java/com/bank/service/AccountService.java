package com.bank.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.entity.Account;
import com.bank.repository.AccountRepository;
import com.bank.util.BankUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountService {

	@Autowired
	private BankUtil util;

	@Autowired
	private AccountRepository accountRepo;

	public Account create(Account account) {
		log.info("Creating new account for customer with id {}", account.getCustomerId());
		account.setAccountNumber(util.generateAccountNumber());
		return accountRepo.save(account);
	}

	public List<Account> list(String customerId) throws Exception {
		util.validateCustomer(customerId);
		log.debug("Get Accounts for customer with id {} ", customerId);
		return accountRepo.findByCustomerId(customerId);
	}

	public Account getByAccountNumber(String customerId, String accountNumber) throws Exception {
		util.validateCustomer(customerId);
		log.debug("Get Account for customer with id {} ", customerId);
		return accountRepo.findByAccountNumber(accountNumber)
				.orElseThrow(() -> new Exception("Account doesn't exist with number " + accountNumber));
	}

	public void delete(String customerId, String accountNumber) throws Exception {
		util.validateCustomer(customerId);
		accountRepo.deleteById(accountNumber);
		log.info("Deleted account with number {}", accountNumber);
	}

	public Account deactivate(String customerId, String accountNumber) throws Exception {
		util.validateCustomer(customerId);
		Account account = accountRepo.findByAccountNumber(accountNumber)
				.orElseThrow(() -> new Exception("Account doesn't exist with number " + accountNumber));
		if (account.isActive())
			account.setActive(false);
		else
			throw new Exception("Account is not ACTIVE");
		return accountRepo.save(account);
	}

	public Account activate(String customerId, String accountNumber) throws Exception {
		util.validateCustomer(customerId);
		Account account = accountRepo.findByAccountNumber(accountNumber)
				.orElseThrow(() -> new Exception("Account doesn't exist with number " + accountNumber));
		if (account.isActive()) {
			throw new IllegalArgumentException("Account is already ACTIVE");
		} else
			account.setActive(true);
		return accountRepo.save(account);
	}

}
