package com.bank.service;

import java.io.NotActiveException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.entity.Account;
import com.bank.repository.AccountRepository;
import com.bank.util.Utility;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountService {

	@Autowired
	private Utility util;
	@Autowired
	private AccountRepository accountRepo;
	
	// get all accounts
	public List<Account> list(String customerId) {
		util.validateCustomer(customerId);
		return accountRepo.findAll();
	}

	// get account by account number
	public Account getByAccountNumber(String accountNumber) {
		return accountRepo.findByAccountNumber(accountNumber);

	}

	// create account
	public Account create(Account account) {
		account.setAccountNumber(util.generateAccountNumber());
		return accountRepo.save(account);
	}

	// delete account by account number
	public Account delete(String accountNumber) {
		Account account = accountRepo.findByAccountNumber(accountNumber);
		accountRepo.delete(account);
		return account;
	}

	// getting all customer accounts
	public List<Account> getCustomerAccounts(String customerId) throws Exception {
		util.validateCustomer(customerId);
		log.info("customer fetched of id : {} " + customerId);
		List<Account> allAccounts = accountRepo.findAll();
		List<Account> result = new ArrayList<>();
		for (Account acc : allAccounts) {
			if (acc.getCustomerId().equals(customerId) && acc.isActive())
				result.add(acc);
		}
		return result;
	}

	// get customer account by account number
	public Account getCustomerAccountByAccountNumber(String customerId, String accountNumber)
			throws NotActiveException {
		util.validateCustomer(customerId);
		Account account = accountRepo.findByAccountNumber(accountNumber);
		if (account.isActive())
			return account;
		else
			throw new NotActiveException("Account is de-activated");
	}

	// de-activating account
	public Account deactivate(String customerId, String accountNumber) throws NotActiveException {
		util.validateCustomer(customerId);
		Account account = accountRepo.findByAccountNumber(accountNumber);
		if (account == null)
			throw new NoSuchElementException("Account doesn't exist");
		if (!account.getCustomerId().equals(customerId))
			throw new IllegalArgumentException("Customer doesn't have account of number " + accountNumber);
		if (account.isActive())
			account.setActive(false);
		else
			throw new NotActiveException("Account is not active");
		accountRepo.save(account);
		return account;
	}

	// activating account
	public Account activate(String customerId, String accountNumber) {
		util.validateCustomer(customerId);
		Account account = accountRepo.findByAccountNumber(accountNumber);
		if (account == null) {
			throw new NoSuchElementException("Account doesn't exist");
		} else if (!account.getCustomerId().equals(customerId)) {
			throw new IllegalArgumentException("Customer doesn't have account of number " + accountNumber);
		}
		if (account.isActive()) {
			account.setActive(true);
		} else
			throw new IllegalArgumentException("Account is already active");
		accountRepo.save(account);
		return account;
	}

}
