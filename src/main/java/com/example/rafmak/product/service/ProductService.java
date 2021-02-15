package com.example.rafmak.product.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.rafmak.billing.entity.Bill;
import com.example.rafmak.product.entity.Category;
import com.example.rafmak.product.entity.Manufacturer;
import com.example.rafmak.product.entity.MeasuredProduct;
import com.example.rafmak.product.entity.MeasuredProductQtyHistory;
import com.example.rafmak.product.entity.PaintMix;
import com.example.rafmak.product.entity.Product;
import com.example.rafmak.product.entity.QtyHistory;
import com.example.rafmak.product.repository.CategoryRepository;
import com.example.rafmak.product.repository.ManufacturerRepository;
import com.example.rafmak.product.repository.MeasuredProductQtyHistoryRepository;
import com.example.rafmak.product.repository.MeasuredProductRepository;
import com.example.rafmak.product.repository.PaintMixRepository;
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
	@Autowired
	PaintMixRepository mixRepository;
	
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
		
		newQtyToProduct(newProduct, product.getTotalQty(), LocalDate.now(),newProduct.getTotalQty(), "Import");
	
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
	
	public Product updateProduct(String id, Product product) {
		
		Product product1 = productRepository.findById(id);
		
		product1.setDescription(product.getDescription());
		product1.setMesurmentSize(product.getMesurmentSize());
		product1.setPrice(product.getPrice());
		product1.setPriceOnPack(product.getPriceOnPack());
		product1.setDiscPrice(product.getPriceOnPack() * 0.9);
		
		
		return productRepository.save(product1);
		
	}
	
	public MeasuredProduct createNewMesuredProduct(String id) {
		
		Product product = productRepository.findById(id);
		MeasuredProduct newMeasuredProduct = new MeasuredProduct();
		  newMeasuredProduct.setId("*"+product.getId());
		  newMeasuredProduct.setDescription(product.getDescription());
		  newMeasuredProduct.setTotalQty(Double.parseDouble(product.getMesurmentSize()));
		  newMeasuredProduct.setPrice(product.getPrice()/newMeasuredProduct.getTotalQty());
		  newMeasuredProduct.setPriceOnPack(product.getPrice()/newMeasuredProduct.getTotalQty());
	      newMeasuredProduct.setTotalWorth(product.getPrice());
	      
	       product.setTotalQty(product.getTotalQty()-1);
		    mpRepository.save(newMeasuredProduct);
		    
		    productRepository.save(product);
		    newQtyToProduct(product, -1.00, LocalDate.now(),product.getTotalQty() ,"Qty on first import");
		    newQtyToMeasuredProduct(newMeasuredProduct, Double.parseDouble(product.getMesurmentSize()), LocalDate.now(), newMeasuredProduct.getTotalQty(),"Added from Product on first creation");
	         	return mpRepository.save(newMeasuredProduct);
	}
	public MeasuredProduct createGroupMeasuredProduct(MeasuredProduct product) {
		
		MeasuredProduct newMeasuredProduct = new MeasuredProduct();
		  newMeasuredProduct.setId(product.getId());
		  newMeasuredProduct.setDescription(product.getDescription());
		  newMeasuredProduct.setTotalQty(0.00);
		  newMeasuredProduct.setPrice(0.00);
		  newMeasuredProduct.setPriceOnPack(0.00);
	      newMeasuredProduct.setTotalWorth(0.00);
	      
            	return mpRepository.save(newMeasuredProduct);
    }
	
	public PaintMix createNewPaintMix(PaintMix mix) {
		
		PaintMix paintMix = new PaintMix();
		  paintMix.setDescription(mix.getDescription());
		  paintMix.setWeight(mix.getWeight());
		  paintMix.setWorth(mix.getWorth());
		       return mixRepository.save(paintMix);
	}
	
	 public QtyHistory addingQty(String id, QtyHistory history) {
		
		Product product = productRepository.findById(id);
		 product.setTotalQty(product.getTotalQty()+history.getQty());
		 productRepository.save(product);
		 newQtyToProduct(product, history.getQty(),LocalDate.now(),product.getTotalQty(),"New Arrived QTY");
		       return qhRepository.save(history);
	}
	
	public MeasuredProduct addQtyToMeasuredProducts(String id,Double number, String paint) {
		
		MeasuredProduct mp = mpRepository.findById(id);
		
		if(mp.getDescription().contains("Akril") || mp.getDescription().contains("Base")||mp.getDescription().contains("Filler")) {
			List<Product> products = new ArrayList<>();
			List<Product> productss = productRepository.findAll();
			for (Product product : productss) {
				if(product.getCategory().getCategoryName().equals(mp.getDescription())) {
					products.add(product);
				}
			}
			for (Product product : products) {
				if(product.getDescription().contains(paint)) {
					Product product1 = productRepository.findById(product.getId());
					PaintMix mix = mixRepository.findByDescription(paint);
					mp.setTotalQty(mp.getTotalQty()+(number * Double.valueOf(mix.getWeight())));
					mp.setTotalWorth(mp.getTotalWorth()+(number * mix.getWorth()));
					mpRepository.save(mp);
					mp.setPrice(mp.getTotalWorth()/mp.getTotalQty());
					mp.setPriceOnPack(mp.getTotalWorth()/mp.getTotalQty());
					product.setTotalQty(product1.getTotalQty()-number);
					productRepository.save(product1);
					newQtyToProduct(product1, -number, LocalDate.now() ,product1.getTotalQty(),"Added to measured product");
				    newQtyToMeasuredProduct(mp, number * Double.parseDouble(product1.getMesurmentSize()), LocalDate.now(), mp.getTotalQty(),"Added from Product");

					return mpRepository.save(mp);
				}
			}
		}
		String str = mp.getId().substring(1);
		Product product = productRepository.findById(str);
		mp.setTotalQty(mp.getTotalQty()+(number * Double.parseDouble(product.getMesurmentSize())));
		mp.setTotalWorth(mp.getTotalWorth()+(number*product.getPrice()));
		mpRepository.save(mp);
		
		product.setTotalQty(product.getTotalQty()-number);
		productRepository.save(product);
		newQtyToProduct(product, -number, LocalDate.now() ,product.getTotalQty(),"Added to measured product");
	    newQtyToMeasuredProduct(mp, number * Double.parseDouble(product.getMesurmentSize()), LocalDate.now(), mp.getTotalQty(),"Added from Product");

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
	
	public  void newQtyToProduct(Product product,Double qty,LocalDate date, Double newQty, String changeMadeFrom) {
		QtyHistory qh = new QtyHistory( product, qty, date, newQty, changeMadeFrom);
		  qhRepository.save(qh);
	}
	
	public void newQtyToMeasuredProduct(MeasuredProduct measuredProduct, Double qty, LocalDate date, Double newQty, String changeMadeFrom) {
		MeasuredProductQtyHistory history = new MeasuredProductQtyHistory(measuredProduct, qty, date, newQty,changeMadeFrom);
		mpqhRepository.save(history);
	}
	
    public List<QtyHistory> productsHistoryOnPeriod(String start,String end){
		
    	LocalDate d1 = LocalDate.parse(start);
    	LocalDate d2 = LocalDate.parse(end);
        	if(end.isEmpty()) {
        		d2 = LocalDate.now();
        }
    		List<QtyHistory> products = qhRepository.findByDateBetween(d1,d2);
    		
    		return products;
    	}
    public void deleteProduct(String id) {
    	Product product = productRepository.findById(id);
    	product.setCategory(null);
    	product.setDescription(null);
    	product.setDiscPrice(null);
    	product.setManufacturer(null);
    	product.setMesurmentSize(null);
    	product.setPrice(null);
    	product.setPriceOnPack(null);
    	product.setTotalQty(null);
    	productRepository.save(product);
    	
    }
	
}
