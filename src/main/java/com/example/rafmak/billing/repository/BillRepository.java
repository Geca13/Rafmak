package com.example.rafmak.billing.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.rafmak.billing.entity.Bill;
import com.example.rafmak.users.entity.Users;

public interface BillRepository extends JpaRepository<Bill, Integer> {
	
	List<Bill> findByCreated(LocalDate created);
	
	List<Bill> findByCreatedAndUser(LocalDate created, Users user);
	
	List<Bill> findByCreatedBetween(LocalDate startDate,LocalDate endDate);

}
