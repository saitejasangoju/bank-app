package com.bank.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.bank.entity.Address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDto {
	@NonNull
	@Size(min = 4, max = 16, message = "Invalid Name")
	private String name;
	@NonNull
	@Size(min = 10,max = 10, message = "Invalid Phone Number")
	private String phone;
	private String dob;
	@NotBlank
	@Email
	private String email;
	@Size(min = 12, message = "Invalid Aadhar Number")
	private String aadhar;
	@Valid
	private Address address;
}
