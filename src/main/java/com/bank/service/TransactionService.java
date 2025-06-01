package com.bank.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import com.bank.util.BankUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionService {

	@Autowired
	private BankUtil util;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	public List<Transaction> list(String customerId, String accountNumber) throws Exception {
		util.validateCustomer(customerId);
		return transactionRepository.findByCustomerIdAndAccountNumber(customerId, accountNumber);
	}

	public Transaction getById(String customerId, String accountNumber, String id) throws Exception {
		util.validateCustomer(customerId);
		return transactionRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Transaction doesn't exist"));
	}

	public List<Transaction> getRecentTransactions(String customerId, String accountNumber) throws Exception {
		util.validateCustomer(customerId);
		Instant date = LocalDate.now().minusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant();
		return transactionRepository.findByCustomerIdAndAccountNumberAndDateGreaterThanEqual(customerId, accountNumber,
				date);
	}

	@Transactional
	public Transaction deposit(String customerId, String accountNumber, CreditDebit credit) throws Exception {
		util.validateCustomer(customerId);
		Account account = util.getAccount(accountNumber);
		Transaction transaction = buildTransaction(account, credit.getAmount(), TransactionType.DEPOSIT);
		transactionRepository.save(transaction);
		account.setAccountBalance(account.getAccountBalance() + transaction.getAmount());
		accountRepository.save(account);
		return transaction;
	}

	@Transactional
	public Transaction withdrawal(String customerId, String accountNumber, CreditDebit debit) throws Exception {
		util.validateCustomer(customerId);
		Account account = util.getAccount(accountNumber);
		if (account.getAccountBalance() < debit.getAmount()) {
			throw new Exception("Insufficient balance");
		}
		Transaction transaction = buildTransaction(account, debit.getAmount(), TransactionType.WITHDRAW);
		transactionRepository.save(transaction);
		account.setAccountBalance(account.getAccountBalance() - transaction.getAmount());
		accountRepository.save(account);
		return transaction;
	}

	@Transactional
	public List<Transaction> moneyTransfer(String customerId, String accountNumber, MoneyTransfer transferObj)
			throws Exception {
		util.validateCustomer(customerId);
		Account senderAccount = util.getAccount(accountNumber);
		log.info("Sender account number is {}", accountNumber);
		Account receiverAccount = util.getAccount(transferObj.getReceiver());
		log.info("Receiver account number is " + transferObj.getReceiver());
		List<Transaction> transactionsList = new ArrayList<>();
		Transaction senderTransaction = buildTransaction(senderAccount, transferObj.getAmount(),
				TransactionType.WITHDRAW);
		senderTransaction = transactionRepository.save(senderTransaction);
		transactionsList.add(senderTransaction);
		Transaction receiverTransaction = buildTransaction(receiverAccount, transferObj.getAmount(),
				TransactionType.DEPOSIT);
		receiverTransaction = transactionRepository.save(receiverTransaction);
		transactionsList.add(receiverTransaction);
		senderAccount.setAccountBalance(senderAccount.getAccountBalance() - transferObj.getAmount());
		accountRepository.save(senderAccount);
		receiverAccount.setAccountBalance(receiverAccount.getAccountBalance() + transferObj.getAmount());
		accountRepository.save(receiverAccount);
		return transactionsList;
	}

	private Transaction buildTransaction(Account account, double amount, TransactionType type) {
		return Transaction.builder().customerId(account.getCustomerId()).accountNumber(account.getAccountNumber())
				.amount(amount).type(type).build();
	}

	public String deleteByAccountNumber(String customerId, String accountNumber) throws Exception {
		util.validateCustomer(customerId);
		util.getAccount(accountNumber);
		transactionRepository.deleteByAccountNumber(accountNumber);
		return "Deleted Successfully";
	}
}
