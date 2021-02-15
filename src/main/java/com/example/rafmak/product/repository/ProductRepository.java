package com.example.rafmak.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rafmak.product.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

	Product findById(String id);

	boolean existsById(String id);
	
	Product findByDescription(String description);

	boolean existsByDescription(String description);
	
	List<Product> findAllByDescription(String description);
	
	List<Product> findAllByManufacturerId(Integer id);
	
}
