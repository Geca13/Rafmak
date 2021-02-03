package com.example.rafmak.invoice.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.rafmak.invoice.entity.Company;
import com.example.rafmak.invoice.entity.Invoice;
import com.example.rafmak.invoice.repository.CompanyRepository;
import com.example.rafmak.invoice.repository.InvoiceRepository;

@Service
public class InvoiceServices {
	
	@Autowired
	CompanyRepository companyRepository;
	
	@Autowired
	InvoiceRepository invoiceRepository;
	
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
	
	public List<Company> findCompany(String search) {
		
		if(search != null) {
			List<Company> companies = companyRepository.findBySearch(search);
			return companies;
		}
		return companyRepository.findAll();
	}
	
	public LocalDate invoiceDays(Integer id) {
		LocalDate today = LocalDate.now();
		LocalDate arrivalDate = today.plusDays(id);
		return arrivalDate;
	}
	
	public void calculateDept() {
		
	List<Invoice> invoices = invoiceRepository.findAll();
	
	for (Invoice invoice : invoices) {
		
		Company company = invoice.getCompany();
		
		company.setHasTotalDebt(company.getHasTotalDebt()+invoice.getTotal());
		
		companyRepository.save(company);
		
	}
}
	
	public void calculateArivedDebt() {
		
		List<Invoice> invoices = invoiceRepository.findAll();
		for (Invoice invoice : invoices) {
			
			Company company = invoice.getCompany();
			
			if(LocalDate.now().isAfter(invoice.getArrival())) {
				company.setDeptOverdue(company.getDeptOverdue() +  invoice.getTotal());
				
				companyRepository.save(company);
			}
			
		}
	
		
	   }
	
   }
	
   


