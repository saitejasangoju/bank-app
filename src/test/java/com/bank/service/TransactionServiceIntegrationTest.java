package com.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.bank.BankApplication;
import com.bank.dto.CreditDebit;
import com.bank.dto.MoneyTransfer;
import com.bank.entity.Transaction;
import com.bank.repository.TransactionRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = BankApplication.class)
@TestPropertySource(locations = "classpath:application.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionServiceIntegrationTest {

	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private TransactionRepository transactionRepository;

	private String cid = "713414165780";
	private String aid = "465686742353";

	@Test
	@Order(1)
	void testDeposit() throws Exception {
		CreditDebit deposit = CreditDebit.builder().amount(2500.0).build();
		Transaction transaction = transactionService.deposit(cid, aid, deposit);
		assertEquals(2500.0, transaction.getAmount());		
	}
	
	@Test
	@Order(2)
	void testWithdarawal() throws Exception {
		CreditDebit withdraw = CreditDebit.builder().amount(1400.0).build();
		Transaction transaction = transactionService.withdrawal(cid, aid, withdraw);
		assertEquals(1400.0, transaction.getAmount());		
	}
	
	@Test
	@Order(3)
	void testMoneyTransfer() throws Exception {
		MoneyTransfer transfer = MoneyTransfer.builder().amount(1200.0).receiver("338111880756").build();
		List<Transaction> transactions = transactionService.moneyTransfer(cid, aid, transfer);
		assertEquals(2, transactions.size());		
	}
	
	@Test
	@Order(4)
	void testGetRecentTransactions() throws Exception {
		List<Transaction> transactions = transactionService.getRecentTransactions(cid, aid);
		assertEquals(3, transactions.size());		
	}
	
	@Test
	@Order(5)
	void testGetById() throws Exception {
		List<Transaction> list = transactionRepository.findAll();
		Transaction t = list.get(list.size() - 1);
		String tid = t.getId();
		Transaction transactions = transactionService.getById(cid, aid, tid);
		assertEquals(1200.0, transactions.getAmount());		
	}
	
	@Test
	@Order(6)
	void testListTransactions() throws Exception {
		List<Transaction> list = transactionRepository.findAll();
		Transaction t = list.get(list.size() - 1);
		String tid = t.getId();
		Transaction transactions = transactionService.getById(cid, aid, tid);
		assertEquals(1200.0, transactions.getAmount());		
	}
	
	@Test
	@Order(7)
	void testAccount1DeleteTransactions() throws Exception {
		String deletedMessage = transactionService.deleteByAccountNumber(cid, aid);
		assertEquals("Deleted Successfully", deletedMessage);		
	}
	

	@Test
	@Order(8)
	void testAccount2DeleteTransactions() throws Exception {
		String deletedMessage = transactionService.deleteByAccountNumber("561778824517", "338111880756");
		assertEquals("Deleted Successfully", deletedMessage);		
	}
	
	
	
	
	
	

}
