package com.sopera.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sopera.model.YoutubeAccount;

@Repository
public interface YoutubeAccountRepository extends JpaRepository<YoutubeAccount,Long>{

}
