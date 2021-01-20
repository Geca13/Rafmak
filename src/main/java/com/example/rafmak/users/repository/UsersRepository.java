package com.example.rafmak.users.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.rafmak.users.entity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
	
	Users findByEmail(String email);
	
	Users findByToken(String token);
	
	@Query("SELECT u FROM Users u WHERE CONCAT(u.firstName, ' ', u.lastName, ' ', u.email) LIKE %?1%")
	Page<Users> findBySearch(String search, Pageable pageable);

}
