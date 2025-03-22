package com.sopera.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sopera.model.Brand;

public interface BrandRepository extends JpaRepository<Brand,Long>{
    
}
