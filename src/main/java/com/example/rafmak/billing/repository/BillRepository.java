package com.example.rafmak.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.rafmak.billing.entity.Bill;

public interface BillRepository extends JpaRepository<Bill, Integer> {

}
