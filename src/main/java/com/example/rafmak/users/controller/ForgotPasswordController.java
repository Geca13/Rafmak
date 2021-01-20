package com.example.rafmak.users.controller;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.rafmak.users.entity.Users;
import com.example.rafmak.users.service.InvalidPasswordException;
import com.example.rafmak.users.service.UserNotFoundException;
import com.example.rafmak.users.service.UsersServiceImpl;

import net.bytebuddy.utility.RandomString;

@Controller
public class ForgotPasswordController {
	
	@Autowired
	UsersServiceImpl userService;
	
	@Autowired
	private JavaMailSender sender;

	@GetMapping ("/forgotPassword")
	public String showForgotPasswordForm(Model model) {
		
		
		return "forgottenPassord";
	}
	
	@PostMapping("/forgotPassword")
	public String processRequest(HttpServletRequest request, Model model) {
		
		String email = request.getParameter("email");
		String token = RandomString.make(45);
		
		try {
			userService.forgotPassword(token, email);
			
			String resetPasswordLink = getSiteUrl(request) + "/newPassword?token=" + token;
			
			sendEmail(email,resetPasswordLink);
			
			model.addAttribute("message", "Email with reset password link has been sent, please go to your email");
			
		} catch (UserNotFoundException e) {
			model.addAttribute("error", e.getMessage());
		} catch (UnsupportedEncodingException |MessagingException e) {
			model.addAttribute("error", "Something bad happened while sending the email.");
		}
		
		return "forgottenPassord";
		
	}
	
	private void sendEmail(String email, String resetPasswordLink) throws UnsupportedEncodingException, MessagingException {
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom("Geca@geca.com", "Java imitacija");
		helper.setTo(email);
		
		String subject = "Someone triggered reset password for your account... ";
		String content = "Here is the link to reset your password " +
		                 "<a href=\"" + resetPasswordLink +"\"> Change my password</a>" 
		                 +" if it wasnt you, you can ignore this email";
		
		helper.setSubject(subject);
		helper.setText(content, true);
		sender.send(message);
		
	}

	public String getSiteUrl(HttpServletRequest request) {
		String siteUrl = request.getRequestURL().toString();
		return siteUrl.replace(request.getServletPath(), "");
	}
	
	
	@GetMapping("/newPassword")
	public String newPasswordForm(@Param(value = "token") String token, Model model) {
		Users user = userService.getToken(token);
		if(user == null) {
			
			model.addAttribute("message", "Invalid Token");
			return "message";
			
		}
		
		model.addAttribute("token", token);
		
		return "newPassword";
		
	}
	
	@PostMapping("/newPassword")
	public String setNewPassword(HttpServletRequest request,Model model) throws InvalidPasswordException {
		UsersServiceImpl validator = new UsersServiceImpl();
		String token = request.getParameter("token");
		String password = request.getParameter("password");
		
		Users user = userService.getToken(token);
              if(user == null) {
            	  
			   model.addAttribute("message", "Invalid Token");
			return "message";
			
              }else if(validator.validate(user.getPassword()) == false) {
      	    	
            	  model.addAttribute("pmessage", "Your chosen password doesnt fit our creteria , it must contain at least 1 number, UpperCase and LowerCase letters and 1 special character");
      			return "pmessage";
            		   
              }
              else{
            	  
            	  userService.updatePassword(user, password);
            	  model.addAttribute("message", "You have succesfully changed your password and you can easily log in now...");
      			
              }
		
		
              return "redirect:/login";
		
		
		
	}
	
}
