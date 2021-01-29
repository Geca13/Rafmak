package com.example.rafmak.billing.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import com.example.rafmak.billing.entity.Bill;
import com.example.rafmak.billing.entity.BillingProducts;
import com.example.rafmak.billing.entity.Company;
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
	@Autowired
	ProductService productService;
	
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
		if(productRepository.findById(id)==null) {
		    model.addAttribute("product", mpRepository.findById(id));
		}
		Bill bill = billRepository.findById(bid).get();
		model.addAttribute("bill", bill);
		
		List<BillingProducts> products = bill.getProducts();
		model.addAttribute("products", products);
		
		BillingProducts bprod1 = new BillingProducts();
		model.addAttribute("bprod1", bprod1);
		
		Double total = bill.getTotal();
		model.addAttribute("total", total);
		
		Double tax = bill.getTax();
		model.addAttribute("tax", tax);
		
		     return "newBill";
	}
	
	@PostMapping("/createProduct/{bid}/{id}")
	public String addToList(BillingProducts bprod,@PathVariable("bid")Integer bid,@PathVariable(value = "id")String id,@Param(value = "priceType")String priceType) {
		Bill bill = billRepository.findById(bid).get();
		List<BillingProducts> products = bpRepository.findAll();
		BillingProducts bprod1 = new BillingProducts();
		
		if(id.startsWith("*")) {
	        MeasuredProduct product = mpRepository.findById(id);
	          bprod1.setId(String.valueOf(products.size()+1));
		      bprod1.setPid(product.getId());
	          bprod1.setDescription(product.getDescription());
		      bprod1.setQty(bprod.getQty());
		      bprod1.setPrice(product.getPrice());
		      bprod1.setItemTotal(product.getPrice() * bprod.getQty()); 
		      bprod1.setItemTax(bprod1.getItemTotal() * 0.1525);
		          bpRepository.save(bprod1);
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
		      bill.getProducts().add(bprod1);
		    Double total = 0.00;
		    Double tax = 0.00;
		 
		 for (BillingProducts billingProducts : bill.getProducts()) {
			total = total + billingProducts.getItemTotal();
			tax = tax +billingProducts.getItemTax();
		}
		 
		     bill.setTotal(total);
		     bill.setTax(tax);
		         billRepository.save(bill);
		           return "redirect:/bill/"+bill.getId();
	}
	
	@PostMapping("/print/{id}")
	public String printBill(@PathVariable("id")Integer id) {
		Bill bill = billRepository.findById(id).get();
		  bill.setTime(LocalDateTime.now());
		  bill.setPrinted(true);
		  for (BillingProducts billingProducts : bill.getProducts()) {
			    if(productRepository.existsById(billingProducts.getPid())) {
				 Product product = productRepository.findById(billingProducts.getPid());
			 product.setTotalQty(product.getTotalQty() - billingProducts.getQty());
		      
			 productRepository.save(product);
			 productService.newQtyToProduct(product, - billingProducts.getQty(), LocalDate.now(),product.getTotalQty());
			 }
			    if(mpRepository.existsById(billingProducts.getPid())) {
			    	MeasuredProduct product = mpRepository.findById(billingProducts.getPid());
			    	product.setTotalQty(product.getTotalQty()- billingProducts.getQty());
			    	mpRepository.save(product);
			    	productService.newQtyToMeasuredProduct(product, - billingProducts.getQty(), LocalDate.now(), product.getTotalQty());
			    }
		}
		   billRepository.save(bill);
		    return "redirect:/";
	}
	
	@GetMapping("/removeProduct/{bid}/{id}")
	public String removeProductFromBill(@PathVariable("bid")Integer bid,@PathVariable(value = "id")String id) {
		Bill bill = billRepository.findById(bid).get();
		BillingProducts product = bpRepository.findById(id).get();
		if(bill.getPrinted() == true) {
			return "redirect:/bill/"+bill.getId()+"?theBillIsPrinted"; 
		}
		   if(bill.getProducts().contains(product)) {
			  bill.getProducts().remove(product);
			  bill.setTotal(bill.getTotal()-product.getItemTotal());
			  bill.setTax(bill.getTax()-product.getItemTax());
			}
		  billRepository.save(bill);
		    return "redirect:/bill/"+bill.getId();
	}
	
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
}
