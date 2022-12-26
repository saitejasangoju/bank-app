package com.bank.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account {
	private String accountNumber;
	private String accountType;
	private String ifscCode = "SBI2345";
	private double accountBalance;
	private boolean isActive = true;
	
}
