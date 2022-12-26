package com.bank.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bank.entity.Account;
import com.bank.entity.Customer;
import com.bank.entity.Transaction;
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
	private TransactionRepository transactionRepo;

	// getting current date and time
	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	Date date = new Date();
	String dateTime = formatter.format(date);

	// listing all
	public List<Customer> getAll() {
		log.info("fetched all the details ");
		return customerRepo.findAll();
	}

	// create customer
	public Customer create(Customer customer) throws Exception {
		Account account = customer.getAccount();
		String customerId = customer.getAadhar();
		Customer existingCustomer = customerRepo.findByCustomerAadhar(customerId);
		// checking for existing customer using aadhar
		if (existingCustomer != null) {
			throw new Exception("Aadhar Number is already exist.");
		}
		// checking for valid aadhar number
		if (customer.getAadhar().length() != 12)
			throw new Exception("Aadhar Number should be 12 digits. ");
		// checking the age criteria using date of birth
		LocalDate dateOfBirth = LocalDate.parse(customer.getDob());
		LocalDate currDate = LocalDate.now();
		Period period = Period.between(currDate, dateOfBirth);
		int age = Math.abs(period.getYears());
		if (age < 18) {
			throw new Exception("Sorry, You don't have enough age to open account. ");
		}
		account = accountRepo.save(account);
		customer.setAccount(account);
		customerRepo.save(customer);
		log.info("New customer is added successfully");
		return customer;
	}

	// delete customer permanently
	public String delete(String customerId) {
		Customer customer = customerRepo.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException("Customer doesn't exist with id " + customerId));
		customerRepo.delete(customer);
		log.info("deleted the customer of id : " + customerId);
		return "Successfully deleted of customer id " + customerId;
	}

	// update all the editable details
	public Customer update(String customerId, Customer customer) {
		Customer existingCustomer = customerRepo.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException("Customer doesn't exist with id " + customerId));
		existingCustomer.setCustomerEmail(customer.getCustomerEmail());
		existingCustomer.setCustomerPhone(customer.getCustomerPhone());
		existingCustomer.setAddress(customer.getAddress());
		log.info("modified details of customer id : " + customerId);
		customerRepo.save(existingCustomer);
		return existingCustomer;
	}

	// getting account by account number
	public Account getAccount(String accountNumber) throws Exception {
		Customer customer = customerRepo.findById(accountNumber)
				.orElseThrow(() -> new NoSuchElementException("Customer doesn't exist"));
		log.info("customer fetched of id : {} " + accountNumber);
		Account account = customer.getAccount();
		log.info("Account fetched of number : {} ", account);
		if(account.isActive() == true)
			return account;
		else
			throw new Exception("Account is de-activated");
	}

	// depositing some amount into particular account using customer id
	public String deposit(String customerId, String accountNumber) throws Exception {
		Customer existingCustomer = customerRepo.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException("Customer doesn't exists with id " + customerId));
		log.info("Account feteched of customer id {} ", customerId);
		Account account = existingCustomer.getAccount();
		account = accountRepo.findById(accountNumber)
				.orElseThrow(() -> new NoSuchElementException("Account doesn't exists with number " + accountNumber));
		if (account.isActive() == false)
			throw new Exception("This is account is de-activated");
		Transaction transaction = account.getTransaction();
		double amount = transaction.getAmount();
		if (amount <= 0) {
			throw new Exception("Sorry! You cannot deposit amount 0 or lesser");
		}
		double totalBalance = account.getAccountBalance() + amount;
		log.info("the current date and time is " + dateTime);
		transaction.setId(generateRandomNumber());
		transaction.setDateTime(dateTime);
		transaction.setAmount(amount);
		transaction.setAccountNumber(account.getAccountNumber());
		transaction.setCustomerId(existingCustomer.getCustomerId());
		transaction.setCredit(transaction.getAmount());
		transaction.setDebit(0);
		transaction = transactionRepo.save(transaction);
		account.setTransaction(transaction);
		account.setAccountBalance(totalBalance);
		account = accountRepo.save(account);
		existingCustomer.setAccount(account);
		customerRepo.save(existingCustomer);
		log.info("Balance after deposit is " + totalBalance);
		return "successfully deposited of amount " + amount + ". Total balance is " + totalBalance;
	}
	
	// withdrawing some amount from particular account using customer id
	public String withdrawal(String customerId, String accountNumber) throws Exception {
		Customer customer = customerRepo.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException("Customer doesn't exist with id " + customerId));
		log.info("Account feteched of customer id {} ", customerId);
		Account account = customer.getAccount();
		if (account.isActive() == false)
			throw new Exception("This is account is de-activated");
		Transaction transaction = account.getTransaction();
		double amount = transaction.getAmount();
		double balanceLeft = account.getAccountBalance() - amount;
		if (balanceLeft < 0) {
			throw new Exception("Insufficient amount .. ");
		}
		log.info("the current date and time is " + dateTime);
		transaction.setId(generateRandomNumber());
		transaction.setDateTime(dateTime);
		transaction.setAmount(amount);
		transaction.setAccountNumber(account.getAccountNumber());
		transaction.setCustomerId(customer.getCustomerId());
		transaction.setCredit(0);
		transaction.setDebit(amount);
		transaction = transactionRepo.save(transaction);
		account.setTransaction(transaction);
		account.setAccountBalance(balanceLeft);
		account = accountRepo.save(account);
		customer.setAccount(account);
		customerRepo.save(customer);
		log.info("balance after withdrawal is " + balanceLeft);
		return "successfully withdrawn of amount " + amount + ". Balance left " + balanceLeft;
	}

	// de-activating account using customer id
	public String deActivate(String customerId, String accountNumber) {
		Customer customer = customerRepo.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException("Customer doesn't exist with id " + customerId));
		log.info("Account fetched of customer id {} " + customerId);
		Account account = accountRepo.findById(accountNumber)
				.orElseThrow(() -> new NoSuchElementException("Account doesn't exist with number " + accountNumber));
		account.setActive(false);
		account = accountRepo.save(account);
		customer.setAccount(account);
		customerRepo.save(customer);
		return "Account is de-activated of number " + account.getAccountNumber();
	}

	// activating account using customer id
	public String activate(String customerId, String accountNumber) {
		Customer customer = customerRepo.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException("Customer doesn't exist with id " + customerId));
		log.info("Account fetched of customer id {} " + customerId);
		Account account = accountRepo.findById(accountNumber)
				.orElseThrow(() -> new NoSuchElementException("Account doesn't exist with number " + accountNumber));
		account.setActive(true);
		account = accountRepo.save(account);
		customer.setAccount(account);
		customerRepo.save(customer);
		return "Account is activated of number " + account.getAccountNumber();
	}

	// get all transactions of particular customer 
	public List<Transaction> getAllTransactions(String customerId, String accountNumber) {
		Customer customer = customerRepo.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException("Customer doesn't exist with id " + customerId));
		log.info("Customer fetched of customer id {} " + customerId);
		Account account = customer.getAccount();
		account = accountRepo.findById(accountNumber)
				.orElseThrow(() -> new NoSuchElementException("Account doesn't exist with number " + accountNumber));
		Transaction transaction = account.getTransaction();
		List<Transaction> allTransactions = transactionRepo.findAll();
		List<Transaction> list = new ArrayList<Transaction>();
		for (Transaction t : allTransactions) {
			if (customerId.equals(t.getCustomerId()))
				list.add(t);
		}
		return list;
	}

	// transfer money from one account to another using customer id
	public String moneyTransfer(String customerId, String accountNumber, Customer receivingCustomer) throws Exception {
		Customer sender = customerRepo.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException("Invalid sender of id " + customerId));
		log.info("Customer id of sender is " + customerId);
		Account senderAccount = sender.getAccount();
		senderAccount = accountRepo.findById(accountNumber)
				.orElseThrow(() -> new NoSuchElementException("Invalid sender account of number " + accountNumber));
		Account receiverAccount = receivingCustomer.getAccount();
		receiverAccount = accountRepo.findByAccountNumber(receiverAccount.getAccountNumber());
		// checking for valid sender account
		if (senderAccount.isActive() == false)
			throw new Exception("Sorry, Your account is de-activated");
		// checking for valid reciever account
		if (receiverAccount.isActive() == false)
			throw new Exception("Receivers account is not active");
		// transaction at sender side
		Transaction transactionAtSender = senderAccount.getTransaction();
		double debitAmount = transactionAtSender.getAmount();
		double balanceAfterDebit = senderAccount.getAccountBalance() - debitAmount;
		log.info("balance after debit " + balanceAfterDebit);
		senderAccount.setAccountBalance(balanceAfterDebit);
		senderAccount = accountRepo.save(senderAccount);
		sender.setAccount(senderAccount);
		// generating transaction id by calling generate random number method
		transactionAtSender.setId(generateRandomNumber());
		log.info("Automatically generated transaction id : " + transactionAtSender.getId());
		// getting current date and time
		transactionAtSender.setDateTime(dateTime);
		transactionAtSender.setAmount(debitAmount);
		log.info("the current date and time is " + dateTime);
		transactionAtSender.setAccountNumber(senderAccount.getAccountNumber());
		transactionAtSender.setCustomerId(sender.getCustomerId());
		transactionAtSender.setCredit(0);
		transactionAtSender.setDebit(debitAmount);
		transactionAtSender = transactionRepo.save(transactionAtSender);
		senderAccount.setTransaction(transactionAtSender);
		senderAccount = accountRepo.save(senderAccount);
		sender.setAccount(senderAccount);
		customerRepo.save(sender);
		// credit amount into receiver
		Transaction transactionAtReceiver = receiverAccount.getTransaction();
		double creditAmount = transactionAtReceiver.getAmount();
		double balanceAfterCredit = receiverAccount.getAccountBalance() + creditAmount;
		log.info("balance after credit " + balanceAfterCredit);
		receiverAccount.setAccountBalance(balanceAfterCredit);
		receiverAccount = accountRepo.save(receiverAccount);
		// generating random id using generate random number method
		transactionAtReceiver.setId(generateRandomNumber());
		log.info("Automatically generated transaction id : " + transactionAtReceiver.getId());
		log.info("the current date and time is " + dateTime);
		transactionAtReceiver.setDateTime(dateTime);
		transactionAtReceiver.setAmount(creditAmount);
		transactionAtReceiver.setAccountNumber(receiverAccount.getAccountNumber());
		transactionAtReceiver.setCustomerId(receivingCustomer.getCustomerId());
		transactionAtReceiver.setCredit(creditAmount);
		transactionAtReceiver.setDebit(0);
		transactionAtReceiver = transactionRepo.save(transactionAtReceiver);
		receiverAccount.setTransaction(transactionAtReceiver);
		receiverAccount = accountRepo.save(receiverAccount);
		receivingCustomer.setAccount(receiverAccount);
		customerRepo.save(receivingCustomer);
		return "Transfer Successfull";
	}

	// generating random number
	public String generateRandomNumber() {
		Random random = new Random();
		String randomStr = "";
		for (int i = 1; i <= 10; i++) {
			int num = random.nextInt(9);
			randomStr += Integer.toString(num);
		}
		return randomStr;
	}

}
