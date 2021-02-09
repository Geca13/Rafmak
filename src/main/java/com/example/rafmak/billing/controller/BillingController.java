package com.example.rafmak.billing.controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.example.rafmak.billing.service.BillingServices;
import com.example.rafmak.finance.repository.DailyFiscalReportRepository;
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
	BillProductsListRepository bplRepository;
	@Autowired
	UsersRepository userRepository;
	@Autowired
	ProductService productService;
	@Autowired
	BillRepository billRepository;
	@Autowired
	DailyFiscalReportRepository dailyRepository;
	
	@PostMapping("/createBill")
	public String createBill(@AuthenticationPrincipal UsersDetails userD) {
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
		BillProductsList bill = new BillProductsList();
		if(dailyRepository.existsByDate(LocalDate.now())) {
		      return "index?theDayIsClosed";
	   }
		
		bill.setUser(user);
		bill.setTime(LocalDate.now());
		bill.setPrinted(false);
		bill.setDailyBillCounter(services.dailyBillCounter());
		bplRepository.save(bill);
		     return "redirect:/bill/"+bill.getId();
	}
	
	@GetMapping("/bill/{bid}")
	public String findProducts(Model model,@PathVariable("bid")Integer bid,@Param(value = "id")String id) {
		
		model.addAttribute("product", productRepository.findById(id));
		if(productRepository.findById(id)==null) {
		    model.addAttribute("product", mpRepository.findById(id));
		}
		BillProductsList bill = bplRepository.findById(bid).get();
		model.addAttribute("bill", bill);
		
		List<BillingProducts> products = bill.getProducts();
		model.addAttribute("products", products);
		
		BillingProducts bprod1 = new BillingProducts();
		model.addAttribute("bprod1", bprod1);
		
		Double total = bill.getTotal();
		model.addAttribute("total", total);
		
		Double tax = bill.getTax();
		model.addAttribute("tax", tax);
		
		Integer counter = bill.getDailyBillCounter();
		model.addAttribute("counter", counter);
		
		     return "newBill";
	}
	
	@PostMapping("/createProduct/{bid}/{id}")
	public String addToList(BillingProducts bprod,@PathVariable("bid")Integer bid,@PathVariable(value = "id")String id,@Param(value = "priceType")String priceType) {
		BillProductsList list = bplRepository.findById(bid).get();
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
	          }
	          bpRepository.save(bprod1);
		      bprod1.setItemTotal(bprod1.getQty() * bprod1.getPrice()); 
		      bprod1.setItemTax(bprod1.getItemTotal() * 0.1525);
		          bpRepository.save(bprod1);
		}
		      list.getProducts().add(bprod1);
		    Double total = 0.00;
		    Double tax = 0.00;
		 
		 for (BillingProducts billingProducts : list.getProducts()) {
			total = total + billingProducts.getItemTotal();
			tax = tax +billingProducts.getItemTax();
		}
		 
		     list.setTotal(total);
		     list.setTax(tax);
		         bplRepository.save(list);
		           return "redirect:/bill/"+list.getId();
	}
	
	@PostMapping("/print/{id}")
	public String printBill(@PathVariable("id")Integer id) {
		Bill bill = new Bill();
		BillProductsList list = bplRepository.findById(id).get();
		  list.setTime(LocalDate.now());
		  list.setPrinted(true);
		  
		   bplRepository.save(list);
		   bill.setList(list);
		   bill.setCreated(LocalDate.now());
		   bill.setTax(list.getTax());
		   bill.setTotal(list.getTotal());
		   billRepository.save(bill);
		   for (BillingProducts billingProducts : list.getProducts()) {
				  
			    if(productRepository.existsById(billingProducts.getPid())) {
				 Product product = productRepository.findById(billingProducts.getPid());
			 product.setTotalQty(product.getTotalQty() - billingProducts.getQty());
			 
			 productRepository.save(product);
			 productService.newQtyToProduct(product, - billingProducts.getQty(), LocalDate.now(),product.getTotalQty(), "Sold on bill: " + bill.getId());
			 }
			    
			    if(mpRepository.existsById(billingProducts.getPid())) {
			    	MeasuredProduct product = mpRepository.findById(billingProducts.getPid());
			    	product.setTotalQty(product.getTotalQty()- billingProducts.getQty());
			    	product.setTotalWorth(product.getTotalWorth()-billingProducts.getItemTotal());
			    	mpRepository.save(product);
			    	productService.newQtyToMeasuredProduct(product, - billingProducts.getQty(), LocalDate.now(), product.getTotalQty(),"Sold on bill: " + bill.getId());
			    }
		}
		   
		    return "redirect:/";
	}
	
	@GetMapping("/removeProduct/{bid}/{id}")
	public String removeProductFromBill(@PathVariable("bid")Integer bid,@PathVariable(value = "id")String id) {
		BillProductsList list = bplRepository.findById(bid).get();
		BillingProducts product = bpRepository.findById(id).get();
		if(list.getPrinted() == true) {
			return "redirect:/bill/"+list.getId()+"?theBillIsPrinted"; 
		}
		   if(list.getProducts().contains(product)) {
			  list.getProducts().remove(product);
			  list.setTotal(list.getTotal()-product.getItemTotal());
			  list.setTax(list.getTax()-product.getItemTax());
			}
		  bplRepository.save(list);
		    return "redirect:/bill/"+list.getId();
	}
	
	@GetMapping("findBillByDailyCounter")
	public String findBillByDC(@Param(value = "id")Integer id,LocalDate date) {
		BillProductsList list = services.findBillByCounter(id, date);
		return "redirect:/bill/"+list.getId();
	}
	
	@GetMapping("/todaysBills")
	public String todaysBills(Model model) {
		List <Bill> list = services.todaysBills();
		model.addAttribute("bills", list);
		return "bills";
	}
	
	@GetMapping("/billsOnDate")
	public String getBillsOnDate(Model model,@Param (value = "d")String d) {
		List <Bill> list = services.billsOnDate(d);
		model.addAttribute("bills", list);
		return "bills";
	}
	
	@GetMapping("/billsOnPeriod")
	public String getBillsOnPeriod(Model model,@Param (value = "d1")String d1,@Param (value = "d2")String d2) {
		List <Bill> list = services.billsOnPeriod(d1,d2);
		model.addAttribute("bills", list);
		return "bills";
	}
	
}
