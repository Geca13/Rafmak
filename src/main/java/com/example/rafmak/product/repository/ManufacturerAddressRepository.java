package com.example.rafmak.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rafmak.product.entity.ManufacturerAddress;

@Repository
public interface ManufacturerAddressRepository extends JpaRepository<ManufacturerAddress, Integer> {

}
