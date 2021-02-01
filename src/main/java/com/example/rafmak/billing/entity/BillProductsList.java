package com.example.rafmak.billing.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NaturalId;

import com.example.rafmak.users.entity.Users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillProductsList {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private Integer dailyBillCounter;
	
	@ManyToMany
	private List<BillingProducts> products;
	
	private Double total;
	
	private Double tax;
	
	private LocalDate time;
	
	private Boolean printed;
	
	@ManyToOne
	@JoinColumn(referencedColumnName = "id")
	private Users user;
	

}
