package com.example.rafmak.billing.entity;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillingProducts {
	
	@Id
	private String id;
	
	private String pid;
	
	private String description;
	
	private Double qty;
	
	private Double price;
	
	private Double itemTotal;
	
	private Double itemTax;
	
}
