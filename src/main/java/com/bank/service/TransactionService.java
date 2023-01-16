package com.bank.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.dto.CreditDebit;
import com.bank.dto.MoneyTransfer;
import com.bank.entity.Account;
import com.bank.entity.Transaction;
import com.bank.entity.TransactionType;
import com.bank.exception.CustomerNotMatchAccount;
import com.bank.exception.NotActiveException;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionService {

	private static final String INVALID_ACCOUNT_NUMBER = "Invalid account number";
	private static final String INVALID_CUSTOMER_REQUEST = "Customer deesn't contain account";

	@Autowired
	private Utility util;

	@Autowired
	private AccountRepository accountRepo;

	@Autowired
	private TransactionRepository transactionRepo;

	// list all transactions
	public List<Transaction> list(String customerId, String accountNumber) throws CustomerNotMatchAccount {
		util.validateCustomer(customerId);
		Account account = accountRepo.findByAccountNumber(accountNumber);
		if (account == null)
			throw new NoSuchElementException("Account doesn't exist");
		else if (!account.getCustomerId().equals(customerId))
			throw new CustomerNotMatchAccount(INVALID_CUSTOMER_REQUEST);
		List<Transaction> all = transactionRepo.findAll();
		List<Transaction> list = new ArrayList<>();
		for (Transaction t : all) {
			if (t.getAccountNumber().equals(accountNumber)) {
				list.add(t);
			}
		}
		return list;
	}

	// get transactions by id
	public Transaction getById(String customerId, String accountNumber, Long id) {
		util.validateCustomer(customerId);
		Account account = accountRepo.findByAccountNumber(accountNumber);
		if (account == null) {
			throw new NoSuchElementException(INVALID_ACCOUNT_NUMBER);
		}
		if (!account.getCustomerId().equals(customerId)) {
			throw new IllegalArgumentException("Customer id doesn't contain account number " + accountNumber);
		}
		return transactionRepo.findById(id);
	}

	// getting recent transactions maximum 10
	public List<Transaction> getRecentTransactions(String customerId, String accountNumber)
			throws CustomerNotMatchAccount {
		util.validateCustomer(customerId);
		Account account = accountRepo.findByAccountNumber(accountNumber);
		if (account == null)
			throw new IllegalArgumentException(INVALID_ACCOUNT_NUMBER);
		else if (!account.getCustomerId().equals(customerId))
			throw new CustomerNotMatchAccount(INVALID_CUSTOMER_REQUEST);
		log.info("Account fetched of number {} ", accountNumber);
		List<Transaction> allList = transactionRepo.findAll();
		log.info("All list of transactions {}", allList);
		List<Transaction> list = new ArrayList<>();
		for (Transaction t : allList) {
			if (t.getAccountNumber().equals(accountNumber)) {
				list.add(t);
			}
		}
		log.info("list of transactions {}", list);
		List<Transaction> result = new ArrayList<>();
		if (list.size() > 10) {
			int start = list.size() - 10;
			for (int i = start; i < list.size(); i++) {
				result.add(list.get(i));
			}
		} else
			return list;
		return result;
	}

	// deposit money into account
	@Transactional
	public Transaction deposit(String customerId, String accountNumber, CreditDebit credit) throws NotActiveException {
		if (credit.getAmount() <= 0) {
			throw new IllegalArgumentException("Sorry, You cannot deposit 0 or lesser");
		}
		util.validateCustomer(customerId);
		Account account = accountRepo.findByAccountNumber(accountNumber);
		if (account == null)
			throw new NoSuchElementException(INVALID_ACCOUNT_NUMBER);
		if (!account.getCustomerId().equals(customerId))
			throw new IllegalArgumentException(INVALID_CUSTOMER_REQUEST);
		if (!account.isActive())
			throw new NotActiveException("Account is not active");
		Transaction transaction = Transaction.builder().id(util.generateTransactionId()).customerId(customerId).accountNumber(accountNumber)
				.amount(credit.getAmount()).type(TransactionType.DEPOSIT).build();
		transactionRepo.save(transaction);
		account.setAccountBalance(account.getAccountBalance() + transaction.getAmount());
		accountRepo.save(account);
		return transaction;
	}

	// withdrawing money from account
	@Transactional
	public Transaction withdrawal(String customerId, String accountNumber, CreditDebit debit) {
		util.validateCustomer(customerId);
		Account account = accountRepo.findByAccountNumber(accountNumber);
		if (account == null)
			throw new NoSuchElementException(INVALID_ACCOUNT_NUMBER);
		if (!account.getCustomerId().equals(customerId))
			throw new IllegalArgumentException(INVALID_CUSTOMER_REQUEST);
		if (!account.isActive())
			throw new IllegalArgumentException("Account is not active");
		if (account.getAccountBalance() <= 0)
			throw new IllegalArgumentException("Insufficient Account Balance");
		if (debit.getAmount() <= 0) {
			throw new IllegalArgumentException("Sorry, You cannot withdraw 0 or lesser");
		}
		Transaction transaction = Transaction.builder().id(util.generateTransactionId()).customerId(customerId).accountNumber(accountNumber)
				.amount(debit.getAmount()).type(TransactionType.WITHDRAW).build();
		transactionRepo.save(transaction);
		account.setAccountBalance(account.getAccountBalance() - transaction.getAmount());
		accountRepo.save(account);
		return transaction;
	}

	// transfer money from one account to another using account number
	@Transactional
	public List<Transaction> moneyTransfer(String customerId, String accountNumber, MoneyTransfer transferObj)
			throws Exception {
		if (transferObj.getAmount() <= 0)
			throw new IllegalArgumentException("Cannot transfer amount 0 or lesser");
		util.validateCustomer(customerId);
		// checking for valid sender account
		Account sendingAccount = accountRepo.findByAccountNumber(accountNumber);
		if (!sendingAccount.isActive())
			throw new IllegalArgumentException("Sender account is not active");
		log.info("account id of sender is " + accountNumber);
		// checking for valid reciever account
		Account receiverAccount = accountRepo.findByAccountNumber(transferObj.getReceiver());
		if (!receiverAccount.isActive())
			throw new IllegalArgumentException("Receiver account is not active");
		log.info("account number of receiver is " + transferObj.getReceiver());
		if(sendingAccount.getAccountBalance() - transferObj.getAmount() < 0)
			throw new IllegalArgumentException("Insufficient amount to send");
		List<Transaction> transactionsList = new ArrayList<>();
		// transaction at sender
		double amount = transferObj.getAmount();
		double balanceAfterDebit = sendingAccount.getAccountBalance() - amount;
		Transaction transactionAtSender = Transaction.builder().id(util.generateTransactionId()).accountNumber(sendingAccount.getAccountNumber())
				.customerId(sendingAccount.getCustomerId()).amount(amount).type(TransactionType.WITHDRAW).build();
		log.info("balance after debit " + balanceAfterDebit);
		transactionAtSender = transactionRepo.save(transactionAtSender);
		transactionsList.add(transactionAtSender);
		// transaction at receiver
		double balanceAfterCredit = receiverAccount.getAccountBalance() + amount;
		Transaction transactionAtReceiver = Transaction.builder().id(util.generateTransactionId()).accountNumber(receiverAccount.getAccountNumber())
				.customerId(receiverAccount.getCustomerId()).amount(amount).type(TransactionType.DEPOSIT).build();
		log.info("balance after credit " + balanceAfterCredit);
		transactionAtReceiver = transactionRepo.save(transactionAtReceiver);
		transactionsList.add(transactionAtReceiver);
		// updating sender account
		sendingAccount.setAccountBalance(balanceAfterDebit);
		accountRepo.save(sendingAccount);
		// updating receiver account
		receiverAccount.setAccountBalance(balanceAfterCredit);
		accountRepo.save(receiverAccount);
		return transactionsList;
	}

	public String deleteByAccountNumber(String customerId, String accountNumber) throws CustomerNotMatchAccount {
		util.validateCustomer(customerId);
		Account account = accountRepo.findByAccountNumber(accountNumber);
		if (account == null)
			throw new NoSuchElementException(INVALID_ACCOUNT_NUMBER);
		if (!account.getCustomerId().equals(customerId))
			throw new CustomerNotMatchAccount(INVALID_CUSTOMER_REQUEST);
		List<Transaction> allTransactions = transactionRepo.findAll();
		for (Transaction t : allTransactions) {
			if (t.getAccountNumber().equals(accountNumber)) {
				Long id = t.getId();
				Transaction transaction = transactionRepo.findById(id);
				transactionRepo.delete(transaction);
			}
		}
		return "Deleted Successfully";
	}
}
