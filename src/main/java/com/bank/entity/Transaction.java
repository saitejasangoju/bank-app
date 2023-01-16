package com.bank.entity;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@Document(collection = "transactions")
@Entity
@Table(name = "transaction")
public class Transaction {
	
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private Long id;
	private String customerId;
	private String accountNumber;
	@CreationTimestamp
	private Instant date;
	private double amount;
	private TransactionType type;
}
