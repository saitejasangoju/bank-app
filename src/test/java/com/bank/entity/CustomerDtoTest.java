package com.bank.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.bank.dto.CustomerDto;
import com.jparams.verifier.tostring.ToStringVerifier;

class CustomerDtoTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void test() {
		ToStringVerifier.forClass(CustomerDto.builder().getClass()).verify();
	}

}
