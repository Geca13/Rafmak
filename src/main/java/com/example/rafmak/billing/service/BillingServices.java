package com.example.rafmak.billing.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import com.example.rafmak.billing.entity.Bill;
import com.example.rafmak.billing.entity.BillingProducts;
import com.example.rafmak.billing.repository.BillRepository;
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
	
	
	public Bill createBill(@AuthenticationPrincipal UsersDetails userD) {
		
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
		List<BillingProducts> products = new ArrayList<>();
		Bill bill = new Bill();
		  bill.setUser(user);
		  bill.setProducts(products);
		  bill.setTime(LocalDateTime.now());
		  bill.setTax(0.00);
		  bill.setTotal(0.00);
		
		     return billRepository.save(bill);
	}
	
	

}
