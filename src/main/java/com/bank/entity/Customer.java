package com.bank.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="customer")
public class Customer {
	@Id
	private String id;
	private String name;
	private String dob;
	private long phone;
	private String email;
	private String aadhar;
	private Account account;
	private Address address;
	
	
}
