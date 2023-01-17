package com.bank.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "transactions")
@Entity
@Table(name = "transaction")
public class Transaction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@org.springframework.data.annotation.Id
	private Long id;
	private String customerId;
	private String accountNumber;
	@CreationTimestamp
	@CreatedDate
	private Date date;
	private double amount;
	private TransactionType type;
}
