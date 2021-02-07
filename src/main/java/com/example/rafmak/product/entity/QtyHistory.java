package com.example.rafmak.product.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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
public class QtyHistory {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	private Product product;
	
	private Double qty;
	
	private LocalDate date;
	
	private Double newQty;
	
	

	public QtyHistory(Product product, Double qty, LocalDate date, Double newQty) {
		super();
		this.product = product;
		this.qty = qty;
		this.date = date;
		this.newQty = newQty;
	}
	
}
