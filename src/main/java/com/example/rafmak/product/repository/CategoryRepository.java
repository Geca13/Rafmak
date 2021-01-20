package com.example.rafmak.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rafmak.product.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
	
    Category findByCategoryName(String categoryName);
	
	Boolean existsByCategoryName(String categoryName);

}
