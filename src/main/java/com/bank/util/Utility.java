package com.bank.util;

import java.util.NoSuchElementException;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bank.entity.Customer;
import com.bank.repository.CustomerRepository;

@Component
public class Utility {
	
	@Autowired 
	private CustomerRepository customerRepo;

	// checking for valid customer
	public void validateCustomer(String id) {
		Customer customer = customerRepo.findById(id).orElse(null);
		if (customer == null)
			throw new NoSuchElementException("Customer doesn't exist");
	}

	// generating random number
	public String generateAccountNumber() {
		Random random = new Random();
		String randomStr = "";
		for (int i = 1; i <= 13; i++) {
			if (i == 1) {
				int num = random.nextInt(9);
				if (num == 0)
					randomStr += num + 1;
			} else {
				int num = random.nextInt(9);
				randomStr += Integer.toString(num);
			}
		}
		return randomStr;
	}

}
