package com.example.rafmak.finance.repository;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.rafmak.finance.entity.DailyFiscalReport;

@Repository
public interface DailyFiscalReportRepository extends JpaRepository<DailyFiscalReport, Integer> {
	
	Boolean existsByDate(LocalDate date);
	
	DailyFiscalReport findByDate(LocalDate date);

}
