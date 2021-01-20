package com.example.rafmak.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rafmak.users.entity.Country;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {
	
    Country findByCountryName(String countryName);
	
	Boolean existsByCountryName(String countryName);

}
