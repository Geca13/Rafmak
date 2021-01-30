package com.example.rafmak.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rafmak.product.entity.PaintMix;

@Repository
public interface PaintMixRepository extends JpaRepository<PaintMix, Integer> {

	PaintMix findByDescription(String description);
}
