package com.example.rafmak.product.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
public class MeasuredProductQtyHistory {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	private MeasuredProduct measuredProduct;
	
    private Double qty;
	
	private LocalDate date;
	
	private Double newMPQty;

	public MeasuredProductQtyHistory(MeasuredProduct measuredProduct, Double qty, LocalDate date, Double newMPQty) {
		super();
		this.measuredProduct = measuredProduct;
		this.qty = qty;
		this.date = date;
		this.newMPQty = newMPQty;
	}
	
	

}
