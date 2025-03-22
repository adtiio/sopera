package com.sopera.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchListResponse;

@Service
public class YoutubeService {
    
    private static final String APPLICATION_NAME = "sopera";
    private static final JsonFactory JSON_FACTORY=new GsonFactory();
    private static final HttpTransport httpTransport=new NetHttpTransport();

    private final String apiKey="api_key";

    public YouTube getServices(){
        return new YouTube.Builder(httpTransport, JSON_FACTORY, request->{}).setApplicationName(APPLICATION_NAME).build();
    }

    public Map<String, Object> getChannelDetails(String channelName) throws IOException{
        YouTube youTube=getServices();
        String channelId=getChannelIdFromName(channelName, youTube);

        YouTube.Channels.List request=youTube.channels().list("snippet,statistics,brandingSettings").setId(channelId).setKey(apiKey);

        ChannelListResponse response=request.execute();
        List<Channel> channels=response.getItems();

        if(channels.isEmpty()) return null;
        
        Channel channel= channels.get(0);

        return Map.of(
            "channelName", channel.getSnippet().getTitle(),
            "description", channel.getSnippet().getDescription(),
            "subscribers", channel.getStatistics().getSubscriberCount(),
            "totalViews", channel.getStatistics().getViewCount(),
            "totalVideo", channel.getStatistics().getVideoCount(),
            "bannerImage", channel.getBrandingSettings().getImage().getBannerExternalUrl()
                        
        );
    }

    public String getChannelIdFromName(String channelName, YouTube youTube) throws IOException{
        
        YouTube.Search.List searchRequest=youTube.search().list("snippet").setQ(channelName).setType("channel").setMaxResults(1L).setKey(apiKey);

        SearchListResponse searchResponse=searchRequest.execute();

        if(!searchResponse.getItems().isEmpty()){
            return searchResponse.getItems().get(0).getId().getChannelId();
        }
        return null;
    }


}
