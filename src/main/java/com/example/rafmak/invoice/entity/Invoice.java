package com.example.rafmak.invoice.entity;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.example.rafmak.billing.entity.BillingProducts;
import com.example.rafmak.users.entity.Users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private Integer shippingId;
	
	private String comment;
	
	@ManyToOne
	@JoinColumn(referencedColumnName = "id")
	private Company company;
	
	@ManyToMany
	private List<BillingProducts> products;
	
	private Double total;
	
	private Double tax;
	
	private LocalDate issued;
	
	private LocalDate arrival;
	
	private Boolean expired;
	
	@ManyToOne
	@JoinColumn(referencedColumnName = "id")
	private Users user;

}
