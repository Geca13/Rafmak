package com.example.rafmak.product.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
	private String id;
	
	private String description;
	
	private String mesurmentSize;
	
	private Double price;
	
	private Double priceOnPack;
	
	private Double discPrice;
	
	private Double totalQty;
	
	@ManyToOne
	private Manufacturer manufacturer;
	
	@ManyToOne
    private Category category;
	
	
}
