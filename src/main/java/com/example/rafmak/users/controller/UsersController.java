package com.example.rafmak.users.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.rafmak.billing.entity.Bill;
import com.example.rafmak.billing.entity.BillProductsList;
import com.example.rafmak.billing.repository.BillProductsListRepository;
import com.example.rafmak.billing.repository.BillRepository;
import com.example.rafmak.billing.service.BillingServices;
import com.example.rafmak.invoice.entity.Invoice;
import com.example.rafmak.invoice.repository.InvoiceRepository;
import com.example.rafmak.invoice.service.InvoiceServices;
import com.example.rafmak.users.entity.Address;
import com.example.rafmak.users.entity.Country;
import com.example.rafmak.users.entity.Users;
import com.example.rafmak.users.repository.AddressRepository;
import com.example.rafmak.users.repository.CountryRepository;
import com.example.rafmak.users.repository.RoleRepository;
import com.example.rafmak.users.repository.UsersRepository;
import com.example.rafmak.users.service.InvalidPasswordException;
import com.example.rafmak.users.service.UsersDetails;
import com.example.rafmak.users.service.UsersService;
import com.example.rafmak.users.service.UsersServiceImpl;
import com.example.rafmak.users.service.userWithThatEmailAlreadyExistsException;

@Controller
public class UsersController {
	
	@Autowired
	UsersRepository userRepository;
	@Autowired
	UsersServiceImpl usersServiceImpl;
	@Autowired
	CountryRepository countryRepository;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	UsersService usersService;
	@Autowired
	InvoiceRepository invoiceRepository ;
	@Autowired
	InvoiceServices invoiceServices;
	@Autowired
	BillingServices services;
	@Autowired
	BillRepository billRepository;
	@Autowired
	BillProductsListRepository bplRepository;
	
	
	@GetMapping("/login")
	public String login(Model model ) {
		
		 return "login";
	}
	
	@GetMapping("/")
	public String logedIn(@AuthenticationPrincipal UsersDetails userD,Model model ) {
		
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
		   model.addAttribute("user", user);
		Double invoicesTotalSum = usersServiceImpl.getInvoicesTotalByDayByUser(user);
		   model.addAttribute("invoicesTotalSum", invoicesTotalSum);
		Integer invoicesTotal = usersServiceImpl.getInvoicesTotalNumberByUser(user);
	       model.addAttribute("invoicesTotal", invoicesTotal);
		Integer numberPayedBills = usersServiceImpl.getPayedBillsTotalNumberByUser(user);
		   model.addAttribute("numberPayedBills", numberPayedBills);
		Integer numberUnPayedBills = usersServiceImpl.getUnpayedBillsTotalNumberByUser(user);
		   model.addAttribute("numberUnPayedBills", numberUnPayedBills);
		Double payedBillsTotalSum = usersServiceImpl.getPayedBillsTotalByDayByUser(user);
		   model.addAttribute("payedBillsTotalSum", payedBillsTotalSum);
		Double unpayedBillsTotalSum = usersServiceImpl.getUnpayedBillsTotalByDayByUser(user);
		   model.addAttribute("unpayedBillsTotalSum", unpayedBillsTotalSum);
		   invoiceServices.findExpiredInvoices();
		   invoiceServices.calculateLateDateDebt();
		   
		 return "index";
	}
	
	@GetMapping("/addEmployee")
	public String newEmployee(Model model,Users user,MultipartFile file ) {
		
		Users newUser = new Users();
		model.addAttribute("user", newUser);
		model.addAttribute("file", file);
		
		return "addNewEmployee";
	}
	
	@PostMapping("/addEmployee")
	public String addNewEmployees(Model model,@ModelAttribute("user")Users user, MultipartFile file) {
		
		
		try {
			usersService.saveEmployee(user,file);
		} catch (InvalidPasswordException | userWithThatEmailAlreadyExistsException e) {
			model.addAttribute("error", e.getMessage());
			return "index";
		}
		
		return "redirect:/";
		
	}
	
	@GetMapping("/users")
	public String viewProductPage(Model model,@Param("search")String search) {
		
		 findPage(1,"firstName", "asc", model, search);
		     return "all_users";
	}
	
