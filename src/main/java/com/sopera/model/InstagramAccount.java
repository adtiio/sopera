package com.sopera.model;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
public class InstagramAccount extends SocialMediaAccount{

    private int followers;
    private int followings;
    private int totalPosts;

    @Override
    public void fetchLatestData() {
        System.out.println("fetching instagram data");
    }
}
