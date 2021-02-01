package com.example.rafmak.billing.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.weaver.NewConstructorTypeMunger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import com.example.rafmak.billing.entity.Bill;
import com.example.rafmak.billing.entity.BillingProducts;
import com.example.rafmak.billing.repository.BillRepository;
import com.example.rafmak.billing.repository.BillingProductsRepository;
import com.example.rafmak.invoice.entity.Company;
import com.example.rafmak.invoice.repository.CompanyRepository;
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
	
	
	
	public Integer dailyBillCounter() {
		List<Bill> bills = billRepository.findAll();
		if(bills.isEmpty()) {
			Bill bill = new Bill();
			bill.setDailyBillCounter(1);
			return bill.getDailyBillCounter();
		}
		LocalDate today = LocalDate.now();
		
		Bill lastBill = billRepository.findById(bills.size()).get();
		Bill bill = new Bill();
		bill.setTime(today);
		if (!today.equals(lastBill.getTime())) {
			bill.setDailyBillCounter(1);
		}else {
			bill.setDailyBillCounter(lastBill.getDailyBillCounter()+1);
		}
		
		return bill.getDailyBillCounter();
		
	}
	
	public Bill findBillByCounter(Integer id , LocalDate date) {
		LocalDate d = LocalDate.now();
		Bill bill = billRepository.findByDailyBillCounterAndTime(id, d);
		
		   return bill;
	}
	
  
	
	
}
