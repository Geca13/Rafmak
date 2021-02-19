package com.example.rafmak.invoice.service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.rafmak.billing.entity.BillingProducts;
import com.example.rafmak.billing.repository.BillingProductsRepository;
import com.example.rafmak.finance.entity.Payment;
import com.example.rafmak.finance.repository.PaymentRepository;
import com.example.rafmak.invoice.controller.emptyInvoiceException;
import com.example.rafmak.invoice.entity.Company;
import com.example.rafmak.invoice.entity.ExpiredDateInvoices;
import com.example.rafmak.invoice.entity.Invoice;
import com.example.rafmak.invoice.entity.InvoicePdfExporter;
import com.example.rafmak.invoice.repository.CompanyRepository;
import com.example.rafmak.invoice.repository.ExpiredDateInvoicesRepository;
import com.example.rafmak.invoice.repository.InvoiceRepository;
import com.example.rafmak.product.entity.MeasuredProduct;
import com.example.rafmak.product.entity.Product;
import com.example.rafmak.product.repository.MeasuredProductRepository;
import com.example.rafmak.product.repository.ProductRepository;
import com.example.rafmak.product.service.ProductService;
import com.lowagie.text.DocumentException;

@Service
@Transactional
public class InvoiceServices {
	
	@Autowired
	CompanyRepository companyRepository;
	
	@Autowired
	InvoiceRepository invoiceRepository;
	
	@Autowired
	ExpiredDateInvoicesRepository ediRepository;
	
	@Autowired
	BillingProductsRepository bpRepository;
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	MeasuredProductRepository mpRepository;
	
	@Autowired
	PaymentRepository paymentRepository;
	
	@Autowired
	ProductService productService;
	
	public Company saveNewCustomer(Company company) {
		Company newCustomer = new Company();
		
		newCustomer.setCompanyName(company.getCompanyName());
		newCustomer.setAccountNumber(company.getAccountNumber());
		newCustomer.setPhoneNumber(company.getPhoneNumber());
		newCustomer.setEmail(company.getEmail());
		newCustomer.setContactPerson(company.getContactPerson());
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
    	invoices.sort(Comparator.comparing(Invoice::getId).reversed());
    		return invoices;
		}
    
    public List<Invoice> invoices(){
    	
    	List<Invoice> invoices = invoiceRepository.findAll();
    	invoices.sort(Comparator.comparing(Invoice::getId).reversed());
    	
    		return invoices;
		}
    
    public Invoice deleteInvoice(Integer id) {
    	Invoice invoice = invoiceRepository.findById(id).get();
    	for (BillingProducts products : invoice.getProducts()) {
    		if(productRepository.existsById(products.getPid())) {
				 Product product = productRepository.findById(products.getPid());
			 product.setTotalQty(product.getTotalQty() + products.getQty());
			 
			 productRepository.save(product);
			 productService.newQtyToProduct(product, + products.getQty(), LocalDate.now(),product.getTotalQty(), "Return from Invoice id: " + invoice.getId() + " from Company: "+ invoice.getCompany().getCompanyName());
			 }
			    
			    if(mpRepository.existsById(products.getPid())) {
			    	MeasuredProduct product = mpRepository.findById(products.getPid());
			    	product.setTotalQty(product.getTotalQty() + products.getQty());
			    	product.setTotalWorth(product.getTotalWorth()+ products.getItemTotal());
			    	mpRepository.save(product);
			    	productService.newQtyToMeasuredProduct(product, + products.getQty(), LocalDate.now(), product.getTotalQty(), "Return from Invoice id: " + invoice.getId() + " from Company: "+ invoice.getCompany().getCompanyName());
			    }
    	}
    	invoice.setCompany(null);
    	invoice.setTax(0.00);
    	invoice.setTotal(0.00);
    	invoice.setUser(null);
    	invoice.setPrinted(false);
    	
    	
    	
		for (BillingProducts products : invoice.getProducts()) {    
    		products.setDate(null);
			products.setDescription(null);
			products.setItemTax(null);
			products.setItemTotal(null);
			products.setPid(null);
			products.setPrice(null);
			products.setQty(null);
			bpRepository.save(products);
			    }
    	
    	invoice.setProducts(null);
		return invoiceRepository.save(invoice);
    }
    
    public void deleteCustomer(Integer id) {
    	
    	Company company = companyRepository.findById(id).get();
    	company.setExpiredInvoices(null);
    	companyRepository.save(company);
    	List<Invoice> invoices = invoiceRepository.findByCompany(company);
    	for (Invoice invoice : invoices) {
			invoice.setProducts(null);
			invoice.setUser(null);
			invoice.setCompany(null);
			invoiceRepository.save(invoice);
			invoiceRepository.delete(invoice);
		}
    	List<ExpiredDateInvoices> expiredInvoices = ediRepository.findByCompany(company);
    	for (ExpiredDateInvoices invoice : expiredInvoices) {
			invoice.setCompany(null);
			invoice.setUser(null);
			
			ediRepository.save(invoice);
			ediRepository.delete(invoice);
		}
    	List<Payment> payments = paymentRepository.findByCompany(company);
    	for (Payment payment : payments) {
    		payment.setCompany(null);
    		paymentRepository.save(payment);
    		paymentRepository.delete(payment);
		}
    	companyRepository.save(company);
    	companyRepository.delete(company);
    		
    }
    
    public void updateCustomer(Integer id,Company company) {
    	
    	Company company1 = companyRepository.findById(id).get();
    	
    	company1.setCompanyName(company.getCompanyName());
    	company1.setAccountNumber(company.getAccountNumber());
    	company1.setPhoneNumber(company.getPhoneNumber());
    	company1.setEmail(company.getEmail());
    	company1.setContactPerson(company.getContactPerson());
    	company1.setStreetAddress(company.getStreetAddress());
    	company1.setZipCode(company.getZipCode());
    	company1.setCity(company.getCity());
    	
    	companyRepository.save(company1);
    }
    
    
  
    
    public void exportToPdf(HttpServletResponse response, Integer id) throws DocumentException, IOException {
    	
    	response.setContentType("aplication/pdf");
    	
    	DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
    	String headerKey = "Content - Disposition";
    	String headerValue = "attachment; filename=invoice_ "+currentDateTime + " .pdf";
    	
    	response.setHeader(headerKey, headerValue);
    	
    	Invoice invoice = invoiceRepository.findById(id).get();
    	
    	InvoicePdfExporter exporter = new InvoicePdfExporter(invoice);
    	
    	exporter.export(response);
    }
    
}
