package com.bank.service;

import java.io.NotActiveException;
import java.nio.file.FileAlreadyExistsException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import javax.naming.InsufficientResourcesException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bank.dto.DepositAndWithdrawal;
import com.bank.dto.MoneyTransfer;
import com.bank.entity.Account;
import com.bank.entity.Customer;
import com.bank.entity.Transaction;
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
		String aadhar = customer.getAadhar();
		Customer existingCustomer = customerRepo.findByAadhar(aadhar);
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
		customer.setId(generateAccountNumber());
		account.setAccountNumber(customer.getId());
		customer.setAccount(account);
		customerRepo.save(customer);
		log.info("New customer is added successfully");
		return customer;
	}

	// delete customer permanently
	public String delete(String id) {
		Customer customer = customerRepo.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Customer doesn't exist with id " + id));
		customerRepo.delete(customer);
		log.info("deleted the customer of id : " + id);
		return "Successfully deleted of customer id " + id;
	}

	// update all the editable details
	public Customer update(String id, Customer customer) {
		Customer existingCustomer = customerRepo.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Customer doesn't exist with id " + id));
		existingCustomer.setEmail(customer.getEmail());
		existingCustomer.setPhone(customer.getPhone());
		existingCustomer.setAddress(customer.getAddress());
		log.info("details are modified of customer id : " + id);
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
		if (account.isActive() == true)
			return account;
		else
			throw new Exception("Account is de-activated");
	}

	public String deposit(DepositAndWithdrawal depositObj) throws Exception {
		Customer existingCustomer = customerRepo.findById(depositObj.getAccountNumber()).orElseThrow(
				() -> new NoSuchElementException("Customer doesn't exists with id " + depositObj.getAccountNumber()));
		log.info("Account feteched of customer id {} ", depositObj.getAccountNumber());
		Account account = existingCustomer.getAccount();
		if (account.isActive() == false)
			throw new NotActiveException("This is account is de-activated");
		double amount = depositObj.getAmount();
		if (amount <= 0) {
			throw new Exception("Sorry! You cannot deposit amount 0 or lesser");
		}
		double totalBalance = account.getAccountBalance() + amount;
		account.setAccountBalance(totalBalance);
		log.info("Balance after deposit is " + totalBalance);
		existingCustomer.setAccount(account);
		customerRepo.save(existingCustomer);
		// transactions
		Transaction transaction = new Transaction();
		transaction.setId(generateTransactionId());
		transaction.setDateTime(dateTime);
		log.info("the current date and time is " + dateTime);
		transaction.setAmount(amount);
		transaction.setAccountNumber(account.getAccountNumber());
		transaction.setCredit(transaction.getAmount());
		transaction = transactionRepo.save(transaction);
		return "successfully deposited of amount " + amount + ". Total balance is " + totalBalance;
	}

	// withdrawing some amount from particular account using customer id
	public String withdrawal(DepositAndWithdrawal withdrawalObj) throws Exception {
		Customer customer = customerRepo.findById(withdrawalObj.getAccountNumber()).orElseThrow(
				() -> new NoSuchElementException("Customer doesn't exist with id " + withdrawalObj.getAccountNumber()));
		log.info("Account feteched of customer id {} ", withdrawalObj.getAccountNumber());
		Account account = customer.getAccount();
		if (account.isActive() == false)
			throw new NotActiveException("This is account is de-activated");
		Transaction transaction = new Transaction();
		double amount = withdrawalObj.getAmount();
		double balanceLeft = account.getAccountBalance() - amount;
		if (balanceLeft < 0) {
			throw new InsufficientResourcesException("Insufficient amount .. ");
		}
		account.setAccountBalance(balanceLeft);
		log.info("balance after withdrawal is " + balanceLeft);
		customer.setAccount(account);
		customerRepo.save(customer);
		// transactions
		log.info("the current date and time is " + dateTime);
		transaction.setId(generateTransactionId());
		transaction.setDateTime(dateTime);
		transaction.setAmount(amount);
		transaction.setAccountNumber(account.getAccountNumber());
		transaction.setDebit(amount);
		transaction = transactionRepo.save(transaction);
		return "successfully withdrawn of amount " + amount + ". Balance left " + balanceLeft;
	}

	// de-activating account using customer id
	public String deActivate(String accountNumber) {
		Customer customer = customerRepo.findById(accountNumber)
				.orElseThrow(() -> new NoSuchElementException("Customer doesn't exist with id " + accountNumber));
		log.info("Account fetched of customer id {} " + accountNumber);
		Account account = customer.getAccount();
		account.setActive(false);
		customer.setAccount(account);
		customerRepo.save(customer);
		return "Account is de-activated of number " + account.getAccountNumber();
	}

	// activating account using customer id
	public String activate(String accountNumber) {
		Customer customer = customerRepo.findById(accountNumber)
				.orElseThrow(() -> new NoSuchElementException("Customer doesn't exist with id " + accountNumber));
		log.info("Account fetched of customer id {} " + accountNumber);
		Account account = customer.getAccount();
		account.setActive(true);
		customer.setAccount(account);
		customerRepo.save(customer);
		return "Account is activated of number " + account.getAccountNumber();
	}

	// get all transactions of particular customer
	public List<Transaction> getAllTransactions(String accountNumber) {
		Customer customer = customerRepo.findById(accountNumber)
				.orElseThrow(() -> new NoSuchElementException("Customer doesn't exist with id " + accountNumber));
		log.info("Customer fetched of customer id {} " + accountNumber);
		Account account = customer.getAccount();
		List<Transaction> allTransactions = transactionRepo.findAll();
		List<Transaction> list = new ArrayList<Transaction>();
		for (Transaction t : allTransactions) {
			if (account.getAccountNumber().equals(t.getAccountNumber()))
				list.add(t);
		}
		return list;
	}

	// transfer money from one account to another using customer id
	public String moneyTransfer(MoneyTransfer transferObj) throws Exception {
		Customer sender = customerRepo.findById(transferObj.getSender())
				.orElseThrow(() -> new NoSuchElementException("Invalid sender account of number " + transferObj.getSender()));
		log.info("Customer id of sender is " + transferObj.getSender());
		Customer receiver = customerRepo.findById(transferObj.getReceiver())
				.orElseThrow(() -> new NoSuchElementException("Invalid receiver account of number " + transferObj.getReceiver()));
		Account senderAccount = sender.getAccount();
		Account receiverAccount = receiver.getAccount();
		// checking for valid sender account
		if (senderAccount.isActive() == false)
			throw new NotActiveException("Sorry, Your account is de-activated");
		// checking for valid reciever account
		if (receiverAccount.isActive() == false)
			throw new NotActiveException("Receivers account is not active");
		// transaction at sender side
		Transaction  transactionAtSender = new Transaction();
		double debitAmount = transferObj.getAmount();
		double balanceAfterDebit = senderAccount.getAccountBalance() - debitAmount;
		log.info("balance after debit " + balanceAfterDebit);
		senderAccount.setAccountBalance(balanceAfterDebit);
		sender.setAccount(senderAccount);
		customerRepo.save(sender);
		// generating transaction id by calling generate random number method
		transactionAtSender.setId(generateTransactionId());
		log.info("Automatically generated transaction id : " + transactionAtSender.getId());
		// getting current date and time
		transactionAtSender.setDateTime(dateTime);
		transactionAtSender.setAmount(debitAmount);
		log.info("the current date and time is " + dateTime);
		transactionAtSender.setAccountNumber(senderAccount.getAccountNumber());
		transactionAtSender.setDebit(debitAmount);
		transactionAtSender = transactionRepo.save(transactionAtSender);
		
		// credit amount into receiver
		Transaction transactionAtReceiver = new Transaction();
		double creditAmount = transferObj.getAmount();
		double balanceAfterCredit = receiverAccount.getAccountBalance() + creditAmount;
		log.info("balance after credit " + balanceAfterCredit);
		receiverAccount.setAccountBalance(balanceAfterCredit);
		receiver.setAccount(receiverAccount);
		customerRepo.save(receiver);
		// generating random id using generate random number method
		transactionAtReceiver.setId(generateTransactionId());
		log.info("Automatically generated transaction id : " + transactionAtReceiver.getId());
		log.info("the current date and time is " + dateTime);
		transactionAtReceiver.setDateTime(dateTime);
		transactionAtReceiver.setAmount(creditAmount);
		transactionAtReceiver.setAccountNumber(receiverAccount.getAccountNumber());
		transactionAtReceiver.setCredit(creditAmount);
		transactionAtReceiver = transactionRepo.save(transactionAtReceiver);
		return "Transfer Successfull";
	}

	// generating random number
	public String generateAccountNumber() {
		Random random = new Random();
		String randomStr = "";
		for (int i = 1; i <= 13; i++) {
			int num = random.nextInt(9);
			randomStr += Integer.toString(num);
		}
		return randomStr;
	}
	
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
