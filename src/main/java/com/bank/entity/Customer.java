package com.bank.entity;

import java.sql.Date;
import java.time.Instant;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@Document(collection="customer")
@Entity
@Table(name = "customer")
public class Customer {
	
	@Id
	private String customerId;
	private String name;
	private String dob;
	private String phone;
	private String email;
	private String aadhar;
	@CreatedDate
	private Date createdDate;
	@Version
	@JsonIgnore
	private Integer version;
	@LastModifiedDate
	private Date updatedDate;
	
	@OneToOne(cascade = CascadeType.ALL)
	private Address address;

}
