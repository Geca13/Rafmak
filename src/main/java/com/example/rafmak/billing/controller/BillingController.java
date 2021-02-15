package com.example.rafmak.billing.controller;

import java.time.LocalDate;
import java.util.ArrayList;
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
import com.example.rafmak.invoice.entity.Invoice;
import com.example.rafmak.product.entity.MeasuredProduct;
import com.example.rafmak.product.entity.Product;
import com.example.rafmak.product.repository.MeasuredProductRepository;
import com.example.rafmak.product.repository.ProductRepository;
import com.example.rafmak.product.service.ProductService;
import com.example.rafmak.users.entity.Users;
import com.example.rafmak.users.repository.UsersRepository;
import com.example.rafmak.users.service.UsersDetails;
import com.example.rafmak.users.service.UsersServiceImpl;

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
	@Autowired
	UsersServiceImpl usersServiceImpl;
	
	
	
	@PostMapping("/createBill")
	public String createBill(@AuthenticationPrincipal UsersDetails userD) {
		
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
       
        List<BillProductsList> todaysList = new ArrayList<>();
        List<BillProductsList> lists = bplRepository.findByCreated(LocalDate.now());
        for (BillProductsList list : lists) {
			if(list.getProducts().isEmpty()) {
				todaysList.add(list);
			}
		}
        
        if(todaysList.isEmpty()) {
        
		BillProductsList bill = new BillProductsList();
		if(dailyRepository.existsByDate(LocalDate.now())) {
		      return "index?theDayIsClosed";
	   }
		
		bill.setUser(user);
		bill.setCreated(LocalDate.now());
		bill.setPrinted(false);
		bill.setTax(0.00);
		bill.setTotal(0.00);
		bill.setDailyBillCounter(services.dailyBillCounter());
		bplRepository.save(bill);
		     return "redirect:/bill/"+bill.getId();
	}else {
		BillProductsList bill = bplRepository.findById(todaysList.get(0).getId()).get();
		bill.setUser(user);
		bplRepository.save(bill);
		 return "redirect:/bill/"+todaysList.get(0).getId();
	}
       
	}
	@GetMapping("/bill/{bid}")
	public String findProducts(Model model,@PathVariable("bid")Integer bid,@Param(value = "id")String id) {
		BillProductsList bill = bplRepository.findById(bid).get();
		model.addAttribute("bill", bill);
		
		model.addAttribute("product", productRepository.findById(id));
		if(productRepository.findById(id)==null) {
		    model.addAttribute("product", mpRepository.findById(id));
		    	
		}
		
		
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
			      if(bprod.getQty() == null) {
			    	  return "redirect:/bill/"+list.getId()+"?qtyError"; 
	                     }
			      if(bprod.getQty() > product.getTotalQty()) {
		        	  return "redirect:/bill/"+list.getId()+"?invalidQty";
		          }
			      bprod1.setItemTotal(bprod.getItemTotal()); 
			      if(bprod.getItemTotal() == null) {
			    	  return "redirect:/bill/"+list.getId()+"?priceError"; 
	                     }
			      bprod1.setPrice(bprod.getItemTotal()/bprod.getQty());
			      bprod1.setItemTax(bprod1.getItemTotal() * 0.1525);
			          bpRepository.save(bprod1);
			          
	        } else {
	          bprod1.setId(String.valueOf(products.size()+1));
		      bprod1.setPid(product.getId());
	          bprod1.setDescription(product.getDescription());
		      bprod1.setQty(bprod.getQty());
		      if(bprod.getQty() == null) {
		    	  return "redirect:/bill/"+list.getId()+"?qtyError"; 
                     }
		      if(bprod.getQty() > product.getTotalQty()) {
	        	  return "redirect:/bill/"+list.getId()+"?invalidQty";
	          }
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
	          if(bprod.getQty() == null) {
        	         bprod1.setQty(1.00);
                  }
	          if(bprod.getQty() > product.getTotalQty()) {
	        	  return "redirect:/bill/"+list.getId()+"?invalidQty";
	          }
	          if(priceType.isEmpty() || priceType.equalsIgnoreCase("m")) {
	        	  bprod1.setPrice(product.getPrice());
	          }else if(priceType.equalsIgnoreCase("g")) {
	        	  bprod1.setPrice(product.getPriceOnPack());
	          }else {
	        	  return "redirect:/bill/"+list.getId()+"?invalidPriceType";
	          }
	          
	          bpRepository.save(bprod1);
	          
		      bprod1.setItemTotal(bprod1.getQty() * bprod1.getPrice()); 
		      bprod1.setItemTax(bprod1.getItemTotal() * 0.1525);
		          bpRepository.save(bprod1);
		          
			}
		
		      list.getProducts().add(bprod1);
		      if(bprod1.getId()== null) {
		    	  return "redirect:/bill/"+list.getId()+"?invalidId";
		      }
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
		if(list.getProducts().isEmpty()) {
			return "redirect:/bill/"+list.getId()+"?noProductsInList";
		}
		  list.setCreated(LocalDate.now());
		  list.setPrinted(true);
		  
		   bplRepository.save(list);
		   bill.setList(list);
		   bill.setCreated(LocalDate.now());
		   bill.setTax(list.getTax());
		   bill.setTotal(list.getTotal());
		   bill.setUser(list.getUser());
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
	
	@GetMapping("/todaysPayedBillsByUser")
	public String todaysPayedBillsByLoggedInUser(Model model,@AuthenticationPrincipal UsersDetails userD) {
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
		List<Bill> list = usersServiceImpl.payedBills(user);
		model.addAttribute("bills", list);
		
		return "bills";
	}
	
	@GetMapping("/todaysUnPayedBillsByUser")
	public String todaysUnpayedBillsByLoggedInUser(Model model,@AuthenticationPrincipal UsersDetails userD) {
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
		List<BillProductsList> list = usersServiceImpl.unPayedBills(user);
		model.addAttribute("bills", list);
		
		return "billingLists";
	}
	
	@GetMapping("/todaysInvoicesByUser")
	public String todaysInvoicesByLoggedInUser(Model model,@AuthenticationPrincipal UsersDetails userD) {
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
		List<Invoice> list = usersServiceImpl.getTodaysInvoicesByUser(user);
		model.addAttribute("invoices", list);
		return "invoices";
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
	
	@GetMapping("/deleteList/{id}")
	public String deleteBillingList(@PathVariable("id")Integer id) {
		
		services.deleteList(id);
		
		return "redirect:/todaysUnPayedBillsByUser";
		
	}
	
	@GetMapping("/billsPage")
    public String openBillsPage(Model model ) {
		
		
		
		return "billsPage";
	}
	
}
