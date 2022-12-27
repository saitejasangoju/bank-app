package com.bank.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="customer")
public class Customer {
	@Id
	private String customerId;
	private String name;
	private String dob;
	private String phone;
	private String email;
	private String aadhar;
	private Address address;
}
