package com.example.rafmak.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rafmak.finance.entity.OverdueInvoices;

@Repository
public interface OverdueInvoicesRepository extends JpaRepository<OverdueInvoices, Integer> {

}