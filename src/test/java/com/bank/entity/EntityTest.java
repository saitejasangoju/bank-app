package com.bank.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.bank.dto.AccountDto;
import com.bank.dto.CreditDebit;
import com.bank.dto.CustomerDto;
import com.bank.dto.CustomerUpdateDto;
import com.bank.dto.MoneyTransfer;
import com.jparams.verifier.tostring.ToStringVerifier;

class EntityTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void customerTest() {
		ToStringVerifier.forClass(Customer.builder().getClass()).verify();
	}
	
	@Test
	void customerDtoTest() {
		ToStringVerifier.forClass(CustomerDto.builder().getClass()).verify();
	}
	
	@Test
	void customerUpdateTest() {
		ToStringVerifier.forClass(CustomerUpdateDto.builder().getClass()).verify();
	}
	
	@Test
	void accountTest() {
		ToStringVerifier.forClass(Account.builder().getClass()).verify();
	}
	

	@Test
	void accountDtoTest() {
		ToStringVerifier.forClass(AccountDto.builder().getClass()).verify();
	}
	
	@Test
	void addressTest() {
		ToStringVerifier.forClass(Address.builder().getClass()).verify();
	}
	
	@Test
	void moneyTransferTest() {
		ToStringVerifier.forClass(MoneyTransfer.builder().getClass()).verify();
	}
	
	@Test
	void creditDebitTest() {
		ToStringVerifier.forClass(CreditDebit.builder().getClass()).verify();
	}


}
