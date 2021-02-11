package com.example.rafmak.finance.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.rafmak.finance.entity.ReportProduct;

@Repository
public interface ReportProductRepository extends JpaRepository<ReportProduct, Integer>{

	Boolean existsByPidAndPriceAndDate(String pid, Double price,LocalDate date);
	
	ReportProduct findByPidAndPriceAndDate(String pid, Double price,LocalDate date);
	
	Boolean existsByPidAndDescriptionAndDate(String pid, String description,LocalDate date);

	ReportProduct findByPidAndDescriptionAndDate(String pid ,String description,LocalDate date);
}
