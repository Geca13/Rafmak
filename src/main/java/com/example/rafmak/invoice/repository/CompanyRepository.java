package com.example.rafmak.invoice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rafmak.invoice.entity.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {

}
