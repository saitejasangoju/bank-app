package com.bank.exception;

@SuppressWarnings("serial")
public class NotActiveException extends Exception{

	public NotActiveException(String message) {
		super(message);
	}
}
