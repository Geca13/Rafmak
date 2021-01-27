package com.example.rafmak.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rafmak.billing.entity.BillingProducts;

@Repository
public interface BillingProductsRepository extends JpaRepository<BillingProducts, String> {

}
