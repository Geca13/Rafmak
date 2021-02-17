package com.example.rafmak.billing.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.rafmak.billing.entity.Bill;
import com.example.rafmak.billing.entity.BillProductsList;
import com.example.rafmak.billing.entity.BillingProducts;
import com.example.rafmak.billing.repository.BillProductsListRepository;
import com.example.rafmak.billing.repository.BillRepository;
import com.example.rafmak.billing.repository.BillingProductsRepository;
import com.example.rafmak.invoice.repository.CompanyRepository;
import com.example.rafmak.product.entity.MeasuredProduct;
import com.example.rafmak.product.entity.Product;
import com.example.rafmak.product.repository.MeasuredProductRepository;
import com.example.rafmak.product.repository.ProductRepository;
import com.example.rafmak.product.repository.QtyHistoryRepository;
import com.example.rafmak.users.repository.UsersRepository;



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
		list.setCreated(today);
		if (!today.equals(lastList.getCreated())) {
			list.setDailyBillCounter(1);
		}else {
			list.setDailyBillCounter(lastList.getDailyBillCounter()+1);
		}
		
		return list.getDailyBillCounter();
		
	}
	
	
	public BillProductsList findBillByCounter(Integer id , LocalDate date) {
		LocalDate d = LocalDate.now();
		BillProductsList list = bplRepository.findByDailyBillCounterAndCreated(id, d);
		
		   return list;
	}
	
	public void deleteList(Integer id) {
		
		BillProductsList list = bplRepository.findById(id).get();
		
		for (BillingProducts product : list.getProducts()) {
			product.setDate(null);
			product.setDescription(null);
			product.setItemTax(null);
			product.setItemTotal(null);
			product.setPid(null);
			product.setPrice(null);
			product.setQty(null);
			bpRepository.save(product);
		}
		list.setProducts(null);
		list.setUser(null);
		bplRepository.save(list);
		
		
		
	}
	
	public void deleteBillingProducts() {
		
		List<BillingProducts> products = bpRepository.findAll();
		for (BillingProducts product : products) {
			
			if(product.getDate()== null && product.getDescription().equals(null) && product.getItemTax() == null && product.getItemTotal( )== null && product.getPid().equals(null) && product.getPrice() == null && product.getQty()== null) {
				bpRepository.deleteById(product.getId());;
			}
		}
		
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
