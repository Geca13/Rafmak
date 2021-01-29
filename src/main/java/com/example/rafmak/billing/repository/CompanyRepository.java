package com.example.rafmak.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rafmak.billing.entity.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {

}
