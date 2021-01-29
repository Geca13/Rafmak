package com.example.rafmak;



import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import com.example.rafmak.product.entity.Category;
import com.example.rafmak.product.entity.Manufacturer;
import com.example.rafmak.product.entity.ManufacturerAddress;
import com.example.rafmak.product.repository.CategoryRepository;
import com.example.rafmak.product.repository.ManufacturerAddressRepository;
import com.example.rafmak.product.repository.ManufacturerRepository;
import com.example.rafmak.users.entity.Country;
import com.example.rafmak.users.entity.Role;
import com.example.rafmak.users.entity.RoleName;
import com.example.rafmak.users.repository.CountryRepository;
import com.example.rafmak.users.repository.RoleRepository;




@SpringBootApplication
public class RafmakApplication {

	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	CountryRepository countryRepository;
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	ManufacturerRepository manufacturerRepository;
	
	@Autowired
	ManufacturerAddressRepository maRepository;
	
	public static void main(String[] args) {
		SpringApplication.run(RafmakApplication.class, args);
		
		
	}
		@PostConstruct
		public void init() {
			
			try {
				roleRepository.save(new Role(1, RoleName.ROLE_ADMIN));
				roleRepository.save(new Role(2, RoleName.ROLE_EMPLOYEE));
				roleRepository.save(new Role(3, RoleName.ROLE_OWNER));
		
			    countryRepository.save(new Country(1, "Macedonia"));
				countryRepository.save(new Country(2, "Finland"));
				countryRepository.save(new Country(3, "Germany"));
				countryRepository.save(new Country(4, "Nederlands"));
				countryRepository.save(new Country(5, "Belgium"));
				
				categoryRepository.save(new Category(1,"Coats"));
				categoryRepository.save(new Category(2,"Hardeners"));
				categoryRepository.save(new Category(3,"Thinners"));
				categoryRepository.save(new Category(4,"Paints"));	
				
				
				
				maRepository.save(new ManufacturerAddress (1,"Zuiveringweg","89","Lelystad","8203",countryRepository.findById(4).get())); 
				maRepository.save(new ManufacturerAddress (2,"Pensalantie","210","Jeppo","66850 ",countryRepository.findById(2).get()));	
				maRepository.save(new ManufacturerAddress (3,"Baarbeek","2","Zwijndrecht","2070 ",countryRepository.findById(5).get()));	

				manufacturerRepository.save(new Manufacturer(1,"DeBeer","debeer@debeer.com","+11 111 1111",maRepository.findById(1).get()));
				manufacturerRepository.save(new Manufacturer(2,"Mirka","mirka@mirka.com","+22 222 2222",maRepository.findById(2).get()));
				manufacturerRepository.save(new Manufacturer(3,"Finixa","finixa@finixa.com","+33 333 3333",maRepository.findById(3).get()));

				

			} catch (Exception e) {
				System.err.println(e);
			}
		
		
	}

}
