package com.example.rafmak.billing.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rafmak.billing.entity.BillProductsList;
import com.example.rafmak.users.entity.Users;

@Repository
public interface BillProductsListRepository extends JpaRepository<BillProductsList, Integer> {
	
	BillProductsList findByDailyBillCounterAndTime(Integer id,LocalDate date);
	
	List<BillProductsList> findByUserAndPrintedAndTime(Users user,Boolean printed, LocalDate time);
	

}
