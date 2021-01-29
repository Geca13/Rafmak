package com.example.rafmak.product.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rafmak.product.entity.MeasuredProduct;

@Repository
public interface MeasuredProductRepository extends JpaRepository<MeasuredProduct, Integer> {

	MeasuredProduct findById(String id);
	
	boolean existsById(String id);
	
	MeasuredProduct findByDescription(String description);
	
	boolean existsByDescription(String description);
}
