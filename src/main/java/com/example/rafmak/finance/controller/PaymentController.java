package com.example.rafmak.finance.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
import com.example.rafmak.finance.entity.ReportProduct;
import com.example.rafmak.finance.repository.DailyFiscalReportRepository;
import com.example.rafmak.finance.repository.DailyMaterialReportRepository;
import com.example.rafmak.finance.repository.PaymentRepository;
import com.example.rafmak.finance.repository.ReportProductRepository;
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
	DailyFiscalReportRepository fisRepository;
	
	@Autowired
	DailyMaterialReportRepository matRepository;
	
	@Autowired
	ReportProductRepository reportRepository;
	
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
	public String createFisReport(Model model,@ModelAttribute("report1") DailyFiscalReport report1) {
		
		List<BillProductsList> list = bplRepository.findByPrintedAndCreated(false, LocalDate.now());
		for (BillProductsList billProductsList : list) {
			if(!billProductsList.getProducts().isEmpty()) {
				return "redirect:/billsPage?unpaidBillsError"; 
			}
		}
		   
		   
		   if(fisRepository.existsByDate(LocalDate.now())) {
			      return "redirect:/billsPage?dailyReportExist";
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
		
		fisRepository.save(report);
		
		 return "redirect:/billsPage";
	}
	
	@PostMapping("/createMaterialsReport")
	public String createMatReport(Model model,@ModelAttribute("report1") DailyMaterialReport report1) {
		
		DailyMaterialReport report = new DailyMaterialReport();
	
		Double total = 0.00;
		Double tax = 0.00;
		matRepository.save(report);
		List<BillProductsList> lists = bplRepository.findByPrintedAndCreated(true, LocalDate.now());
		for (BillProductsList billProductsList : lists) {
			
			total = total + billProductsList.getTotal();
			report.setTotal(total);
			tax = tax + billProductsList.getTax();
			report.setTax(tax); 
		}
		
		List<ReportProduct> products = new ArrayList<>();
		
			List<BillingProducts> prods = bpRepository.findByDate(LocalDate.now());
				for (BillingProducts product : prods) {
					if(reportRepository.existsByPidAndPriceAndDate(product.getPid(),product.getPrice(),LocalDate.now())) {
						ReportProduct rep = reportRepository.findByPidAndPriceAndDate(product.getPid(),product.getPrice(),LocalDate.now());
						rep.setQty(rep.getQty()+product.getQty());
						rep.setItemTax(rep.getItemTax()+product.getItemTax());
						rep.setItemTotal(rep.getItemTotal()+product.getItemTotal());
						reportRepository.save(rep);
					}else if(reportRepository.existsByPidAndDescriptionAndDate(product.getPid(),"Akril",LocalDate.now())) {
						ReportProduct rep = reportRepository.findByPidAndDescriptionAndDate(product.getPid(),"Akril",LocalDate.now());
						rep.setQty(rep.getQty()+product.getQty());
						rep.setItemTax(rep.getItemTax()+product.getItemTax());
						rep.setItemTotal(rep.getItemTotal()+product.getItemTotal());
						reportRepository.save(rep);
						rep.setPrice(rep.getItemTotal()/rep.getQty());
						reportRepository.save(rep);
					}else if(reportRepository.existsByPidAndDescriptionAndDate(product.getPid(),"Base",LocalDate.now())) {
						ReportProduct rep = reportRepository.findByPidAndDescriptionAndDate(product.getPid(),"Base",LocalDate.now());
						rep.setQty(rep.getQty()+product.getQty());
						rep.setItemTax(rep.getItemTax()+product.getItemTax());
						rep.setItemTotal(rep.getItemTotal()+product.getItemTotal());
						reportRepository.save(rep);
					}else {
					ReportProduct repProduct = new ReportProduct();
					repProduct.setPid(product.getPid());
					
					repProduct.setDescription(product.getDescription());
					repProduct.setPrice(product.getPrice());
					repProduct.setItemTax(product.getItemTax());
					repProduct.setItemTotal(product.getItemTotal());
					repProduct.setQty(product.getQty());
					repProduct.setDate(LocalDate.now());
					reportRepository.save(repProduct);
				 
					products.add(repProduct);
					
					}
				}
			
	
			report.setProducts(products);
			
			report.setDate(LocalDate.now());
			matRepository.save(report);
		
		
		return "redirect:/billsPage";
	}
	
	
		
}
