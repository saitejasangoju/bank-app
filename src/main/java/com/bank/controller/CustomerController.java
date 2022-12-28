package com.bank.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bank.dto.CustomerUpdateDto;
import com.bank.entity.Customer;
import com.bank.service.CustomerService;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
	@Autowired
	private CustomerService service;
	
	@ApiIgnore
	@GetMapping("/home")
	public void home(HttpServletResponse response) throws IOException {
		response.sendRedirect("/swagger-ui.html");
	}
	
	@GetMapping
	public List<Customer> list() {
		return service.list();
	}

	@PostMapping
	public Customer create(@RequestBody Customer customer) throws Exception {
		return service.create(customer);
	}
	
	@PutMapping("/{customerId}")
	public Customer update(@PathVariable String customerId, @RequestBody CustomerUpdateDto customer) {
		return service.update(customerId, customer);
	}
	 
	@DeleteMapping("/{customerId}")
	public Customer delete(@PathVariable String customerId) {
		return service.delete(customerId);
	}
}