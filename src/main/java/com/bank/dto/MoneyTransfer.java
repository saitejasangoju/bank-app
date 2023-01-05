package com.bank.dto;

import lombok.Data;

@Data
public class MoneyTransfer {
	private double amount;
	private String receiver;
}
