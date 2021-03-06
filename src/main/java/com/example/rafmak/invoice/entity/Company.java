package com.example.rafmak.invoice.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Company {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String companyName;
	
	private String accountNumber;
	
	private String phoneNumber;
	
	private String email;
	
	private String contactPerson;
	
	private String streetAddress;
	
	private String zipCode;
	
	private String city;
	
	private Double hasTotalDebt;
	
	private Double deptOverdue;
	
	private Double totalOnAllInvoices;
	
	private Double totalPayed;
	
	@OneToMany
	private List<ExpiredDateInvoices> expiredInvoices;
	
		

}
