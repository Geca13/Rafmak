package com.example.rafmak.finance.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.rafmak.finance.entity.OverdueInvoices;
import com.example.rafmak.invoice.repository.InvoiceRepository;

@Service
public class FinanceService {

	@Autowired
	InvoiceRepository invoiceRepository;
	
	public void expiredDates() {
		
	
	}
	
}
