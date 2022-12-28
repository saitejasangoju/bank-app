package com.bank.dto;

import com.bank.entity.Address;

import lombok.Data;

@Data
public class CustomerUpdateDto {
	private String email;
	private String phone;
	private Address address;
}
