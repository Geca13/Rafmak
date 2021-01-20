package com.example.rafmak.billing.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.rafmak.billing.entity.Bill;
import com.example.rafmak.billing.entity.BillingProducts;
import com.example.rafmak.billing.service.BillingServices;
import com.example.rafmak.users.service.UsersDetails;

@Controller
public class BillingController {
	
	@Autowired
	BillingServices services;
	
	@GetMapping("/bill")
	public String showBill(Model model ,@AuthenticationPrincipal UsersDetails userD) {
		
		Bill bill = new Bill();
		List<BillingProducts> products = bill.getProducts();
		  model.addAttribute("bill", bill);
		  model.addAttribute("products", products);
		  model.addAttribute("user", userD);
		
		      return "newBill";
	}
	
	@PostMapping("/bill")
	public String createBill(@AuthenticationPrincipal UsersDetails userD) {
	
		services.createBill(userD);
		
		        return "redirect:/bill";
	}

}
