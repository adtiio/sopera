package com.sopera.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
public class Creator extends UserRole{

    private String bio;

    @ElementCollection
    @CollectionTable(name = "creator_categories", joinColumns = @JoinColumn(name = "user_role_id"))
    @Column(name = "category")
    private List<String> categories=new ArrayList<>();

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialMediaAccount> socialMediaAccounts;
}
