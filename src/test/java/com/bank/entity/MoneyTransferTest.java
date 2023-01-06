package com.bank.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.bank.dto.MoneyTransfer;
import com.jparams.verifier.tostring.ToStringVerifier;

class MoneyTransferTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void test() {
		ToStringVerifier.forClass(MoneyTransfer.builder().getClass()).verify();
	}

}
