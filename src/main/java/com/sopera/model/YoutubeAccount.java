package com.sopera.model;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
public class YoutubeAccount extends SocialMediaAccount{

    private int subscribers;
    private long totalViews;
    private int totalVideos;

    @Override
    public void fetchLatestData() {
        System.out.println("fetching youtube data");
    }

}
