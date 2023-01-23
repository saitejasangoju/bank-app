package com.bank.entity;

public enum AccountType {

	SAVINGS("SAVINGS"), SALARY("SALARY"), CURRENT("CURRENT");
	
	private String type;
	private AccountType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
}
