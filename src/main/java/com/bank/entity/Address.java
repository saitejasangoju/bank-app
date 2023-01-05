package com.bank.entity;

import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address {
	private String houseNumber;
	private String city;
	private String state;
	@NonNull
	@Size(max = 6, min = 6, message = "invalid pincode")
	private String pincode;
}
