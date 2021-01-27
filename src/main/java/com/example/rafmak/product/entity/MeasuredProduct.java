package com.example.rafmak.product.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeasuredProduct {
	
	@Id
	private String id;
	
	private String description;
	
	private Double price;
	
	private Double totalQty;
	
	private Double totalWorth;

	
}
