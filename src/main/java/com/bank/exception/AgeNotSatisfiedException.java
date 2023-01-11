package com.bank.exception;

@SuppressWarnings("serial")
public class AgeNotSatisfiedException extends Exception{
	public AgeNotSatisfiedException(String message) {
		super(message);
	}
}
