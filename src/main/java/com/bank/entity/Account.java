package com.bank.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

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
@Document(value="account")
@Entity
@Table(name = "account")
public class Account {
	
	@Id
	@org.springframework.data.annotation.Id
	private String accountNumber;
	private String customerId;
	private AccountType type;
	private String ifscCode;
	private double accountBalance;
	@Default
	private boolean active = true;
 	@CreatedDate
	@CreationTimestamp
	private Date createdDate;
 	@LastModifiedDate
	@UpdateTimestamp
 	private Date updatedDate;
 	
}
