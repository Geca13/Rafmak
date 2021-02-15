package com.example.rafmak.users.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.rafmak.billing.entity.Bill;
import com.example.rafmak.billing.entity.BillProductsList;
import com.example.rafmak.billing.repository.BillProductsListRepository;
import com.example.rafmak.billing.repository.BillRepository;
import com.example.rafmak.invoice.entity.Invoice;
import com.example.rafmak.invoice.repository.InvoiceRepository;
import com.example.rafmak.users.entity.Role;
import com.example.rafmak.users.entity.RoleName;
import com.example.rafmak.users.entity.Users;
import com.example.rafmak.users.repository.RoleRepository;
import com.example.rafmak.users.repository.UsersRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.transaction.Transactional;

@Service
@Transactional
public class UsersServiceImpl implements UsersService {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	InvoiceRepository invoiceRepository;
	@Autowired
	BillProductsListRepository bplRepository;
	@Autowired
	BillRepository billRepository;
	
	@Autowired
	private UsersRepository usersRepository;
	
	private Pattern pattern;
	private Matcher matcher;
	
	private static  final String PASSWORD_REGEX = 
	        ("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,15})");
	
	public UsersServiceImpl() {
		pattern = Pattern.compile(PASSWORD_REGEX );
	}
	
	public boolean validate(final String password) {
		matcher =pattern.matcher(password);
		return matcher.matches();
	}

	@Override
	public Users saveAdmin(Users userDto) throws InvalidPasswordException {
		
	    UsersServiceImpl validator = new UsersServiceImpl();
		Users user = new Users();
		 user.setEmail(userDto.getEmail());
		 user.setPassword(passwordEncoder.encode(userDto.getPassword()));
		    if(validator.validate(userDto.getPassword()) == false) {
		    	throw new InvalidPasswordException("Your chosen password doesnt fit our creteria , it must contain at least 1 number, UpperCase and LowerCase letters and 1 special character");
		    }
		 user.setFirstName(userDto.getFirstName());
         user.setLastName(userDto.getLastName());
         user.setAge(userDto.getAge());
        Role role = roleRepository.findByRole(RoleName.ROLE_ADMIN);
         user.setRoles( Collections.singleton(role));
		 user.setDate(LocalDate.now());
		        return usersRepository.save(user);
	}
	
