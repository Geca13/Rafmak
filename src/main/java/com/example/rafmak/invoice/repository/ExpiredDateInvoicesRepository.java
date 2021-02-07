package com.example.rafmak.invoice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rafmak.invoice.entity.Company;
import com.example.rafmak.invoice.entity.ExpiredDateInvoices;

@Repository
public interface ExpiredDateInvoicesRepository extends JpaRepository<ExpiredDateInvoices, Integer> {

	List<ExpiredDateInvoices> findByCompany(Company company);
}
