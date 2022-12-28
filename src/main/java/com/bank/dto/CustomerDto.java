package com.bank.dto;

import com.bank.entity.Address;

import lombok.Data;

@Data
public class CustomerDto {
	private String customerId;
	private String name;
	private String dob;
	private String phone;
	private String email;
	private String aadhar;
	private Address address;
}
