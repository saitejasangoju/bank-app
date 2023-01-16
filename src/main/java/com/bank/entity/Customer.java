package com.bank.entity;

import java.time.Instant;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

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
	@CreationTimestamp
	private Instant createdDate;
	@Version
	@JsonIgnore
	@Transient
	private Integer version;
	@UpdateTimestamp
	private Instant updatedDate;
	@Valid
	@OneToOne(cascade = CascadeType.ALL)
	private Address address;

}
