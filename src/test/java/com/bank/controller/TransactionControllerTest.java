package com.bank.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.bank.dto.CreditDebit;
import com.bank.dto.MoneyTransfer;
import com.bank.entity.Account;
import com.bank.entity.AccountType;
import com.bank.entity.Address;
import com.bank.entity.Customer;
import com.bank.entity.Transaction;
import com.bank.entity.TransactionType;
import com.bank.repository.AccountRepository;
import com.bank.repository.CustomerRepository;
import com.bank.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private CustomerRepository customerRepository;

	@MockBean
	private AccountRepository accountRepository;

	@MockBean
	private TransactionRepository transactionRepository;

	Address address = Address.builder().houseNumber("34-9").city("hyd").state("ts").pincode("987789").build();
	Customer customer1 = Customer.builder().customerId("731163625713").name("teja").dob("2002-11-05")
			.phone("9283773654").email("teja@gmail.com").aadhar("987678098076").address(address).build();
	Customer customer3 = Customer.builder().customerId("831163625713").name("tejasango").dob("2002-10-10")
			.phone("9283773654").email("teja@gmail.com").aadhar("987678098076").address(address).build();
	Customer customer2 = Customer.builder().customerId("731163625714").name("sai").dob("2002-10-20").phone("9883773654")
			.email("sai@gmail.com").aadhar("777678098076").address(address).build();
	Account account1 = Account.builder().aid("63aae0328a1acb3d34d568f5").customerId("731163625713")
			.accountNumber("3714762657302").type(AccountType.SAVING).ifscCode("SBI21315").accountBalance(25000.0)
			.active(true).build();
	Account account2 = Account.builder().aid("63aae0328a1acb3d34d568f9").customerId("731163625713")
			.accountNumber("8714762657302").type(AccountType.SAVING).ifscCode("SBI21315").accountBalance(25000.0)
			.active(true).build();
	Account account3 = Account.builder().aid("63aae0328a1acb3d34d679f6").customerId("831163625713")
			.accountNumber("5414762657301").type(AccountType.SALARY).ifscCode("SBI21315").accountBalance(35000.0)
			.active(false).build();
	Instant date1 = Instant.parse("2022-12-04T17:21:18.139Z");
	Instant date2 = Instant.parse("2022-12-03T17:21:18.139Z");
	Transaction transaction1 = Transaction.builder().id("63ac7b0ec00a170f750646b8").customerId("731163625713")
			.accountNumber("3714762657302").date(date1).amount(2500.0).type(TransactionType.DEPOSIT).build();
	Transaction transaction2 = Transaction.builder().id("63ac7b0ec00a170f750646b9").customerId("731163625713")
			.accountNumber("3714762657302").date(date2).amount(2700.0).type(TransactionType.DEPOSIT).build();

	@Test
	void transfer() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		Mockito.when(accountRepository.findByAccountNumber("8714762657302")).thenReturn(account2);
		MoneyTransfer dto = MoneyTransfer.builder().amount(3500.0).receiver("8714762657302").build();
		String content = objectMapper.writeValueAsString(dto);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/731163625713/accounts/3714762657302/transactions/transfer").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
	
	@Test
	void getRecentTest() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/customers/731163625713/accounts/3714762657302/transactions/recent")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void getByIdTest() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		Mockito.when(transactionRepository.findById("63ac7b0ec00a170f750646b8")).thenReturn(Optional.of(transaction1));
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/v1/customers/731163625713/accounts/3714762657302/transactions/63ac7b0ec00a170f750646b8")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.amount", is(2500.0)));
	}

	// invlalid account number
	@Test
	void invalidGetByIdTest() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		Mockito.when(transactionRepository.findById("63ac7b0ec00a170f750646b8")).thenReturn(Optional.of(transaction1));
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/v1/customers/731163625713/accounts/8714762657302/transactions/63ac7b0ec00a170f750646b8")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	// customer not match account
	@Test
	void invalidCustomerIdTest() throws Exception {
		Mockito.when(customerRepository.findById("831163625713")).thenReturn(Optional.of(customer3));
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		Mockito.when(transactionRepository.findById("63ac7b0ec00a170f750646b8")).thenReturn(Optional.of(transaction1));
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/v1/customers/831163625713/accounts/3714762657302/transactions/63ac7b0ec00a170f750646b8")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	@Test
	void invalidAccountNumberListTest() throws Exception {
		Mockito.when(customerRepository.findById("831163625713")).thenReturn(Optional.of(customer3));
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/831163625713/accounts/3714762657302/transactions")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	// invalid account
	@Test
	void invalidAccountGetRecentTest() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/customers/731163625713/accounts/8714762657302/transactions/recent")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	// customer not match account
	@Test
	void invalidCustomerIdGetRecentTest() throws Exception {
		Mockito.when(customerRepository.findById("831163625713")).thenReturn(Optional.of(customer3));
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/customers/831163625713/accounts/3714762657302/transactions/recent")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	void depositTest() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		CreditDebit dto = CreditDebit.builder().amount(2500).build();
		String content = objectMapper.writeValueAsString(dto);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/731163625713/accounts/3714762657302/transactions/deposit").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.amount", is(2500.0)));
	}

	// customer not match account
	@Test
	void invalidCustomerIdDepositTest() throws Exception {
		Customer customer4 = Customer.builder().customerId("221163625713").name("teja").dob("2002-11-05")
				.phone("9283773654").email("teja@gmail.com").aadhar("987678098076").address(address).build();
		Customer customer = Customer.builder().customerId("731163625713").name("teja").dob("2002-11-25")
				.phone("9283773654").email("teja@gmail.com").aadhar("987678098076").address(address).build();
		Account account = Account.builder().aid("63aae0328a1acb3d34d568f5").customerId("731163625713")
				.accountNumber("3714762657302").type(AccountType.SAVING).ifscCode("SBI21315").accountBalance(2500.0)
				.active(true).build();
		Mockito.when(customerRepository.findById("221163625713")).thenReturn(Optional.of(customer4));
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account);
		CreditDebit dto = CreditDebit.builder().amount(2500).build();
		String content = objectMapper.writeValueAsString(dto);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/v1/customers/221163625713/accounts/3714762657302/transactions/deposit").content(content)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(mockRequest).andExpect(status().isBadRequest());
	}

	// account is null
	@Test
	void invalidAccountNumberDepositTest() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("8814762657302")).thenReturn(null);
		CreditDebit dto = CreditDebit.builder().amount(2500).build();
		String content = objectMapper.writeValueAsString(dto);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/v1/customers/731163625713/accounts/8814762657302/transactions/deposit").content(content)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(mockRequest).andExpect(status().isBadRequest());
	}

	// in active account
	@Test
	void inActiveAccountDepositTest() throws Exception {
		Mockito.when(customerRepository.findById("831163625713")).thenReturn(Optional.of(customer3));
		Mockito.when(accountRepository.findByAccountNumber("5414762657301")).thenReturn(account3);
		CreditDebit dto = CreditDebit.builder().amount(2500).build();
		String content = objectMapper.writeValueAsString(dto);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/v1/customers/831163625713/accounts/5414762657301/transactions/deposit").content(content)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(mockRequest).andExpect(status().isBadRequest());
	}

	@Test
	void withdrawalTest() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		CreditDebit dto = CreditDebit.builder().amount(2500).build();
		String content = objectMapper.writeValueAsString(dto);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/731163625713/accounts/3714762657302/transactions/deposit").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.amount", is(2500.0)));
	}

	// customer not match account
	@Test
	void invalidCustomerIdWithdrawalTest() throws Exception {
		Customer customer = Customer.builder().customerId("731163625713").name("teja").dob("2002-11-05")
				.phone("9283773654").email("teja@gmail.com").aadhar("987678098076").address(address).build();
		Account account = Account.builder().aid("63aae0328a1acb3d34d568f5").customerId("731163625713")
				.accountNumber("3714762657302").type(AccountType.SAVING).ifscCode("SBI21315").accountBalance(2500.0)
				.active(true).build();
		Mockito.when(customerRepository.findById("831163625713")).thenReturn(Optional.of(customer3));
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account);
		CreditDebit dto = CreditDebit.builder().amount(2500).build();
		String content = objectMapper.writeValueAsString(dto);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/831163625713/accounts/3714762657302/transactions/withdrawal").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	Account account = new Account("63aae0328a1acb3d34d568f5", "731163625713", "3714762657302", AccountType.SAVING,
			"SBI21315", 2500.0, true);

	// account is null
	@Test
	void invalidAccountNumberWithdrawalTest() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("8814762657302")).thenReturn(null);
		CreditDebit dto = CreditDebit.builder().amount(2500).build();
		String content = objectMapper.writeValueAsString(dto);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/731163625713/accounts/8814762657302/transactions/withdrawal").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	// in active account
	@Test
	void inActiveAccountWithdrawalTest() throws Exception {
		Mockito.when(customerRepository.findById("831163625713")).thenReturn(Optional.of(customer3));
		Mockito.when(accountRepository.findByAccountNumber("5414762657301")).thenReturn(account3);
		CreditDebit dto = CreditDebit.builder().amount(2500).build();
		String content = objectMapper.writeValueAsString(dto);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/831163625713/accounts/5414762657301/transactions/withdrawal").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	// insufficient balance
	@Test
	void insuffucientBalanceWithdrawalTest() throws Exception {
		Account account5 = Account.builder().aid("63aae0328a1acb3d34d568f5").customerId("731163625713")
				.accountNumber("4414762657302").type(AccountType.SAVING).ifscCode("SBI21315").accountBalance(0)
				.active(true).build();
		Mockito.when(customerRepository.findById("831163625713")).thenReturn(Optional.of(customer3));
		Mockito.when(accountRepository.findByAccountNumber("4414762657302")).thenReturn(account5);
		CreditDebit dto = CreditDebit.builder().amount(2500).build();
		String content = objectMapper.writeValueAsString(dto);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/831163625713/accounts/4414762657302/transactions/withdrawal").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}


	// sender account is not active
	@Test
	void invactiveSenderAccountTransfer() throws Exception {
		Mockito.when(customerRepository.findById("831163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("5414762657301")).thenReturn(account3);
		Mockito.when(accountRepository.findByAccountNumber("9814762657301")).thenReturn(account2);
		MoneyTransfer dto = MoneyTransfer.builder().amount(3500.0).receiver("9814762657301").build();
		String content = objectMapper.writeValueAsString(dto);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/831163625713/accounts/5414762657301/transactions/transfer").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	// sender account is not active
	@Test
	void invactiveReceiverAccountTransfer() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		Mockito.when(accountRepository.findByAccountNumber("5414762657301")).thenReturn(account3);
		MoneyTransfer dto = MoneyTransfer.builder().amount(3500.0).receiver("5414762657301").build();
		String content = objectMapper.writeValueAsString(dto);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/731163625713/accounts/3714762657302/transactions/transfer").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	void listTest() throws Exception {
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account1);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/731163625713/accounts/3714762657302/transactions")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	// wrong transaction id
	@Test
	void InvalidId() throws Exception {
		Customer customer = Customer.builder().customerId("731163625713").name("teja").dob("2002-11-05")
				.phone("9283773654").email("teja@gmail.com").aadhar("987678098076").address(address).build();
		Account account = Account.builder().aid("63aae0328a1acb3d34d568f5").customerId("731163625713")
				.accountNumber("4414762657302").type(AccountType.SAVING).ifscCode("SBI21315").accountBalance(2500.0)
				.active(true).build();
		Instant date = Instant.parse("2022-12-28T17:21:18.139Z");
		Transaction transaction = Transaction.builder().id("63ac7b0ec00a170f750646b9").customerId("731163625713")
				.accountNumber("3714762657302").date(date).amount(2700.0).type(TransactionType.DEPOSIT).build();
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account);
		Mockito.when(transactionRepository.findById("63ac7b0ec00a170f750646b9")).thenReturn(Optional.of(transaction));
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/v1/customers/731163625713/accounts/3714762657302/transactions/13ac7b0ec00a170f750646b9")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	// wrong acount number
	@Test
	void listWithInvalidAccountNumber() throws Exception {
		Customer customer = Customer.builder().customerId("731163625713").name("teja").dob("2002-11-05")
				.phone("9283773654").email("teja@gmail.com").aadhar("987678098076").address(address).build();
		Account account = Account.builder().aid("63aae0328a1acb3d34d568f5").customerId("731163625713")
				.accountNumber("3714762657302").type(AccountType.SAVING).ifscCode("SBI21315").accountBalance(2500.0)
				.active(true).build();
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/731163625713/accounts/9714762657302/transactions")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	// depositing amount 0
	@Test
	void invalidAmountForDeposit() throws Exception {
		Customer customer = Customer.builder().customerId("731163625713").name("teja").dob("2002-11-05")
				.phone("9283773654").email("teja@gmail.com").aadhar("987678098076").address(address).build();
		Account account = Account.builder().aid("63aae0328a1acb3d34d568f5").customerId("731163625713")
				.accountNumber("3714762657302").type(AccountType.SAVING).ifscCode("SBI21315").accountBalance(2500.0)
				.active(true).build();
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account);
		CreditDebit credit = CreditDebit.builder().amount(0).build();
		String content = objectMapper.writeValueAsString(credit);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/731163625713/accounts/3714762657302/transactions/deposit").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

	}

	// withdrawing amount 0
	@Test
	void invalidAmountForWithDrawal() throws Exception {
		Customer customer = Customer.builder().customerId("731163625713").name("teja").dob("2002-11-05")
				.phone("9283773654").email("teja@gmail.com").aadhar("987678098076").address(address).build();
		Account account = Account.builder().aid("63aae0328a1acb3d34d568f5").customerId("731163625713")
				.accountNumber("3714762657302").type(AccountType.SAVING).ifscCode("SBI21315").accountBalance(2500.0)
				.active(true).build();
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account);
		CreditDebit debit = CreditDebit.builder().amount(0).build();
		String content = objectMapper.writeValueAsString(debit);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/731163625713/accounts/3714762657302/transactions/withdrawal").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

	}

	// transferring amount 0
	@Test
	void invalidAmountForTransfer() throws Exception {
		Customer customer = Customer.builder().customerId("731163625713").name("teja").dob("2002-11-05")
				.phone("9283773654").email("teja@gmail.com").aadhar("987678098076").address(address).build();
		Account account = Account.builder().aid("63aae0328a1acb3d34d568f5").customerId("731163625713")
				.accountNumber("3714762657302").type(AccountType.SAVING).ifscCode("SBI21315").accountBalance(2500.0)
				.active(true).build();
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account);
		MoneyTransfer transfer = MoneyTransfer.builder().amount(0).receiver("9814762657301").build();
		String content = objectMapper.writeValueAsString(transfer);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/customers/731163625713/accounts/3714762657302/transactions/transfer").content(content)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

	}

	@Test
	void deleteTest() throws Exception {
		Customer customer = Customer.builder().customerId("731163625713").name("teja").dob("2002-11-05")
				.phone("9283773654").email("teja@gmail.com").aadhar("987678098076").address(address).build();
		Account account = Account.builder().aid("63aae0328a1acb3d34d568f5").customerId("731163625713")
				.accountNumber("3714762657302").type(AccountType.SAVING).ifscCode("SBI21315").accountBalance(2500.0)
				.active(true).build();
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account);
		mockMvc.perform(MockMvcRequestBuilders
				.delete("/api/v1/customers/731163625713/accounts/3714762657302/transactions/delete")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	// account is null
	@Test
	void invalidAccountDeleteTest() throws Exception {
		Customer customer = Customer.builder().customerId("731163625713").name("teja").dob("2002-11-05")
				.phone("9283773654").email("teja@gmail.com").aadhar("987678098076").address(address).build();
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(null);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.delete("/api/v1/customers/731163625713/accounts/3714762657302/transactions/delete");
		mockMvc.perform(mockRequest).andExpect(status().isBadRequest());
	}

	// invalid customer id
	@Test
	void invalidCustomerIdDeleteTest() throws Exception {
		Customer customer = Customer.builder().customerId("731163625713").name("teja").dob("2002-11-05")
				.phone("9283773654").email("teja@gmail.com").aadhar("987678098076").address(address).build();
		Account account = Account.builder().aid("63aae0328a1acb3d34d568f5").customerId("731163625713")
				.accountNumber("3714762657302").type(AccountType.SAVING).ifscCode("SBI21315").accountBalance(2500.0)
				.active(true).build();
		Mockito.when(customerRepository.findById("831163625713")).thenReturn(Optional.of(customer1));
		Mockito.when(customerRepository.findById("731163625713")).thenReturn(Optional.of(customer));
		Mockito.when(accountRepository.findByAccountNumber("3714762657302")).thenReturn(account);
		mockMvc.perform(MockMvcRequestBuilders
				.delete("/api/v1/customers/831163625713/accounts/3714762657302/transactions/delete")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}
}
