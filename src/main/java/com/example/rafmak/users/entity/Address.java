package com.example.rafmak.users.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
public class Address {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String streetName;
	
	private String streetNumber;
	
	private String city;
	
	private String zipCode;
	
	@ManyToOne
	@JoinColumn(referencedColumnName = "id")
	private Country country;
	
	@ManyToOne
	@JoinColumn(referencedColumnName = "id")
	private Users user;

}
