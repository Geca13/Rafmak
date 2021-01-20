package com.example.rafmak.product.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String description;
	
	private String mesurmentSize;
	
	private Double regularPrice;
	
	private Double priceOnPack;
	
	private Double discPrice;
	
	private Integer totalQty;
	
	@ManyToOne
	private Manufacturer manufacturer;
	
	@ManyToOne
    private Category category;
	
	
}
