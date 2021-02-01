package com.example.rafmak.invoice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.rafmak.invoice.entity.Company;
import com.example.rafmak.invoice.repository.CompanyRepository;

@Service
public class InvoiceServices {
	
	@Autowired
	CompanyRepository companyRepository;
	
	public Company saveNewCustomer(Company company) {
		Company newCustomer = new Company();
		
		newCustomer.setCompanyName(company.getCompanyName());
		newCustomer.setAccountNumber(company.getAccountNumber());
		newCustomer.setPhoneNumber(company.getPhoneNumber());
		newCustomer.setEmail(company.getEmail());
		newCustomer.setEmail(company.getEmail());
		newCustomer.setStreetAddress(company.getStreetAddress());
	    newCustomer.setZipCode(company.getZipCode());
	    newCustomer.setCity(company.getCity());
	    newCustomer.setDeptOverdue(0.00);
	    newCustomer.setHasTotalDebt(0.00);
	    newCustomer.setTotalOnAllInvoices(0.00);
	    
		return companyRepository.save(newCustomer);
	}

}
