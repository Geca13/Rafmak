package com.example.rafmak.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.rafmak.finance.entity.ReportProduct;

@Repository
public interface ReportProductRepository extends JpaRepository<ReportProduct, Integer>{

	Boolean existsByPidAndPrice(String pid, Double price);
	
	ReportProduct findByPid(String pid);
	
	Boolean existsByPidAndDescription(String pid, String description);

	ReportProduct findByPidAndDescription(String pid ,String description);
}
