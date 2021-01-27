package com.example.rafmak.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.rafmak.product.entity.Product;
import com.example.rafmak.product.entity.QtyHistory;

public interface QtyHistoryRepository extends JpaRepository<QtyHistory, Integer> {

	List<QtyHistory> findAllByProductId(String pid);

	
}
