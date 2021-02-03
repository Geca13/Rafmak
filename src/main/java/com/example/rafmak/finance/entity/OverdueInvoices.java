package com.example.rafmak.finance.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.example.rafmak.invoice.entity.Invoice;

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
	
	@ManyToMany
	private List<Invoice>withExpiredDate;

}
