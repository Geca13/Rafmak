package com.example.rafmak.invoice.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.rafmak.billing.entity.BillingProducts;
import com.example.rafmak.billing.repository.BillingProductsRepository;
import com.example.rafmak.invoice.entity.Company;
import com.example.rafmak.invoice.entity.Invoice;
import com.example.rafmak.invoice.repository.CompanyRepository;
import com.example.rafmak.invoice.repository.InvoiceRepository;
import com.example.rafmak.invoice.service.InvoiceServices;
import com.example.rafmak.product.entity.MeasuredProduct;
import com.example.rafmak.product.entity.Product;
import com.example.rafmak.product.repository.MeasuredProductRepository;
import com.example.rafmak.product.repository.ProductRepository;
import com.example.rafmak.product.service.ProductService;
import com.example.rafmak.users.entity.Users;
import com.example.rafmak.users.repository.UsersRepository;
import com.example.rafmak.users.service.UsersDetails;

@Controller
public class InvoiceController {
	
	@Autowired
	InvoiceServices services;
	@Autowired
	UsersRepository userRepository;
	@Autowired
	InvoiceRepository invoiceRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	MeasuredProductRepository mpRepository;
	@Autowired 
	BillingProductsRepository bpRepository;
	@Autowired
	ProductService productService;
	@Autowired
	CompanyRepository companyRepository;
	
	@GetMapping("/createNewCustomer")
	public String addNewCustomerForm(Model model) {
		
		model.addAttribute("company", new Company());
		
		return "addNewCustomer";
	}
	
	@PostMapping("/createNewCustomer")
	public String processCustomerForm(@ModelAttribute("company")Company company) {
		
		services.saveNewCustomer(company);
		
		return "redirect:/";
	}
	
	@GetMapping("/companies")
	public String findCompanyForInvoice(Model model,@Param(value = "search") String search) {
		List<Company> companies = services.findCompany(search);
		services.calculateLateDateDebt();
		model.addAttribute("companies", companies);
		return "companyList";
	}
	
	@GetMapping("/allCompanies")
	public String allCompanies(Model model,@Param(value = "search") String search) {
		List<Company> companies = services.findCompany(search);
		services.calculateLateDateDebt();
		model.addAttribute("companies", companies);
		return "allCompanies";
	}
	
	@GetMapping("/createInvoice/{id}")
	public String createInvoice(@AuthenticationPrincipal UsersDetails userD,@PathVariable("id")Integer id) {
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
		List<Invoice> emptyInvoices = new ArrayList<>();
		List<Invoice> invoices = invoiceRepository.findAll();
		
		for (Invoice invoice : invoices) {
			if(invoice.getProducts().isEmpty()) {
				emptyInvoices.add(invoice);
			}
			
		}
		
		if(emptyInvoices.isEmpty()) {
		
        Company company = companyRepository.findById(id).get();
		Invoice invoice = new Invoice();
		invoice.setCompany(company);
		invoice.setUser(user);
		invoice.setTotal(0.00);
		invoice.setIssued(LocalDate.now());
		invoice.setArrival(LocalDate.now().plusDays(1));
		invoice.setPrinted(false);
		
		invoiceRepository.save(invoice);
		
		     return "redirect:/invoice/"+invoice.getId();
		}else {
			Invoice oldInvoice = invoiceRepository.findById(emptyInvoices.get(0).getId()).get();
			oldInvoice.setCompany(companyRepository.findById(id).get());
			oldInvoice.setUser(user);
			invoiceRepository.save(oldInvoice);
			return "redirect:/invoice/" + emptyInvoices.get(0).getId();
		}
    }
	
	@GetMapping("/invoice/{bid}")
	public String findInvoiceProducts(Model model,@PathVariable("bid")Integer bid,@Param(value = "id")String id,@Param(value = "search")String search) {
		
		model.addAttribute("product", productRepository.findById(id));
		if(productRepository.findById(id)==null) {
		    model.addAttribute("product", mpRepository.findById(id));
		}
		Invoice invoice = invoiceRepository.findById(bid).get();
		model.addAttribute("invoice", invoice);
		
		List<BillingProducts> products = invoice.getProducts();
		model.addAttribute("products", products);
		
		BillingProducts bprod1 = new BillingProducts();
		model.addAttribute("bprod1", bprod1);
		
		Double total = invoice.getTotal();
		model.addAttribute("total", total);
		
		Double tax = invoice.getTax();
		model.addAttribute("tax", tax);
		
		Company company = invoice.getCompany();
		model.addAttribute("company", company);
		
		
		 return "newInvoice";
	}
	