	@Override
	public Users saveEmployee(Users user , MultipartFile file) throws InvalidPasswordException {
		
		UsersServiceImpl validator = new UsersServiceImpl();
		Users user1 = new Users();
	     user1.setEmail(user.getEmail());
		 user1.setPassword(passwordEncoder.encode(user.getPassword()));
		    if(validator.validate(user.getPassword()) == false) {
		    	throw new InvalidPasswordException("Your chosen password doesnt fit our creteria , it must contain at least 1 number, UpperCase and LowerCase letters and 1 special character");
		    }
		 user1.setFirstName(user.getFirstName());
         user1.setLastName(user.getLastName());
         user1.setAge(user.getAge());
        Role role = roleRepository.findByRole(RoleName.ROLE_EMPLOYEE);
         user1.setRoles( Collections.singleton(role));
		 user1.setDate(LocalDate.now());
		 user1.setSalary(0.00);
		 String fileName = StringUtils.cleanPath(file.getOriginalFilename());
	       if(fileName.contains("..")) {
	       	System.out.println("not a valid file");
	        }
	        try {
				user1.setImage(Base64.getEncoder().encodeToString(file.getBytes()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		      return usersRepository.save(user1);
	}
	
	@Override
	public Users saveOwner(Users userDto) throws InvalidPasswordException {
		
	    UsersServiceImpl validator = new UsersServiceImpl();
		Users user = new Users();
		 user.setEmail(userDto.getEmail());
		 user.setPassword(passwordEncoder.encode(userDto.getPassword()));
		    if(validator.validate(userDto.getPassword()) == false) {
		    	throw new InvalidPasswordException("Your chosen password doesnt fit our creteria , it must contain at least 1 number, UpperCase and LowerCase letters and 1 special character");
		    }
		 user.setFirstName(userDto.getFirstName());
         user.setLastName(userDto.getLastName());
         user.setAge(userDto.getAge());
        Role role = roleRepository.findByRole(RoleName.ROLE_OWNER);
         user.setRoles( Collections.singleton(role));
		 user.setDate(LocalDate.now());
		       return usersRepository.save(user);
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Users user = usersRepository.findByEmail(username);
		   if(user == null) {
			  throw new UsernameNotFoundException("You are not signUped with that email");
		}
		      return new UsersDetails(user);
	}
	
	@Override
	public Page<Users> findPagina(Integer pageNumber, Integer pageSize, String sortField, String sortDirection,String search) {
		
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
		if(search != null) {
		    return	usersRepository.findBySearch(search, pageable);
		}
		return usersRepository.findAll(pageable);
	}
	
	public void forgotPassword(String token, String email) throws UserNotFoundException {
		
		Users user = usersRepository.findByEmail(email);
		if(user != null) {
			user.setToken(token);
			usersRepository.save(user);
		} else {
			
			throw new UserNotFoundException("We dont have user with "+ email + " email, in our database ");
		}
	}
	
	public Users getToken(String token) {
		return usersRepository.findByToken(token);
	}
	
	public void updatePassword(Users user, String newPassword) throws InvalidPasswordException {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String encodePassword = encoder.encode(newPassword);
	       user.setPassword(encodePassword);
		   user.setToken(null);
		       usersRepository.save(user);
	}
	
	public Double getInvoicesTotalByDayByUser(Users user) {
	  Double invoicesTotalSum = 0.00;
		List<Invoice> invoices = invoiceRepository.findByUserAndIssued(user, LocalDate.now());
		for (Invoice invoice : invoices) {
			invoicesTotalSum = invoicesTotalSum + invoice.getTotal();
		}
		return invoicesTotalSum;
	}
	
	public Integer getInvoicesTotalNumberByUser(Users user) {
		List<Invoice> invoices = invoiceRepository.findByUserAndIssued(user, LocalDate.now());
		return invoices.size();
	}
	
	public List<Invoice> getTodaysInvoicesByUser(Users user) {
		List<Invoice> invoices = invoiceRepository.findByUserAndIssued(user, LocalDate.now());
		return invoices;
	}
	
	public Integer getPayedBillsTotalNumberByUser(Users user) {
		List<BillProductsList> bills = bplRepository.findByUserAndPrintedAndCreated(user, true,LocalDate.now());
		return bills.size();
	}
	
	public Integer getUnpayedBillsTotalNumberByUser(Users user) {
		List<BillProductsList> bills = bplRepository.findByUserAndPrintedAndCreated(user, false,LocalDate.now());
		return bills.size();
	}
	
	public Double getPayedBillsTotalByDayByUser(Users user) {
		  Double payedBillsTotalSum = 0.00;
			List<BillProductsList> bills = bplRepository.findByUserAndPrintedAndCreated(user, true,LocalDate.now());
			for (BillProductsList billProductsList : bills) {
				payedBillsTotalSum = payedBillsTotalSum + billProductsList.getTotal();
			}
		    return payedBillsTotalSum;
		}
	
	public Double getUnpayedBillsTotalByDayByUser(Users user) {
		  Double unPayedBillsTotalSum = 0.00;
			List<BillProductsList> bills = bplRepository.findByUserAndPrintedAndCreated(user, false,LocalDate.now());
			for (BillProductsList billProductsList : bills) {
				unPayedBillsTotalSum = unPayedBillsTotalSum + billProductsList.getTotal();
			}
		    return unPayedBillsTotalSum;
		}
	
	public List<Bill> payedBills(Users user){
		
		List<Bill> bills = billRepository.findByCreatedAndUser(LocalDate.now(), user);
		
		return bills;
	}
	
    public List<BillProductsList> unPayedBills(Users user){
		
		List<BillProductsList> bills = bplRepository.findByUserAndPrintedAndCreated(user, false ,LocalDate.now());
		
		return bills;
	}
	
}
