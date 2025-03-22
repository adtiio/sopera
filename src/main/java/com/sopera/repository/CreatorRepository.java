package com.sopera.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sopera.model.Creator;

public interface CreatorRepository extends JpaRepository<Creator,Long>{
    
    @Query("SELECT c FROM Creator c JOIN c.categories cat WHERE cat = :category")
    List<Creator> findByCategory(@Param("category") String category);

}
