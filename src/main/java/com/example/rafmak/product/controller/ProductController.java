package com.example.rafmak.product.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.rafmak.product.entity.Product;
import com.example.rafmak.product.entity.QtyHistory;
import com.example.rafmak.product.repository.CategoryRepository;
import com.example.rafmak.product.repository.ManufacturerRepository;
import com.example.rafmak.product.repository.MeasuredProductRepository;
import com.example.rafmak.product.repository.ProductRepository;
import com.example.rafmak.product.repository.QtyHistoryRepository;
import com.example.rafmak.product.service.ProductService;

@Controller
public class ProductController {
	
	@Autowired
	ProductRepository productRepository;
	@Autowired
	ProductService productService;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	ManufacturerRepository manufacturerRepository;
	@Autowired
	MeasuredProductRepository mpRepository;
	@Autowired
	QtyHistoryRepository qhRepository;
	
	@GetMapping("/products")
	public String getAllProducts(Model model) {
		
	      model.addAttribute("listProducts", productRepository.findAll());
		
		return "all_products";
	}
	
	
	@GetMapping("/addProduct")
	public String getNewProductForm(Model model) {
	
		 model.addAttribute("product", new Product());
		 model.addAttribute("categories", categoryRepository.findAll());
		 model.addAttribute("manufacturers", manufacturerRepository.findAll());
		
		       return "addNewProduct";
		
	}
	
	@PostMapping("/addProduct")
	public String saveNewProduct(@ModelAttribute("product")Product product) {
		
		productService.saveProduct(product);
		
		       return "redirect:/";
	}
	
	@GetMapping("/addQuantity/{id}")
	public String getQtyForm(Model model,@PathVariable("id")String id) {
		
		model.addAttribute("qtyHistory", new QtyHistory());
		model.addAttribute("product", productRepository.findById(id));
		
		return "addQtyToProduct";
		
	}
	
	@PostMapping("/addQuantity/{id}")
	public String addQuantity(@PathVariable("id")String id,@ModelAttribute("qtyHistory")QtyHistory qtyHistory) {
		
		productService.addingQty(id, qtyHistory);
		
		return "redirect:/";
		
	}
	
	
	
	@GetMapping("/createMeasuredProduct/{id}")
	public String createMProduct(@PathVariable("id") String id) {
		
		productService.createNewMesuredProduct(id);
		
		return "redirect:/";
		
	}
	
	@GetMapping("/allMesuredProducts")
	public String findAllMeasuredProducts(Model model) {
		
		model.addAttribute("allMeasuredPr", mpRepository.findAll());
		
		return "allMeasProducts";
		
	}
	
	@GetMapping("addQtyToMP/{id}")
	public String getQtyToMpForm(Model model , @PathVariable("id") String id ,@Param(value = "number")Integer number) {
	
	
		model.addAttribute("product", mpRepository.findById(id));
		
		return "addQtyToMeasuredProduct";
	}
	
	@PostMapping("addQtyToMP/{id}")
	public String addQtyToMProducts(@PathVariable("id")String id,@Param(value = "number")Double number) {
		
		productService.addQtyToMeasuredProducts(id, number);
		
		return "redirect:/";
	}
	
	@GetMapping("/productsQtyHistory")
	public String productHistory(Model model,@Param(value = "pid")String pid) {
		
		List<QtyHistory> history = productService.findQtyByPid(pid);
		
		model.addAttribute("history", history);
	
		return "productHistoryPage";
		
	}
}
