package com.example.rafmak.billing.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rafmak.billing.entity.BillProductsList;

@Repository
public interface BillProductsListRepository extends JpaRepository<BillProductsList, Integer> {
	
	BillProductsList findByDailyBillCounterAndTime(Integer id,LocalDate date);

}
