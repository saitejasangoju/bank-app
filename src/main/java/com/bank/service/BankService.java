package com.bank.service;

import java.io.NotActiveException;
import java.nio.file.FileAlreadyExistsException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bank.dto.MoneyTransfer;
import com.bank.entity.Account;
import com.bank.entity.Customer;
import com.bank.entity.Transaction;
import com.bank.entity.TransactionType;
import com.bank.repository.AccountRepository;
import com.bank.repository.CustomerRepository;
import com.bank.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BankService {

	@Autowired
	private CustomerRepository customerRepo;
	@Autowired
	private AccountRepository accountRepo;
	@Autowired
	private TransactionRepository transactionRepo;

	// getting current date and time
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	LocalDateTime now = LocalDateTime.now();

	// listing all customers
	public List<Customer> getCustomers() {
		log.info("fetched all the details ");
		return customerRepo.findAll();
	}

	// create customer
	public Customer createCustomer(Customer customer) throws Exception {
		Customer existingCustomer = customerRepo.findByAadhar(customer.getAadhar());
		// checking for existing customer using aadhar
		if (existingCustomer != null) {
			throw new FileAlreadyExistsException("Aadhar Number is already exist.");
		}
		// checking for valid aadhar number
		if (customer.getAadhar().length() != 12)
			throw new FileAlreadyExistsException("Aadhar Number should be 12 digits. ");
		// checking the age criteria using date of birth
		LocalDate dateOfBirth = LocalDate.parse(customer.getDob());
		LocalDate currDate = LocalDate.now();
		Period period = Period.between(currDate, dateOfBirth);
		int age = Math.abs(period.getYears());
		if (age < 18) {
			throw new Exception("Sorry, You don't have enough age to open account. ");
		}
		customer.setCustomerId(generateAccountNumber());
		customerRepo.save(customer);
		log.info("New customer is added successfully");
		return customer;
	}

	// delete customer permanently
	public Customer deleteCustomer(String customerId) {
		Customer customer = customerRepo.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException("Customer doesn't exist with id " + customerId));
		customerRepo.delete(customer);
		log.info("deleted the customer of id : " + customerId);
		return customer;
	}

	// update all the editable details
	public Customer updateCustomer(String customerId, Customer customer) {
		Customer existingCustomer = customerRepo.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException("Customer doesn't exist with id " + customerId));
		existingCustomer.setEmail(customer.getEmail());
		existingCustomer.setPhone(customer.getPhone());
		existingCustomer.setAddress(customer.getAddress());
		log.info("details are modified of customer id : " + customerId);
		customerRepo.save(existingCustomer);
		return existingCustomer;
	}

	// get all accounts
	public List<Account> getAccounts() {
		return accountRepo.findAll();
	}

	// get account by account number
	public Account getAccountByAccountNumber(String accountNumber) {
		Account account = accountRepo.findByAccountNumber(accountNumber);
		return account;
	}

	// create account
	public Account createAccount(Account account) {
		account.setAccountNumber(generateAccountNumber());
		return accountRepo.save(account);
	}

	// delete account by account number
	public Account deleteAccount(String accountNumber) {
		Account account = accountRepo.findByAccountNumber(accountNumber);
		accountRepo.delete(account);
		return account;
	}

	// getting all customer accounts
	public List<Account> getCustomerAccounts(String customerId) throws Exception {
		Customer customer = customerRepo.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException("Customer doesn't exist"));
		log.info("customer fetched of id : {} " + customerId);
		List<Account> allAccounts = accountRepo.findAll();
		List<Account> result = new ArrayList<>();
		for (Account acc : allAccounts) {
			if (acc.getCustomerId().equals(customerId))
				if (acc.isActive() == true)
					result.add(acc);
		}
		return result;
	}

	// get customer account by account number
	public Account getCustomerAccountByAccountNumber(String customerId, String accountNumber) throws Exception {
		Customer customer = customerRepo.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException("Customer doesn't exist"));
		Account account = accountRepo.findByAccountNumber(accountNumber);
		if(account.isActive() == true)
			return account;
		else
			throw new Exception("Account is de-activated");
	}
	
	// de-activating account 
	public Account accountDeactivate(String customerId, String accountNumber) throws Exception {
		Customer customer = customerRepo.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException("Customer doesn't exist"));
		Account account = accountRepo.findByAccountNumber(accountNumber);
		if(account.isActive() == true)
			account.setActive(false);
		else
			throw new Exception("Account is already de-activated");
		accountRepo.save(account);
		return account;
	}
	
	// activating account
	public Account accountActivate(String customerId, String accountNumber) throws Exception {
		Customer customer = customerRepo.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException("Customer doesn't exist"));
		Account account = accountRepo.findByAccountNumber(accountNumber);
		if(account.isActive() == false)
			account.setActive(true);
		else
			throw new Exception("Account is already active");
		accountRepo.save(account);
		return account;
	}

	// deposit money into account
	public Transaction deposit(String customerId, String accountNumber, Transaction transaction) throws Exception {
		Customer existingCustomer = customerRepo.findById(customerId).orElseThrow(
				() -> new NoSuchElementException("Customer doesn't exists with id " + customerId));
		Account account = accountRepo.findByAccountNumber(accountNumber);
		if(account.isActive() == false) {
			throw new NotActiveException("Account is not active");
		}
		if(transaction.getAmount() <= 0) {
			throw new Exception("Sorry, You cannot deposit 0 or lesser");
		}
		transaction.setCustomerId(customerId);
		transaction.setAccountNumber(accountNumber);
		TransactionType type = transaction.getType();
		transaction.setType(type.DEPOSIT);
		transaction.setDate(formatter.format(now));
		transactionRepo.save(transaction);
		account.setAccountBalance(account.getAccountBalance() + transaction.getAmount());
		accountRepo.save(account);
		return transaction;
	}

	// withdrawing money from account
	public Transaction withdrawal(String customerId, String accountNumber, Transaction transaction) throws Exception {
		Customer existingCustomer = customerRepo.findById(customerId).orElseThrow(
				() -> new NoSuchElementException("Customer doesn't exists with id " + customerId));
		Account account = accountRepo.findByAccountNumber(accountNumber);
		if(account.isActive() == false) {
			throw new Exception("Account is not active");
		}
		transaction.setCustomerId(customerId);
		transaction.setAccountNumber(accountNumber);
		TransactionType type = transaction.getType();
		transaction.setType(type.WITHDRAW);
		transaction.setDate(formatter.format(now));
		transactionRepo.save(transaction);
		account.setAccountBalance(account.getAccountBalance() - transaction.getAmount());
		accountRepo.save(account);
		return transaction;
	}
	
	// get transactions by id
	public Transaction getTransactionsById(String id){
		Transaction transaction = transactionRepo.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Transaction doesn't exist"));
		return transaction;
	}

	// transfer money from one account to another account using account number
	public List<Transaction> moneyTransfer(MoneyTransfer transferObj) throws Exception {
		List<Transaction> transactions = new ArrayList<>();
		Account sender = accountRepo.findByAccountNumber(transferObj.getSender());
		log.info("account id of sender is " + transferObj.getSender());
		Account receiver = accountRepo.findByAccountNumber(transferObj.getReceiver());
		log.info("account id of receiver is " + transferObj.getReceiver());
		// checking for valid sender account
		if (sender.isActive() == false)
			throw new NotActiveException("Sender account is not active");
		// checking for valid reciever account
		if (receiver.isActive() == false)
			throw new NotActiveException("Receiver account is not active");

		Transaction transactionAtSender = new Transaction();
		double debitAmount = transferObj.getAmount();
		double balanceAfterDebit = sender.getAccountBalance() - debitAmount;
		log.info("balance after debit " + balanceAfterDebit);
		transactionAtSender.setAccountNumber(sender.getAccountNumber());
		transactionAtSender.setCustomerId(sender.getCustomerId());
		transactionAtSender.setDate(formatter.format(now));
		log.info("the current date and time is " + formatter.format(now));
		transactionAtSender.setAmount(debitAmount);
		transactionAtSender.setType(TransactionType.WITHDRAW);
		transactionAtSender = transactionRepo.save(transactionAtSender);
		transactions.add(transactionAtSender);
		sender.setAccountBalance(balanceAfterDebit);
		accountRepo.save(sender);
	
		Transaction transactionAtReceiver = new Transaction();
		double creditAmount = transferObj.getAmount();
		double balanceAfterCredit = receiver.getAccountBalance() + creditAmount;
		log.info("balance after credit " + balanceAfterCredit);
		log.info("Automatically generated transaction id : " + transactionAtReceiver.getId());
		transactionAtReceiver.setAccountNumber(receiver.getAccountNumber());
		transactionAtReceiver.setCustomerId(receiver.getCustomerId());
		transactionAtReceiver.setDate(formatter.format(now));
		transactionAtReceiver.setAmount(creditAmount);
		transactionAtReceiver.setType(TransactionType.DEPOSIT);
		transactionAtReceiver = transactionRepo.save(transactionAtReceiver);
		transactions.add(transactionAtReceiver);
		receiver.setAccountBalance(balanceAfterCredit);
		accountRepo.save(receiver);
		return transactions;
	}

	// get last 2 days transactions by account number
	public List<Transaction> getLastTwoDaysTransactions(String customerId, String accountNumber) {
		Customer customer = customerRepo.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException("Account doesn't exist"));
		log.info("Customer fetched of id {} ", accountNumber);
		List<Transaction> allTransactions = transactionRepo.findAll();
		List<Transaction> list = new LinkedList<>();
		for (Transaction t : allTransactions) {
			if (t.getAccountNumber().equals(accountNumber)) {
				list.add(t);
			}
		}
		Transaction recent = list.get(list.size() - 1);
		// it contains date and time
		String recentDate = recent.getDate();
		log.info("fetched recent date is {} ", recentDate);
		String splitRecentDate[] = recentDate.split(" ");
		// it contains only date, we don't need time 
		String onlyRecentDate = splitRecentDate[0];
		log.info("fetched only date {} ", onlyRecentDate);
		String splitOnlyDate[] = onlyRecentDate.split("/");
		int recentDay = Integer.parseInt(splitOnlyDate[0]);
		int recentMonth = Integer.parseInt(splitOnlyDate[1]);
		int recentYear = Integer.parseInt(splitOnlyDate[2]);
		List<Transaction> result = new ArrayList<>();
		for (Transaction t : list) {
			// transactionDate contains date and time
			String transactionDate = t.getDate();
			log.info("fetched transaction date {} ", transactionDate);
			String splitTransactionDate[] = transactionDate.split(" ");
			// it contains only date, time is removed
			String onlyTransactionDate = splitTransactionDate[0];
			log.info("fetched only date {} ", onlyTransactionDate);
			String splitDate[] = onlyTransactionDate.split("/");
			int day = Integer.parseInt(splitDate[0]);
			int month = Integer.parseInt(splitDate[1]);
			int year = Integer.parseInt(splitDate[2]);
			if (month != recentMonth)
				continue;
			else if (year != recentYear)
				continue;
			else if ((recentDay - day) <= 2) {
				result.add(t);
			}
		}
		return result;
	}

	// generating random number
	public String generateAccountNumber() {
		Random random = new Random();
		String randomStr = "";
		for (int i = 1; i <= 13; i++) {
			if(i == 1) {
				int num = random.nextInt(9);
				if(num == 0) 
					randomStr += num + 1;	
			}
			else {
				int num = random.nextInt(9);
				randomStr += Integer.toString(num);
			}
		}
		return randomStr;
	}

	//
	public String generateTransactionId() {
		Random random = new Random();
		String randomStr = "";
		for (int i = 1; i <= 7; i++) {
			int num = random.nextInt(9);
			randomStr += Integer.toString(num);
		}
		return randomStr;
	}

}
