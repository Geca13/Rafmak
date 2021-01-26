package com.example.rafmak.product.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.rafmak.product.entity.Category;
import com.example.rafmak.product.entity.Manufacturer;
import com.example.rafmak.product.entity.MeasuredProduct;
import com.example.rafmak.product.entity.Product;
import com.example.rafmak.product.entity.QtyHistory;
import com.example.rafmak.product.repository.CategoryRepository;
import com.example.rafmak.product.repository.ManufacturerRepository;
import com.example.rafmak.product.repository.MeasuredProductRepository;
import com.example.rafmak.product.repository.ProductRepository;
import com.example.rafmak.product.repository.QtyHistoryRepository;

@Service
public class ProductService {
	
	@Autowired
	ProductRepository productRepository;
	@Autowired
	ManufacturerRepository manufacturerRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	MeasuredProductRepository mpRepository;
	@Autowired
	QtyHistoryRepository qhRepository;
	
	public Product saveProduct(Product product) {
		
		List<Product> products = productRepository.findAll();
	    Product newProduct = new Product();
	    newProduct.setId(String.valueOf(products.size()+1));
		newProduct.setDescription(product.getDescription());
		newProduct.setMesurmentSize(product.getMesurmentSize());
		newProduct.setRegularPrice(product.getRegularPrice());
		newProduct.setPriceOnPack(product.getPriceOnPack());
		newProduct.setDiscPrice(product.getPriceOnPack() * 0.9);
		newProduct.setTotalQty(product.getTotalQty());
		productRepository.save(newProduct);
		
		newQty(newProduct, product.getTotalQty(), LocalDate.now());
	
	    if(categoryRepository.existsByCategoryName(product.getCategory().getCategoryName())) {
			Category cat = categoryRepository.findByCategoryName(product.getCategory().getCategoryName());
			 newProduct.setCategory(cat);
		}
		
		if(manufacturerRepository.existsByManufacturerName(product.getManufacturer().getManufacturerName())) {
			Manufacturer man = manufacturerRepository.findByManufacturerName(product.getManufacturer().getManufacturerName());
			newProduct.setManufacturer(man);
		}
		
		       return productRepository.save(newProduct);
	}
	
	public QtyHistory addingQty(String id, QtyHistory history) {
		
		Product product = productRepository.findById(id);
	
		 newQty(product, history.getQty(),LocalDate.now());
		
		 product.setTotalQty(product.getTotalQty()+history.getQty());
		 
		 productRepository.save(product);
		 
		 return qhRepository.save(history);
	}
	
	
	
	public MeasuredProduct createNewMesuredProduct(String id) {
		
		Product product = productRepository.findById(id);
		MeasuredProduct newMeasuredProduct = new MeasuredProduct();
		  newMeasuredProduct.setId("*"+product.getId());
		  newMeasuredProduct.setDescription(product.getDescription());
		  newMeasuredProduct.setMetric(Double.parseDouble(product.getMesurmentSize()));
		  newMeasuredProduct.setPrice(product.getRegularPrice()/newMeasuredProduct.getMetric());
	      newMeasuredProduct.setTotalWorth(product.getRegularPrice());
		   newQty(product, -1, LocalDate.now());
		   product.setTotalQty(product.getTotalQty()-1);
		    productRepository.save(product);
		
	         	return mpRepository.save(newMeasuredProduct);
		
	}
	
	public MeasuredProduct addQtyToMeasuredProducts(String id,Integer number) {
		
		MeasuredProduct mp = mpRepository.findById(id);
		String str = mp.getId().substring(1);
		
		Product product = productRepository.findById(str);
		mp.setMetric(mp.getMetric()+(number * Double.parseDouble(product.getMesurmentSize())));
		mp.setTotalWorth(mp.getTotalWorth()+(number*product.getRegularPrice()));
		mpRepository.save(mp);
		newQty(product, -number, LocalDate.now());
		product.setTotalQty(product.getTotalQty()-number);
		productRepository.save(product);
		return mpRepository.save(mp);
		
	}
	
	
	public void newQty(Product product,Integer qty,LocalDate date) {
		QtyHistory qh = new QtyHistory( product, qty, date);
		  qhRepository.save(qh);
	}

}
