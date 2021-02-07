package com.example.rafmak.invoice.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.example.rafmak.users.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpiredDateInvoices {
	
	@Id
	private Integer id;
	
	@ManyToOne
	@JoinColumn(referencedColumnName = "id")
	private Company company;
	
	private Double total;
	
	@ManyToOne
	@JoinColumn(referencedColumnName = "id")
	private Users user;

}
