package com.example.rafmak.billing.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.rafmak.billing.entity.Bill;
import com.example.rafmak.billing.entity.BillingProducts;
import com.example.rafmak.billing.repository.BillRepository;
import com.example.rafmak.billing.repository.BillingProductsRepository;
import com.example.rafmak.billing.service.BillingServices;
import com.example.rafmak.product.entity.MeasuredProduct;
import com.example.rafmak.product.entity.Product;
import com.example.rafmak.product.repository.MeasuredProductRepository;
import com.example.rafmak.product.repository.ProductRepository;
import com.example.rafmak.product.service.ProductService;
import com.example.rafmak.users.entity.Users;
import com.example.rafmak.users.repository.UsersRepository;
import com.example.rafmak.users.service.UsersDetails;

@Controller
public class BillingController {
	
	@Autowired
	BillingServices services;
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	MeasuredProductRepository mpRepository;
	
	@Autowired 
	BillingProductsRepository bpRepository;
	
	@Autowired
	BillRepository billRepository;
	@Autowired
	UsersRepository userRepository;
	
	@PostMapping("/createBill")
	public String createBill(@AuthenticationPrincipal UsersDetails userD) {
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
		Bill bill = new Bill();
		bill.setUser(user);
		bill.setPrinted(false);
		billRepository.save(bill);
		
		return "redirect:/bill/"+bill.getId();
		
	}
	
	@GetMapping("/bill/{bid}")
	public String findProducts(Model model,@PathVariable("bid")Integer bid,@Param(value = "id")String id) {
		
		model.addAttribute("product", productRepository.findById(id));
	//	model.addAttribute("product", mpRepository.findById(id));
		
		Bill bill = billRepository.findById(bid).get();
		
		model.addAttribute("bill", bill);
		
		List<BillingProducts> products = bill.getProducts();
		model.addAttribute("products", products);
		
		BillingProducts bprod1 = new BillingProducts();
		model.addAttribute("bprod1", bprod1);
		
		return "newBill";
		
	}
	
	
	@PostMapping("/createProduct/{bid}/{id}")
	public String addToList(BillingProducts bprod,@PathVariable("bid")Integer bid,@PathVariable(value = "id")String id) {
		Bill bill = billRepository.findById(bid).get();
		BillingProducts bprod1 = new BillingProducts();
		
		
	        Product product = productRepository.findById(id);
		      bprod1.setPid(product.getId());
	          bprod1.setDescription(product.getDescription());
		      bprod1.setQty(bprod.getQty());
		      bprod1.setPrice(bprod.getPrice());
		      bprod1.setItemTotal(bprod.getPrice() * bprod.getPrice()); 
		   bpRepository.save(bprod1);
	//	}
		 bill.getProducts().add(bprod1);
		   billRepository.save(bill);
		           return "redirect:/bill/"+bill.getId();
		
	}
	
}
