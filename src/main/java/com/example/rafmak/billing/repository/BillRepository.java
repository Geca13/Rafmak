package com.example.rafmak.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rafmak.billing.entity.Bill;

@Repository
public interface BillRepository extends JpaRepository<Bill, Integer> {

}
