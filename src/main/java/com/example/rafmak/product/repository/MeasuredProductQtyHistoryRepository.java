package com.example.rafmak.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rafmak.product.entity.MeasuredProductQtyHistory;

@Repository
public interface MeasuredProductQtyHistoryRepository extends JpaRepository<MeasuredProductQtyHistory, Integer> {

}
