package com.bank.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.bank.dto.CustomerUpdateDto;
import com.jparams.verifier.tostring.ToStringVerifier;

class CustomerUpdateDtoTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void test() {
		ToStringVerifier.forClass(CustomerUpdateDto.builder().getClass()).verify();
	}

}
