package com.example.rafmak.billing.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.rafmak.billing.entity.Bill;

public interface BillRepository extends JpaRepository<Bill, Integer> {
	
	List<Bill> findByCreated(LocalDate created);
	
	List<Bill> findByCreatedBetween(LocalDate startDate,LocalDate endDate);

}
