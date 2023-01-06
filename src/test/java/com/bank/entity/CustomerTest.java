package com.bank.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jparams.verifier.tostring.ToStringVerifier;

class CustomerTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void test() {
		ToStringVerifier.forClass(Customer.builder().getClass()).verify();
	}

}
