package com.example.rafmak.invoice.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.rafmak.invoice.entity.Company;
import com.example.rafmak.invoice.entity.Invoice;
import com.example.rafmak.users.entity.Users;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

	List<Invoice> findByUserAndIssued(Users user,LocalDate issued);
	
	List<Invoice> findByUser(Users user);
	
	List<Invoice> findByCompany(Company company);
	
	List<Invoice> findByArrival(LocalDate date);
	
	List<Invoice>findByIssued(LocalDate issued);
	
	List<Invoice>findByIssuedBetween(LocalDate start,LocalDate end);
	
	List<Invoice>findByCompanyAndArrival(Company company, LocalDate arrived);
	
	List<Invoice> findByCompanyId(Integer id);
	

}
