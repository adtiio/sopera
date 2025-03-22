package com.sopera.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sopera.model.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole,Long>{
    
}
