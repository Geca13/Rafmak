package com.example.rafmak.finance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rafmak.finance.entity.Payment;
import com.example.rafmak.invoice.entity.Company;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

	List<Payment> findByCompany(Company company);
}
