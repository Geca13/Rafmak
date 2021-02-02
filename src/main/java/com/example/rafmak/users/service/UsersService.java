package com.example.rafmak.users.service;

import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import com.example.rafmak.users.entity.RoleName;
import com.example.rafmak.users.entity.Users;

public interface UsersService extends UserDetailsService {
	
	Users saveAdmin(Users userDto) throws InvalidPasswordException;
	
	public Users saveEmployee(Users userDto,MultipartFile file) throws InvalidPasswordException;
	
	Users saveOwner(Users userDto) throws InvalidPasswordException;
	
	public Page<Users> findPagina(Integer pageNumber, Integer pageSize,String sortField, String sortDirection, String search);

}
