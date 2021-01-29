package com.example.rafmak.billing.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.weaver.NewConstructorTypeMunger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import com.example.rafmak.billing.entity.Bill;
import com.example.rafmak.billing.entity.BillingProducts;
import com.example.rafmak.billing.entity.Company;
import com.example.rafmak.billing.repository.BillRepository;
import com.example.rafmak.billing.repository.BillingProductsRepository;
import com.example.rafmak.billing.repository.CompanyRepository;
import com.example.rafmak.product.entity.MeasuredProduct;
import com.example.rafmak.product.entity.Product;
import com.example.rafmak.product.repository.MeasuredProductRepository;
import com.example.rafmak.product.repository.ProductRepository;
import com.example.rafmak.product.repository.QtyHistoryRepository;
import com.example.rafmak.users.entity.Users;
import com.example.rafmak.users.repository.UsersRepository;
import com.example.rafmak.users.service.UsersDetails;

@Service
public class BillingServices {
	
	@Autowired
	UsersRepository userRepository;
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	MeasuredProductRepository mpRepository;
	
	@Autowired
	BillRepository billRepository;
	
	@Autowired
	QtyHistoryRepository qhRepository;
	
	@Autowired
	BillingProductsRepository bpRepository;
	
	@Autowired
	CompanyRepository companyRepository;
	
	public Product findProduct(String id) {
		return productRepository.findById(id);
	}
	
	public MeasuredProduct findMeasuredProduct(String id) {
		return mpRepository.findById(id);
		
	}
	
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
	    
		return companyRepository.save(newCustomer);
	}
	
  /*  
	public BillingProducts createBillProduct(String id) {
	//	String xid = id.substring(1) ;
		BillingProducts product = new BillingProducts();
		if(id.startsWith("*")) {
		  MeasuredProduct mp = findMeasuredProduct(id);
			product.setPid(mp.getId());
			product.setDescription(mp.getDescription());
			product.setPrice(mp.getPrice());
			product.setQty(Double.parseDouble(qty));
			product.setItemTotal(mp.getPrice()*Double.parseDouble(qty));
			return bpRepository.save(product);
		}else {
		//  Integer pid = Integer.valueOf(xid);
		  Product p = findProduct(id);
			product.setPid(String.valueOf(p.getId()));
			product.setDescription(p.getDescription());
			product.setQty(Double.valueOf(qty));
			 if(price == null) {
			    product.setPrice(p.getRegularPrice());
			    product.setItemTotal(p.getRegularPrice() * Double.valueOf(qty));
			 }else if(price.equalsIgnoreCase("g")) {
				product.setPrice(p.getPriceOnPack());
				product.setItemTotal(p.getPriceOnPack() * Double.valueOf(qty));
			 }else if(price.equalsIgnoreCase("d")) {
				product.setPrice(p.getDiscPrice());
				product.setItemTotal(p.getDiscPrice() * Double.valueOf(qty));
				
			 }
		}
		
		return bpRepository.save(product);
		}
	
	public List<BillingProducts> billProducts(String id, String qty, String price){
		
		List<BillingProducts> products = new ArrayList<>();
		
		products.add(createBillProduct(id, qty, price));
		
		return products;
		
	}
	public Bill createBill(@AuthenticationPrincipal UsersDetails userD,String id, String qty, String price) {
	    
	     Bill bill = new Bill();
		 String userEmail = userD.getUsername();
         Users user = userRepository.findByEmail(userEmail);
		 List<BillingProducts> products = billProducts(id, qty, price);
		//   products.add(createBillProduct(id, qty, price));
		
		  bill.setUser(user);
		  
		  Double total = 0.00;
		  for (BillingProducts billingProducts : products) {
			total = total + billingProducts.getItemTotal();
			bill.setTotal(total);
			bill.setTax(bill.getTotal()*0.152);
		}
		  bill.setTime(LocalDateTime.now());
		  
		 return billRepository.save(bill);
	}
	
	
	*/
	
	
}
