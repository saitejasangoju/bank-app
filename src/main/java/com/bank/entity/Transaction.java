package com.bank.entity;


import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transactions")
public class Transaction {
	@Id
	private String id;
	private String accountNumber;
	private String dateTime;
	private double amount;
	private double credit;
	private double debit;
}
