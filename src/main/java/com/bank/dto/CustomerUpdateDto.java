package com.bank.dto;

import com.bank.entity.Address;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerUpdateDto {
	private String email;
	private String phone;
	private Address address;
}
