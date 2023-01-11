package com.bank.dto;

import com.bank.entity.Account;
import com.bank.entity.AccountType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoneyTransfer {
	private double amount;
	private String receiver;
}
