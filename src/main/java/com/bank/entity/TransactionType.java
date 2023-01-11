package com.bank.entity;

public enum TransactionType {
	
	DEPOSIT("DEPOSIT"), WITHDRAW("WITHDRAW");
	
	private String type;
	private TransactionType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
}
