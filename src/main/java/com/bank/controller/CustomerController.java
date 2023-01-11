package com.bank.controller;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.dto.CustomerDto;
import com.bank.dto.CustomerUpdateDto;
import com.bank.entity.Customer;
import com.bank.service.CustomerService;

import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
	
	@Autowired
	private CustomerService service;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@ApiIgnore
	@GetMapping("/home")
	public void home(HttpServletResponse response) throws IOException {
		response.sendRedirect("/swagger-ui.html");
	}
	
	@GetMapping("/{customerId}")
	public ResponseEntity<Customer> getById(@PathVariable String customerId) {
		return new ResponseEntity<>(service.getById(customerId), HttpStatus.ACCEPTED);
	}
	
	
	@GetMapping
	public ResponseEntity<List<Customer>> list() {
		return ResponseEntity.ok(service.list());
	}

	@PostMapping
	public ResponseEntity<Customer> create(@RequestBody @Valid CustomerDto customer) throws Exception {
		return new ResponseEntity<>(service.create(modelMapper.map(customer, Customer.class)), HttpStatus.CREATED);
	}
	
	@PutMapping("/{customerId}")
	public ResponseEntity<Customer> update(@PathVariable String customerId, @RequestBody CustomerUpdateDto customer) {
		return new ResponseEntity<>(service.update(customerId, customer), HttpStatus.ACCEPTED);
	}
	 
	@DeleteMapping("/{customerId}")
	public ResponseEntity<Customer> delete(@PathVariable String customerId) {
		return new ResponseEntity<>(service.delete(customerId), HttpStatus.OK);
	}
	
	@GetMapping("/{customerId}/aadhar/{aadhar}/name/{name}")
	public Customer getByCustomerIdAndAadharAndName(@PathVariable String customerId, @PathVariable String aadhar, @PathVariable String name) throws NoSuchElementException {
		return service.getByCustomerIdAndAadharAndName(customerId, aadhar, name);
	}
	
	@GetMapping("/{customerId}/aadhar/{aadhar}")
	public Customer getByCustomerIdOrAadhar(@PathVariable String customerId, @PathVariable String aadhar) throws NoSuchElementException {
		return service.getByCustomerIdOrAadhar(customerId, aadhar);
	}

}