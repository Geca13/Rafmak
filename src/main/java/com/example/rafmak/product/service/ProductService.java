package com.example.rafmak.product.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.example.rafmak.product.entity.Category;
import com.example.rafmak.product.entity.Manufacturer;
import com.example.rafmak.product.entity.MeasuredProduct;
import com.example.rafmak.product.entity.MeasuredProductQtyHistory;
import com.example.rafmak.product.entity.Product;
import com.example.rafmak.product.entity.QtyHistory;
import com.example.rafmak.product.repository.CategoryRepository;
import com.example.rafmak.product.repository.ManufacturerRepository;
import com.example.rafmak.product.repository.MeasuredProductQtyHistoryRepository;
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
	@Autowired
	MeasuredProductQtyHistoryRepository mpqhRepository;
	
	public Product saveProduct(Product product) {
		
		List<Product> products = productRepository.findAll();
	    Product newProduct = new Product();
	    newProduct.setId(String.valueOf(products.size()+1));
		newProduct.setDescription(product.getDescription());
		newProduct.setMesurmentSize(product.getMesurmentSize());
		newProduct.setPrice(product.getPrice());
		newProduct.setPriceOnPack(product.getPriceOnPack());
		newProduct.setDiscPrice(product.getPriceOnPack() * 0.9);
		newProduct.setTotalQty(product.getTotalQty());
		productRepository.save(newProduct);
		
		newQtyToProduct(newProduct, product.getTotalQty(), LocalDate.now(),newProduct.getTotalQty());
	
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
		
		 product.setTotalQty(product.getTotalQty()+history.getQty());
		 
		 productRepository.save(product);
		 newQtyToProduct(product, history.getQty(),LocalDate.now(),product.getTotalQty());
		 return qhRepository.save(history);
	}
	
	public MeasuredProduct createNewMesuredProduct(String id) {
		
		Product product = productRepository.findById(id);
		MeasuredProduct newMeasuredProduct = new MeasuredProduct();
		  newMeasuredProduct.setId("*"+product.getId());
		  newMeasuredProduct.setDescription(product.getDescription());
		  newMeasuredProduct.setTotalQty(Double.parseDouble(product.getMesurmentSize()));
		  newMeasuredProduct.setPrice(product.getPrice()/newMeasuredProduct.getTotalQty());
	      newMeasuredProduct.setTotalWorth(product.getPrice());
	      
	       product.setTotalQty(product.getTotalQty()-1);
		    mpRepository.save(newMeasuredProduct);
		    productRepository.save(product);
		    newQtyToProduct(product, -1.00, LocalDate.now(),product.getTotalQty());
		    newQtyToMeasuredProduct(newMeasuredProduct, Double.parseDouble(product.getMesurmentSize()), LocalDate.now(), newMeasuredProduct.getTotalQty());
	         	return mpRepository.save(newMeasuredProduct);
		
	}
	
	public MeasuredProduct addQtyToMeasuredProducts(String id,Double number) {
		
		MeasuredProduct mp = mpRepository.findById(id);
		String str = mp.getId().substring(1);
		
		Product product = productRepository.findById(str);
		mp.setTotalQty(mp.getTotalQty()+(number * Double.parseDouble(product.getMesurmentSize())));
		mp.setTotalWorth(mp.getTotalWorth()+(number*product.getPrice()));
		mpRepository.save(mp);
		
		product.setTotalQty(product.getTotalQty()-number);
		productRepository.save(product);
		newQtyToProduct(product, -number, LocalDate.now() ,product.getTotalQty());
	    newQtyToMeasuredProduct(mp, number * Double.parseDouble(product.getMesurmentSize()), LocalDate.now(), mp.getTotalQty());

		return mpRepository.save(mp);
		
	}
	
	public List<QtyHistory> findQtyByPid(String pid) {
		
		if(pid != null) {
			return qhRepository.findAllByProductId(pid);
		}
		return qhRepository.findAll();
	}
	
	public List<MeasuredProductQtyHistory> findQtyByMPid(String id) {
		
		if(id != null) {
			return mpqhRepository.findAllByMeasuredProductId(id);
		}
		return mpqhRepository.findAll();
	}
	
	public  void newQtyToProduct(Product product,Double qty,LocalDate date, Double newQty) {
		QtyHistory qh = new QtyHistory( product, qty, date,newQty);
		  qhRepository.save(qh);
	}
	
	public void newQtyToMeasuredProduct(MeasuredProduct measuredProduct, Double qty, LocalDate date, Double newQty) {
		MeasuredProductQtyHistory history = new MeasuredProductQtyHistory(measuredProduct, qty, date, newQty);
		mpqhRepository.save(history);
	}

}
