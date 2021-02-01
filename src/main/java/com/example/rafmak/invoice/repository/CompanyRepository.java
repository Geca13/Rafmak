package com.example.rafmak.invoice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import com.example.rafmak.invoice.entity.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {
	
	@Query("SELECT p FROM Company p WHERE CONCAT(p.id, ' ', p.companyName) LIKE %?1%")
	@Transactional(readOnly = true)
	List<Company> findBySearch(String search);

}
