package com.bank.dto;

import lombok.Data;

@Data
public class AccountDto {

	private String customerId;
	private String accountType;
	private String ifscCode;
	private double accountBalance;
}
