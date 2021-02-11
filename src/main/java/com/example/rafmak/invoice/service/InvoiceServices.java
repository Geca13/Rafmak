package com.example.rafmak.invoice.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.rafmak.billing.entity.Bill;
import com.example.rafmak.invoice.entity.Company;
import com.example.rafmak.invoice.entity.ExpiredDateInvoices;
import com.example.rafmak.invoice.entity.Invoice;
import com.example.rafmak.invoice.repository.CompanyRepository;
import com.example.rafmak.invoice.repository.ExpiredDateInvoicesRepository;
import com.example.rafmak.invoice.repository.InvoiceRepository;

@Service
public class InvoiceServices {
	
	@Autowired
	CompanyRepository companyRepository;
	
	@Autowired
	InvoiceRepository invoiceRepository;
	
	@Autowired
	ExpiredDateInvoicesRepository ediRepository;
	
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
	    newCustomer.setTotalPayed(0.00);
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
	
	public void findExpiredInvoices() {
		List<Invoice> invoices = invoiceRepository.findAll();
		for (Invoice invoice : invoices) {
			
			if(invoice.getArrival().isBefore(LocalDate.now())) {
				if(ediRepository.existsById(invoice.getId())) {
					ExpiredDateInvoices edi = ediRepository.findById(invoice.getId()).get();
					
					edi.setTotal(invoice.getTotal());
					edi.setCompany(invoice.getCompany());
					edi.setUser(invoice.getUser());
					ediRepository.save(edi);
					
					Company company = invoice.getCompany(); 
					companyRepository.save(company);
				} else {
						
					ExpiredDateInvoices edi = new ExpiredDateInvoices();
					edi.setId(invoice.getId());
					edi.setTotal(invoice.getTotal());
					edi.setCompany(invoice.getCompany());
					edi.setUser(invoice.getUser());
					ediRepository.save(edi);
					
					Company company = invoice.getCompany();
					company.getExpiredInvoices().add(edi);
					companyRepository.save(company);
				}
			}
		}
	}
	
	
	public void calculateLateDateDebt() {
		List<Company>companies = companyRepository.findAll();
		
		 for (Company company : companies) {
		  Double sum = 0.00;
		  Double dept = 0.00;
		   List<ExpiredDateInvoices> invoices = ediRepository.findByCompany(company);
			for (ExpiredDateInvoices invoice : invoices) {
			  sum = sum + invoice.getTotal();
			  if(sum > company.getTotalPayed()) {
				  dept = sum-company.getTotalPayed();
				  company.setDeptOverdue(dept);
				 } else {
					 dept = 0.00;
					 company.setDeptOverdue(dept);
				 }
					companyRepository.save(company);
			}
		} 
	}
	
	public Long calculateDaysBetween() {
		
		Long days = 0L;
	List<Invoice> invoices = invoiceRepository.findAll();
	for (Invoice invoice : invoices) {
			days = invoice.getIssued().until(invoice.getArrival(), ChronoUnit.DAYS);
	}
	return days;
	}
	
      public List<Invoice> todaysInvoices(){
		
		List<Invoice> invoices = invoiceRepository.findByIssued(LocalDate.now());
		
		return invoices;
		
	}
	
    public List<Invoice> invoicesOnDate(String date){
		
	LocalDate d = LocalDate.parse(date);
    	
		List<Invoice> invoices = invoiceRepository.findByIssued(d);
		
		return invoices;
	}
    
    public List<Invoice> invoicesOnPeriod(String startDate,String endDate){
		
    	LocalDate d1 = LocalDate.parse(startDate);
    	LocalDate d2 = LocalDate.parse(endDate);
        	
    		List<Invoice> invoices = invoiceRepository.findByIssuedBetween(d1,d2);
    		
    		return invoices;
    	}
    
    public List<Invoice> invoicesByCompany(Integer id){
    	Company company = companyRepository.findById(id).get();
    	List<Invoice> invoices = invoiceRepository.findByCompanyId(company.getId());
    		
    		return invoices;
		}
	}


