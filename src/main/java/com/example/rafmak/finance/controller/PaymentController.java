package com.example.rafmak.finance.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.rafmak.finance.entity.Payment;
import com.example.rafmak.finance.repository.PaymentRepository;
import com.example.rafmak.invoice.entity.Company;
import com.example.rafmak.invoice.repository.CompanyRepository;

@Controller
public class PaymentController {
	

	@Autowired
	PaymentRepository paymentRepository;
	
	@Autowired
	CompanyRepository companyRepository;
	
	@GetMapping("/paymentFrom/{id}")
	public String paymentsForms(Model model , @PathVariable("id")Integer id) {
		
		model.addAttribute("payment", new Payment());
		Company company = companyRepository.findById(id).get();
		model.addAttribute("company", company);
		
		return "addNewPayment";
		
	}
	
	@PostMapping("/paymentFrom/{id}")
	public String companyMadePayment(@PathVariable("id")Integer id, Payment payment) {
		Payment newPayment = new Payment();
		Company company = companyRepository.findById(id).get();
		newPayment.setCompany(company);
		newPayment.setDate(LocalDate.now());
		newPayment.setSum(payment.getSum());
		paymentRepository.save(newPayment);
		company.setHasTotalDebt(company.getHasTotalDebt()-payment.getSum());
		company.setDeptOverdue(company.getDeptOverdue()-payment.getSum());
		if(company.getDeptOverdue() < payment.getSum()) {
			company.setDeptOverdue(0.00);
		}
		companyRepository.save(company);
		
		return "redirect:/";
		
	}
	
	
	
}