	@GetMapping("/pages/{pageNumber}")
	public String findPage(@PathVariable("pageNumber") Integer pageNumber,
			@RequestParam("sortField") String sortField,
			@RequestParam("sortDir") String sortDir,Model model,
			@Param("search") String search) {
		
		Integer pageSize = 2;
		
		Page<Users>pages = usersServiceImpl.findPagina(pageNumber, pageSize, sortField, sortDir, search);
		List<Users> listUsers = pages.getContent();
		
		  model.addAttribute("currentPage",pageNumber);
		  model.addAttribute("totalPages", pages.getTotalPages());
		  model.addAttribute("totalItems", pages.getTotalElements());
		  model.addAttribute("sortField", sortField);
		  model.addAttribute("sortDir", sortDir);
		  model.addAttribute("search", search);
		  model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		  model.addAttribute("listUsers", listUsers);
		        return "all_users";
		
	}
	
	@GetMapping("/users/profile/changePassword/{id}")
	public String changePassword(@PathVariable ("id") Integer id, Model model ) {
		
		Users user = userRepository.findById(id).get();
	       model.addAttribute("user", user);
		         return "changePassword";
		
	}
	@PostMapping("/users/profile/changePassword/{id}")
	  public String passwordForm( @PathVariable ("id") Integer id,@ModelAttribute("user")Users user) throws InvalidPasswordException {
		UsersServiceImpl validator = new UsersServiceImpl();

			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		Users user1 = userRepository.findById(id).get();
		
		user1.setPassword(encoder.encode(user.getPassword()));
		if(validator.validate(user1.getPassword()) == false) {
	    	
	    	throw new InvalidPasswordException("Your chosen password doesnt fit our creteria , it must contain at least 1 number, UpperCase and LowerCase letters and 1 special character");
	    }
		userRepository.save(user1);
		
		         return "redirect:/profile?change" ;
		
	}
	
	@GetMapping("/profile")
	public String profile( @AuthenticationPrincipal UsersDetails userD, Model model) {
		
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
        model.addAttribute("user", user);
		         return "profile" ;
	}
	
	@GetMapping("/profile/{id}")
	public String profileViewByAdmin( Model model ,@PathVariable("id")Integer id) {
		
		Users user = userRepository.findById(id).get();
        model.addAttribute("user", user);
		         return "profile" ;
	}
	
	@GetMapping("/terminate/{id}")
	public String terminateEmployee( Model model ,@PathVariable("id")Integer id) {
		
		Users user = userRepository.findById(id).get();
        user.setRoles(null);
        userRepository.save(user);
        List<Bill> bills = billRepository.findByUser(user);
        for (Bill bill : bills) {
			bill.setUser(null);
			billRepository.save(bill);
		}
        List<BillProductsList> billsList = bplRepository.findByUser(user);
        
        for (BillProductsList billProductsList : billsList) {
        	bplRepository.delete(billProductsList);
		}
        
        List<Invoice> invoices = invoiceRepository.findByUser(user);
        for (Invoice invoice : invoices) {
			invoice.setUser(null);
			invoiceRepository.save(invoice);
		}
        userRepository.delete(user);
		         return "redirect:/users" ;
	}
	
	@GetMapping("/administration")
	public String openAdministration(Model model ) {
			
			return "administration";
		}
	
	@GetMapping("/billsByEmployee/{id}")
	public String getTodayBillsByEmployee(Model model ,@PathVariable("id")Integer id) {
		
		Users user = userRepository.findById(id).get();
		List<Bill> list = usersServiceImpl.payedBills(user);
		model.addAttribute("bills", list);
		
		return "bills";
		
	}
	@GetMapping("/unPaidbillsByEmployee/{id}")
	public String getTodayUnpaidBillsByEmployee(Model model , @PathVariable("id")Integer id) {
		
		Users user = userRepository.findById(id).get();
		List<BillProductsList> bills = usersServiceImpl.unPayedBills(user);
		model.addAttribute("bills", bills);
		return "billingLists";
		
	}
	
	
	
	
	
}
