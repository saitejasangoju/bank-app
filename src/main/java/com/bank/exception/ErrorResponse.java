package com.bank.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {

	private String message;
	public ErrorResponse(String ex) {
		this.message = ex;
	}
}
