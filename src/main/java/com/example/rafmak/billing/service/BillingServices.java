package com.example.rafmak.billing.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import com.example.rafmak.billing.entity.Bill;
import com.example.rafmak.billing.entity.BillProductsList;
import com.example.rafmak.billing.repository.BillProductsListRepository;
import com.example.rafmak.billing.repository.BillRepository;
import com.example.rafmak.billing.repository.BillingProductsRepository;
import com.example.rafmak.invoice.repository.CompanyRepository;
import com.example.rafmak.product.entity.MeasuredProduct;
import com.example.rafmak.product.entity.Product;
import com.example.rafmak.product.repository.MeasuredProductRepository;
import com.example.rafmak.product.repository.ProductRepository;
import com.example.rafmak.product.repository.QtyHistoryRepository;
import com.example.rafmak.users.entity.Users;
import com.example.rafmak.users.repository.UsersRepository;
import com.example.rafmak.users.service.UsersDetails;


@Service
public class BillingServices {
	
	@Autowired
	UsersRepository userRepository;
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	MeasuredProductRepository mpRepository;
	
	@Autowired
	BillProductsListRepository bplRepository;
	
	@Autowired
	QtyHistoryRepository qhRepository;
	
	@Autowired
	BillingProductsRepository bpRepository;
	
	@Autowired
	CompanyRepository companyRepository;
	
	@Autowired
	BillRepository billRepository;
	
	public Product findProduct(String id) {
		return productRepository.findById(id);
	}
	
	public MeasuredProduct findMeasuredProduct(String id) {
		return mpRepository.findById(id);
		
	}
	
	public Integer dailyBillCounter() {
		List<BillProductsList> lists = bplRepository.findAll();
		if(lists.isEmpty()) {
			BillProductsList bpl = new BillProductsList();
			bpl.setDailyBillCounter(1);
			return bpl.getDailyBillCounter();
		}
		LocalDate today = LocalDate.now();
		
		BillProductsList lastList = bplRepository.findById(lists.size()).get();
		BillProductsList list = new BillProductsList();
		list.setTime(today);
		if (!today.equals(lastList.getTime())) {
			list.setDailyBillCounter(1);
		}else {
			list.setDailyBillCounter(lastList.getDailyBillCounter()+1);
		}
		
		return list.getDailyBillCounter();
		
	}
	
	public BillProductsList findBillByCounter(Integer id , LocalDate date) {
		LocalDate d = LocalDate.now();
		BillProductsList list = bplRepository.findByDailyBillCounterAndTime(id, d);
		
		   return list;
	}
	
	public List<Bill> todaysBills(){
		
		List<Bill> bills = billRepository.findByCreated(LocalDate.now());
		
		return bills;
		
	}
	
	
    public List<Bill> billsOnDate(String date){
		
	LocalDate d = LocalDate.parse(date);
    	
		List<Bill> bills = billRepository.findByCreated(d);
		
		return bills;
	}
    
    public List<Bill> billsOnPeriod(String startDate,String endDate){
		
    	LocalDate d1 = LocalDate.parse(startDate);
    	LocalDate d2 = LocalDate.parse(endDate);
        	
    		List<Bill> bills = billRepository.findByCreatedBetween(d1,d2);
    		
    		return bills;
    	}
    	
}
