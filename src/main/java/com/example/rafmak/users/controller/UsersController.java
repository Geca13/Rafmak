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

@Controller
public class UsersController {
	
	@Autowired
	UsersRepository userRepository;
	@Autowired
	UsersServiceImpl usersServiceImpl;
	@Autowired
	AddressRepository addressRepository;
	@Autowired
	CountryRepository countryRepository;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	UsersService usersService;
	
	@GetMapping("/login")
	public String login(Model model ) {
		
		 return "login";
	}
	
	@GetMapping("/")
	public String logedIn(@AuthenticationPrincipal UsersDetails userD,Model model ) {
		
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
		model.addAttribute("user", user);
		
		
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
		} catch (InvalidPasswordException e) {
			model.addAttribute("error", e.getMessage());
			return "index";
		}
		
		return "redirect:/";
		
	}
	
	@GetMapping("/allUsers")
	public String getAllUsers(Model model) {
		
		List<Users> allUsers = userRepository.findAll();
		model.addAttribute("allUsers", allUsers);
		
		return "all_users";
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
        
		         return "profile" ;
	}
	
	@GetMapping("/profile/{id}/newAddress")
	public String setAddress(@PathVariable ("id") Integer id, @AuthenticationPrincipal UsersDetails userD, Model model,@ModelAttribute("address") Address address) {
		
	    Users user = userRepository.findById(id).get();
	    List<Country> listCountry = countryRepository.findAll();
         model.addAttribute("user", user);
		 model.addAttribute("listCountry", listCountry);
		         return "new_Address";
	}
	
	@PostMapping("/profile/{id}/newAddress")
	public String processCreationForm(@PathVariable("id")Integer id, Address address) {
		
		Users user = userRepository.findById(id).get();
		  Address  address1 = new Address();
		  address1.setStreetName(address.getStreetName());
		  address1.setStreetNumber(address.getStreetNumber());
		  address1.setCity(address.getCity());
		  address1.setZipCode(address.getZipCode());
		  address1.setCountry(address.getCountry());
		  address1.setUser(user);
		
		addressRepository.save(address1);
		          return "redirect:/confirmAndPay/" + user.getId();
	}		
}
