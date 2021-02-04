package com.example.rafmak.finance.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.example.rafmak.invoice.entity.Company;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OverdueInvoices {
	
	@Id
	private Integer id;
	
	@ManyToOne
	@JoinColumn(referencedColumnName = "id")
	private Company company;
	
	private Double total;

}
