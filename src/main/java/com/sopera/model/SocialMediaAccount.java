package com.sopera.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sopera.model.enums.SocialMediaPlatform;

import jakarta.persistence.*;

import lombok.Data;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public abstract class SocialMediaAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String username;

    @Enumerated( EnumType.STRING)
    SocialMediaPlatform platform;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="creator_id", nullable = false)
    @JsonIgnore
    private Creator creator;

    public abstract void fetchLatestData();
}
