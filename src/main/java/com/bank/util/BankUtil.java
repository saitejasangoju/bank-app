package com.bank.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bank.entity.Account;
import com.bank.entity.Customer;
import com.bank.repository.AccountRepository;
import com.bank.repository.CustomerRepository;

@Component
public class BankUtil {

	private Map<String, Customer> CACHED_CUSTOMER = new ConcurrentHashMap<>();
	private List<String> KEYS = Collections.synchronizedList(new ArrayList<>());
	private static final long MIN = 1000000000000L; // smallest 13-digit number
	private static final long MAX = 9999999999999L; // largest 13-digit number

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private AccountRepository accountRepository;

	public Customer validateCustomer(String id) throws Exception {
		if (CACHED_CUSTOMER.containsKey(id)) {
			return CACHED_CUSTOMER.get(id);
		} else {
			Customer customer = customerRepository.findById(id)
					.orElseThrow(() -> new Exception("Customer doesn't exist"));
			if (KEYS.size() > 50) {
				CACHED_CUSTOMER.remove(KEYS.remove(0));
			}
			CACHED_CUSTOMER.put(id, customer);
			KEYS.add(id);
			return customer;
		}
	}

	public Account getAccount(String accountNumber) throws Exception {
		Account account = accountRepository.findByAccountNumber(accountNumber)
				.orElseThrow(() -> new Exception("Account doesn't exists with number " + accountNumber));
		if (!account.isActive())
			throw new Exception("Account is not ACTIVE");
		return account;
	}

	public String generateAccountNumber() {
		Random random = new Random();
		long randomStr = MIN + (long) (random.nextDouble() * (MAX - MIN + 1));
		return String.valueOf(randomStr);
	}

}
