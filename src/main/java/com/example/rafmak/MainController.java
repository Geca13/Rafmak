package com.example.rafmak;





import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.rafmak.users.entity.Users;
import com.example.rafmak.users.service.InvalidPasswordException;
import com.example.rafmak.users.service.UsersService;
import com.example.rafmak.users.service.userWithThatEmailAlreadyExistsException;

@Controller
@RequestMapping("/signUpForm")
public class MainController {
	
	private UsersService usersService;

	public MainController(UsersService usersService) {
		super();
		this.usersService = usersService;
	}
	
	@ModelAttribute("user")
	public Users userDto() {
		return new Users();
	}
	
	
	@GetMapping
	public String showSignUpForm(Model model) {
		
	    return "signUpForm";
	}
	
	@PostMapping
	public String registerAdmin(@ModelAttribute("user") Users userDto, Model model) {
		
		try {
			usersService.saveAdmin(userDto);
		} catch (InvalidPasswordException | userWithThatEmailAlreadyExistsException e) {
			model.addAttribute("error", e.getMessage());
			return "signUpForm";
		}
		
		return  "redirect:/signUpForm?success" ;
	}
	
}
 



