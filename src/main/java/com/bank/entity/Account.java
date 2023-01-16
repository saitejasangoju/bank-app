package com.bank.entity;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@Document(value="account")
@Entity
@Table(name = "account")
public class Account {
	
	@Id
	private String accountNumber;
	private String customerId;
	private AccountType type;
	private String ifscCode;
	private double accountBalance;
	@Default
	private boolean active = true;
 //	@CreatedDate
	@CreationTimestamp
	private Instant createdDate;
 //	@LastModifiedDate
	@UpdateTimestamp
 	private Instant updatedDate;
 	
}
