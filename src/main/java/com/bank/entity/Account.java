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
@Document(value="account")
public class Account {
	@Id
	private String aid;
	private String customerId;
	private String accountNumber;
	private String accountType;
	private String ifscCode;
	private double accountBalance;
	private boolean isActive = true;
	
}
