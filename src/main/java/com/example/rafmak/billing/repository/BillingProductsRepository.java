package com.example.rafmak.billing.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rafmak.billing.entity.BillingProducts;

@Repository
public interface BillingProductsRepository extends JpaRepository<BillingProducts, String> {
	
	Boolean existsByPid(String pid);
	
	BillingProducts findByPid(String pid);
	
	List<BillingProducts> findByDate (LocalDate date);

}
