package com.example.rafmak.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rafmak.product.entity.Manufacturer;

@Repository
public interface ManufacturerRepository extends JpaRepository<Manufacturer, Integer> {
	
     Boolean existsByManufacturerName(String manufacturerName);
	
     Manufacturer findByManufacturerName(String manufacturerName);

}
