package com.example.rafmak.finance.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.rafmak.billing.entity.Bill;
import com.example.rafmak.billing.entity.BillProductsList;
import com.example.rafmak.billing.entity.BillingProducts;
import com.example.rafmak.billing.repository.BillProductsListRepository;
import com.example.rafmak.billing.repository.BillRepository;
import com.example.rafmak.billing.repository.BillingProductsRepository;
import com.example.rafmak.finance.entity.DailyFiscalReport;
import com.example.rafmak.finance.entity.DailyMaterialReport;
import com.example.rafmak.finance.entity.Payment;
import com.example.rafmak.finance.repository.DailyFiscalReportRepository;
import com.example.rafmak.finance.repository.PaymentRepository;
import com.example.rafmak.invoice.entity.Company;
import com.example.rafmak.invoice.repository.CompanyRepository;

@Controller
public class PaymentController {
	

	@Autowired
	PaymentRepository paymentRepository;
	
	@Autowired
	CompanyRepository companyRepository;
	
	@Autowired
	BillRepository billRepository;
	
	@Autowired
	BillProductsListRepository bplRepository;
	
	@Autowired
	BillingProductsRepository bpRepository;
	
	@Autowired
	DailyFiscalReportRepository dailyRepository;
	
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
		company.setTotalPayed(company.getTotalPayed()+payment.getSum());
		companyRepository.save(company);
		company.setHasTotalDebt(company.getTotalOnAllInvoices()-company.getTotalPayed());
		companyRepository.save(company);
		return "redirect:/";
		
	}
	
	@PostMapping("/createFiscalReport")
	public String createFisReport(Model model) {
		
		List<BillProductsList> list = bplRepository.findByPrintedAndTime(false, LocalDate.now());
		   if(!list.isEmpty()) {
			   return "index?unpaidBillsError"; 
		   }
		   
		   if(dailyRepository.existsByDate(LocalDate.now())) {
			      return "index?dailyReportExist";
		   }
		DailyFiscalReport report = new DailyFiscalReport();
		Double total = 0.00;
		Double tax = 0.00;
		List<Bill> bills = billRepository.findByCreated(LocalDate.now());
		for (Bill bill : bills) {
			total = total +bill.getTotal();
			tax = tax + bill.getTax();
		}
		report.setDate(LocalDate.now());
		report.setTax(tax);
		report.setTotal(total);
		report.setTotalDayBills(bills.size());
		
		dailyRepository.save(report);
		
		 return "redirect:/";
	}
	
	@PostMapping("/createMaterialsReport")
	public String createMatReport(Model model) {
		
		DailyMaterialReport report = new DailyMaterialReport();
		Double total = 0.00;
		Double tax = 0.00;
		List<BillProductsList> lists = bplRepository.findByPrintedAndTime(true, LocalDate.now());
		for (BillProductsList billProductsList : lists) {
			for (BillingProducts product : billProductsList.getProducts()) {
				report.getProducts().add(product);
				Double qty = 0.00;
				if(report.getProducts().contains(product)) {
					qty = qty + product.getQty();
					product.setQty(qty);
				}
				
			}
		}
		
		return "redirect:/";
	}
	
	
		
}
