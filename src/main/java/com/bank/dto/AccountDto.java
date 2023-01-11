package com.bank.dto;

import com.bank.entity.AccountType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDto {

	private String customerId;
	private AccountType type;
	private String ifscCode;
	private double accountBalance;
}
