package com.bank.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.bank.dto.AccountDto;
import com.jparams.verifier.tostring.ToStringVerifier;

class AccountDtoTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void test() {
		ToStringVerifier.forClass(AccountDto.builder().getClass()).verify();
	}

}
