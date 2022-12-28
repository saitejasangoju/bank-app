package com.bank.service;

import java.io.NotActiveException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import com.bank.dto.CreditDebit;
import com.bank.dto.MoneyTransfer;
import com.bank.entity.Account;
import com.bank.entity.Transaction;
import com.bank.entity.TransactionType;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import com.bank.util.Utility;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionService {

	private static final String INVALID_ACCOUNT_NUMBER = "Invalid account number";

	@Autowired
	private Utility util;

	@Autowired
	private AccountRepository accountRepo;

	@Autowired
	private TransactionRepository transactionRepo;

	// list all transactions
	public List<Transaction> list(String customerId, String accountNumber) {
		util.validateCustomer(customerId);
		Account account = accountRepo.findByAccountNumber(accountNumber);
		if (account == null)
			throw new NoSuchElementException("Account doesn't exist");
		return transactionRepo.findAll();
	}

	// get transactions by id
	public Transaction getById(String customerId, String accountNumber, String id) {
		util.validateCustomer(customerId);
		Account account = accountRepo.findByAccountNumber(accountNumber);
		if (account == null) {
			throw new NoSuchElementException(INVALID_ACCOUNT_NUMBER);
		}
		if (!account.getCustomerId().equals(customerId)) {
			throw new IllegalArgumentException("Customer id doesn't contain account of number " + accountNumber);
		}
		return transactionRepo.findById(id).orElseThrow(() -> new NoSuchElementException("Transaction doesn't exist"));
	}

	// get last 2 days transactions by account number
	public List<Transaction> getRecentTransactions(String customerId, String accountNumber) {
		util.validateCustomer(customerId);
		Account account = accountRepo.findByAccountNumber(accountNumber);
		if(account == null) {
			throw new NoSuchElementException(INVALID_ACCOUNT_NUMBER);
		}
		log.info("Customer fetched of id {} ", accountNumber);
		List<Transaction> list = transactionRepo.findByAccountNumber(accountNumber);
		Transaction recent = list.get(list.size() - 1);
		// it contains date and time
		String recentDate = recent.getDate().toString();
		log.info("fetched recent date is {} ", recentDate);
		String[] splitRecentDate = recentDate.split("T");
		// it contains only date, we don't need time
		String onlyRecentDate = splitRecentDate[0];
		log.info("fetched only date {} ", onlyRecentDate);
		String[] splitOnlyDate = onlyRecentDate.split("-");
		int recentDay = Integer.parseInt(splitOnlyDate[0]);
		int recentMonth = Integer.parseInt(splitOnlyDate[1]);
		int recentYear = Integer.parseInt(splitOnlyDate[2]);
		List<Transaction> result = new ArrayList<>();
		for (Transaction t : list) {
			// transactionDate contains date and time
			String transactionDate = t.getDate().toString();
			log.info("fetched transaction date {} ", transactionDate);
			String[] splitTransactionDate = transactionDate.split("T");
			// it contains only date, time is removed
			String onlyTransactionDate = splitTransactionDate[0];
			log.info("fetched only date {} ", onlyTransactionDate);
			String[] splitDate = onlyTransactionDate.split("-");
			int day = Integer.parseInt(splitDate[0]);
			int month = Integer.parseInt(splitDate[1]);
			int year = Integer.parseInt(splitDate[2]);
			if((month == recentMonth) && (year == recentYear) && ((recentDay - day) <= 2)) {
				result.add(t);
			}
		}
		return result;
	}

	// deposit money into account
	public Transaction deposit(String customerId, String accountNumber, CreditDebit credit) throws NotActiveException {
		if (credit.getAmount() <= 0) {
			throw new IllegalArgumentException("Sorry, You cannot deposit 0 or lesser");
		}
		util.validateCustomer(customerId);
		Account account = accountRepo.findByAccountNumber(accountNumber);
		if (account == null)
			throw new NoSuchElementException(INVALID_ACCOUNT_NUMBER);
		if (!account.getCustomerId().equals(customerId))
			throw new IllegalArgumentException("customer doesn't have an account of number " + accountNumber);
		if (!account.isActive())
			throw new NotActiveException("Account is not active");
		Transaction transaction = new Transaction();
		transaction.setCustomerId(customerId);
		transaction.setAccountNumber(accountNumber);
		transaction.setAmount(credit.getAmount());
		transaction.setType(TransactionType.DEPOSIT);
		transactionRepo.save(transaction);
		account.setAccountBalance(account.getAccountBalance() + transaction.getAmount());
		accountRepo.save(account);
		return transaction;
	}

	// withdrawing money from account
	public Transaction withdrawal(String customerId, String accountNumber, CreditDebit debit)
			throws NotActiveException {
		if (debit.getAmount() <= 0) {
			throw new IllegalArgumentException("Sorry, You cannot withdraw 0 or lesser");
		}
		util.validateCustomer(customerId);
		Account account = accountRepo.findByAccountNumber(accountNumber);
		if (account == null)
			throw new NoSuchElementException(INVALID_ACCOUNT_NUMBER);
		if (!account.getCustomerId().equals(customerId))
			throw new IllegalArgumentException("customer doesn't have an account of number " + accountNumber);
		if (!account.isActive())
			throw new NotActiveException("Account is not active");
		Transaction transaction = new Transaction();
		transaction.setCustomerId(customerId);
		transaction.setAccountNumber(accountNumber);
		transaction.setAmount(debit.getAmount());
		transaction.setType(TransactionType.WITHDRAW);
		transactionRepo.save(transaction);
		account.setAccountBalance(account.getAccountBalance() + transaction.getAmount());
		accountRepo.save(account);
		return transaction;
	}

	// transfer money from one account to another using account number
	public List<Transaction> moneyTransfer(String customerId, String accountNumber, MoneyTransfer transferObj)
			throws Exception {
		// list for storing transactions at sender and receiver sides
		util.validateCustomer(customerId);
		List<Transaction> transactionsList = new ArrayList<>();
		// checking for valid sender account
		Account sendingAccount = accountRepo.findByAccountNumber(accountNumber);
		if (!sendingAccount.isActive())
			throw new NotActiveException("Sender account is not active");
		log.info("account id of sender is " + accountNumber);
		// checking for valid reciever account
		Account receiverAccount = accountRepo.findByAccountNumber(transferObj.getReceiver());
		if (!receiverAccount.isActive())
			throw new NotActiveException("Receiver account is not active");
		log.info("account number of receiver is " + transferObj.getReceiver());
		// transaction at sender
		Transaction transactionAtSender = new Transaction(TransactionType.WITHDRAW);
		double amount = transferObj.getAmount();
		double balanceAfterDebit = sendingAccount.getAccountBalance() - amount;
		log.info("balance after debit " + balanceAfterDebit);
		transactionAtSender.setAccountNumber(sendingAccount.getAccountNumber());
		transactionAtSender.setCustomerId(sendingAccount.getCustomerId());
		transactionAtSender.setAmount(amount);
		transactionAtSender = transactionRepo.save(transactionAtSender);
		transactionsList.add(transactionAtSender);
		// transaction at receiver
		Transaction transactionAtReceiver = new Transaction(TransactionType.DEPOSIT);
		double balanceAfterCredit = receiverAccount.getAccountBalance() + amount;
		log.info("balance after credit " + balanceAfterCredit);
		transactionAtReceiver.setAccountNumber(receiverAccount.getAccountNumber());
		transactionAtReceiver.setCustomerId(receiverAccount.getCustomerId());
		transactionAtReceiver.setAmount(amount);
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
}
