package com.bank.exception;

@SuppressWarnings("serial")
public class CustomerNotMatchAccount extends Exception {

	public CustomerNotMatchAccount(String message) {
		super(message);
	}
}
