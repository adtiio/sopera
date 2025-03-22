package com.sopera.model;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
public class Brand extends UserRole {
    private String companyName;
    private String industry;
}