	@PostMapping("/createInvoiceProduct/{bid}/{id}")
	public String addToInvoiceList(BillingProducts bprod,@PathVariable("bid")Integer bid,@PathVariable(value = "id")String id,@Param(value = "priceType")String priceType) {
		Invoice invoice = invoiceRepository.findById(bid).get();
		 if(invoice.getPrinted()== true) {
			 invoice.getCompany().setHasTotalDebt(invoice.getCompany().getHasTotalDebt()-invoice.getTotal());
			 if(invoice.getArrival().isBefore(LocalDate.now())) {
			 invoice.getCompany().setDeptOverdue(invoice.getCompany().getDeptOverdue()-invoice.getTotal());
			 companyRepository.save(invoice.getCompany());
			 }
			 companyRepository.save(invoice.getCompany());
			 
			 for (BillingProducts billingProducts : invoice.getProducts()) {
				  
				    if(productRepository.existsById(billingProducts.getPid())) {
					 Product product = productRepository.findById(billingProducts.getPid());
				 product.setTotalQty(product.getTotalQty() + billingProducts.getQty());
				 
				 productRepository.save(product);
				 productService.newQtyToProduct(product, + billingProducts.getQty(), LocalDate.now(),product.getTotalQty(),"Returned from Invoce update or deletion for invoice id: " + invoice.getId()+" for company: "+ invoice.getCompany().getCompanyName());
				 }
				    
				    if(mpRepository.existsById(billingProducts.getPid())) {
				    	MeasuredProduct product = mpRepository.findById(billingProducts.getPid());
				    	product.setTotalQty(product.getTotalQty()+ billingProducts.getQty());
				    	product.setTotalWorth(product.getTotalWorth()+ billingProducts.getItemTotal());
				    	mpRepository.save(product);
				    	productService.newQtyToMeasuredProduct(product, + billingProducts.getQty(), LocalDate.now(), product.getTotalQty(),"Returned from Invoce update or deletion for invoice id: " + invoice.getId()+" for company: "+ invoice.getCompany().getCompanyName());
				    }
			}
			 
		 }
		List<BillingProducts> products = bpRepository.findAll();
		BillingProducts bprod1 = new BillingProducts();
		
		if(id.startsWith("*")) {
			
	        MeasuredProduct product = mpRepository.findById(id);
	        if(product.getDescription().equals("Akril")|| product.getDescription().equals("Base")) {
	        	  bprod1.setId(String.valueOf(products.size()+1));
			      bprod1.setPid(product.getId());
		          bprod1.setDescription(product.getDescription());
			      bprod1.setQty(bprod.getQty()); 
			      bprod1.setItemTotal(bprod.getItemTotal()); 
			      bprod1.setPrice(bprod.getItemTotal()/bprod.getQty());
			      bprod1.setItemTax(bprod1.getItemTotal() * 0.1525);
			          bpRepository.save(bprod1);
	        } else {
	          bprod1.setId(String.valueOf(products.size()+1));
		      bprod1.setPid(product.getId());
	          bprod1.setDescription(product.getDescription());
		      bprod1.setQty(bprod.getQty());
		      bprod1.setPrice(product.getPrice());
		      bprod1.setItemTotal(product.getPrice() * bprod.getQty()); 
		      bprod1.setItemTax(bprod1.getItemTotal() * 0.1525);
		          bpRepository.save(bprod1);
	        }
		}else {
			Product product = productRepository.findById(id);
			  bprod1.setId(String.valueOf(products.size()+1));
		      bprod1.setPid(product.getId());
	          bprod1.setDescription(product.getDescription());
	          bprod1.setQty(bprod.getQty());
	          if(priceType.isEmpty()  || priceType.equalsIgnoreCase("m")) {
	        	  bprod1.setPrice(product.getPrice());
	          }else if(priceType.equalsIgnoreCase("g")) {
	        	  bprod1.setPrice(product.getPriceOnPack());
	          }else if(priceType.equalsIgnoreCase("d")) {
	        	  bprod1.setPrice(product.getDiscPrice());
	          }
	          bpRepository.save(bprod1);
		      bprod1.setItemTotal(bprod1.getQty() * bprod1.getPrice()); 
		      bprod1.setItemTax(bprod1.getItemTotal() * 0.1525);
		          bpRepository.save(bprod1);
		}
		    invoice.getProducts().add(bprod1);
		    Double total = 0.00;
		    Double tax = 0.00;
		 
		 for (BillingProducts billingProducts : invoice.getProducts()) {
			total = total + billingProducts.getItemTotal();
			tax = tax +billingProducts.getItemTax();
		}
		 
		 invoice.setTotal(total);
		 invoice.setTax(tax);
		 invoiceRepository.save(invoice);
		 
		           return "redirect:/invoice/"+invoice.getId();
	}
	
