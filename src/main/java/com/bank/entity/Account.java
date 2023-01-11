package com.bank.entity;

import java.time.Instant;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.Valid;

import org.springframework.data.annotation.CreatedDate;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.springframework.data.annotation.LastModifiedDate;

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
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String aid;
	private String customerId;
	private String accountNumber;
	@Enumerated(EnumType.STRING)
	@Valid
	private AccountType type;
	private String ifscCode;
	private double accountBalance;
	@Default
	private boolean active = true;
 	@CreatedDate
	private Instant createdDate;
 	@LastModifiedDate
 	private Instant updatedDate;
 	
}
