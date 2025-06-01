package com.bank.exception;

@SuppressWarnings("serial")
public class MethodArgumentNotValidException extends Exception{
	public MethodArgumentNotValidException(String message) {
		super(message);
	}
}