	@PostMapping("/printInvoice/{id}")
	public String printInvoice(@PathVariable("id")Integer id,@ModelAttribute("invoice") Invoice invoice) {
		Invoice invoice1 = invoiceRepository.findById(id).get();
		
		  invoice1.setIssued(LocalDate.now());
		  invoice1.setShippingId(invoice.getId());
		  invoice1.setComment(invoice.getComment());
		  
		  for (BillingProducts billingProducts : invoice1.getProducts()) {
			  
			    if(productRepository.existsById(billingProducts.getPid())) {
				 Product product = productRepository.findById(billingProducts.getPid());
			 product.setTotalQty(product.getTotalQty() - billingProducts.getQty());
			 
			 productRepository.save(product);
			 productService.newQtyToProduct(product, - billingProducts.getQty(), LocalDate.now(),product.getTotalQty(), "Sold on Invoice id: " + invoice1.getId() + " for Company: "+ invoice1.getCompany().getCompanyName());
			 }
			    
			    if(mpRepository.existsById(billingProducts.getPid())) {
			    	MeasuredProduct product = mpRepository.findById(billingProducts.getPid());
			    	product.setTotalQty(product.getTotalQty()- billingProducts.getQty());
			    	product.setTotalWorth(product.getTotalWorth()-billingProducts.getItemTotal());
			    	mpRepository.save(product);
			    	productService.newQtyToMeasuredProduct(product, - billingProducts.getQty(), LocalDate.now(), product.getTotalQty(), "Sold on Invoice id: " + invoice1.getId() + " for Company: "+ invoice1.getCompany().getCompanyName());
			    }
		}
		  invoiceRepository.save(invoice1);
		  
		  Company company = invoice1.getCompany();
		  company.setHasTotalDebt(company.getHasTotalDebt()+invoice1.getTotal());
		  company.setTotalOnAllInvoices(company.getTotalOnAllInvoices()+invoice1.getTotal());
		  companyRepository.save(company);
		  invoice1.setPrinted(true);
		  invoiceRepository.save(invoice1);
			  
		    return "redirect:/";
		    }
	
	@GetMapping("/removeProductFromInvoice/{bid}/{id}")
	public String removeProductFromInvoice(@PathVariable("bid")Integer bid,@PathVariable(value = "id")String id) {
		Invoice invoice = invoiceRepository.findById(bid).get();
		BillingProducts product = bpRepository.findById(id).get();
		
		   if(invoice.getProducts().contains(product)) {
			   invoice.getProducts().remove(product);
			   invoice.setTotal(invoice.getTotal()-product.getItemTotal());
			   invoice.setTax(invoice.getTax()-product.getItemTax());
			}
		   invoiceRepository.save(invoice);
		    return "redirect:/invoice/"+invoice.getId();
	}
	
	@GetMapping("/findInvoiceById")
	public String findInvoice(@Param(value = "id")Integer id) {
		Invoice invoice = invoiceRepository.findById(id).get();
		return "redirect:/invoice/"+invoice.getId(); 
	}
	
	@PostMapping("/setDays/{bid}")
	public String setInvoiceDate (@PathVariable("bid")Integer bid,@Param("id")Integer id, Model model) {
		Invoice invoice = invoiceRepository.findById(bid).get();
		LocalDate date = services.invoiceDays(id);
		invoice.setIssued(LocalDate.now());
		invoice.setArrival(date);
		invoiceRepository.save(invoice);

		Long days = services.calculateDaysBetween();
		model.addAttribute("days", days);
		
		return "redirect:/invoice/"+invoice.getId();
	}
	
	@GetMapping("/todaysInvoices")
	public String todaysInvoices(Model model) {
		List <Invoice> list = services.todaysInvoices();
		model.addAttribute("invoices", list);
		return "invoices";
	}
	
	@GetMapping("/invoicesOnDate")
	public String getInvoicesOnDate(Model model,@Param (value = "d")String d) {
		List <Invoice> list = services.invoicesOnDate(d);
		model.addAttribute("invoices", list);
		return "invoices";
	}
	
	@GetMapping("/invoicesOnPeriod")
	public String getInvoicesOnPeriod(Model model,@Param (value = "d1")String d1,@Param (value = "d2")String d2) {
		List <Invoice> list = services.invoicesOnPeriod(d1,d2);
		model.addAttribute("invoices", list);
		return "invoices";
	}
	
	@GetMapping("/invoicesFromCompany/{id}")
	public String findInvoicesByCompany(Model model, @PathVariable("id")Integer id) {
		
		List<Invoice> invoices = services.invoicesByCompany(id);
		model.addAttribute("invoices", invoices);
		
		    return "invoices";
	}
	
	@GetMapping("/invoicesPage")
	   public String openInvoicesPage(Model model ) {
			
		return "invoicesPage";
	}
	
	@GetMapping("/deleteInvoice/{id}")
	public String deleteInvoice(@PathVariable("id")Integer id) {
		 services.deleteInvoice(id);
		return "redirect:/companies";
	}
	
	@GetMapping("/deleteCustomer/{id}")
	public String deleteCustomer(@PathVariable("id")Integer id) {
		 services.deleteCustomer(id);
		return "redirect:/allCompanies";
	}
	
}
