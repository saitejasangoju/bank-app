package com.bank.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bank.entity.Account;
import com.bank.exception.CustomerNotMatchAccount;
import com.bank.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountService {
	
	private static final String INVALID_ACCOUNT_NUMBER = "Account doesn't exist";
	private static final String CUSTOMER_FETCHED = "Customer fetched of id : {} ";
	@Autowired
	private Utility util;
	
	@Autowired
	private AccountRepository accountRepo;

	// create account
	public Account create(Account account) {
		util.validateCustomer(account.getCustomerId());
		log.info(CUSTOMER_FETCHED, account.getCustomerId());
		account.setAccountNumber(util.generateId());
		return accountRepo.save(account);
	}
	
	// get all accounts
	public List<Account> list(String customerId) {
		util.validateCustomer(customerId);
		log.info(CUSTOMER_FETCHED, customerId);
		List<Account> allAccounts = accountRepo.findAll();
		List<Account> accounts = new ArrayList<>();
		for(Account acc : allAccounts) {
			if(acc.getCustomerId().equals(customerId)) {
				accounts.add(acc);
			}
		}
		return accounts;
	}

	// get account by account number
	public Account getByAccountNumber(String customerId, String accountNumber) {
		util.validateCustomer(customerId);
		log.info(CUSTOMER_FETCHED, customerId);
		Account account = accountRepo.findByAccountNumber(accountNumber);
		if(account.getCustomerId().equals(customerId) && account.isActive()) 
			return account;
		else
			throw new NoSuchElementException(INVALID_ACCOUNT_NUMBER);
	}

	// delete account by account number
	public Account delete(String customerId, String accountNumber) throws CustomerNotMatchAccount {
		util.validateCustomer(customerId);
		Account account = accountRepo.findByAccountNumber(accountNumber);
		if(account.getCustomerId().equals(customerId))
			accountRepo.delete(account);
		else 
			throw new CustomerNotMatchAccount("Customer not matching account" + accountNumber);
		return account;
	}

	// de-activating account
	public Account deactivate(String customerId, String accountNumber) {
		util.validateCustomer(customerId);
		Account account = accountRepo.findByAccountNumber(accountNumber);
		if (account == null)
			throw new NoSuchElementException(INVALID_ACCOUNT_NUMBER);
		if (!account.getCustomerId().equals(customerId))
			throw new IllegalArgumentException("Customer doesn't have account of number " + accountNumber);
		if (account.isActive())
			account.setActive(false);
		else
			throw new IllegalArgumentException("Account is not active");
		accountRepo.save(account);
		return account;
	}

	// activating account
	public Account activate(String customerId, String accountNumber) {
		util.validateCustomer(customerId);
		Account account = accountRepo.findByAccountNumber(accountNumber);
		if (account == null) {
			throw new NoSuchElementException(INVALID_ACCOUNT_NUMBER);
		} else if (!account.getCustomerId().equals(customerId)) {
			throw new IllegalArgumentException("Customer doesn't have account of number " + accountNumber);
		}
		if (account.isActive()) {
			throw new IllegalArgumentException("Account is already active");
		} else
			account.setActive(true);
		accountRepo.save(account);
		return account;
	}

}
