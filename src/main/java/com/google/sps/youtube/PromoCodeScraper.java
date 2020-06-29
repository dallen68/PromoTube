
package com.google.sps.youtube;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelContentDetails;
import com.google.api.services.youtube.model.ChannelContentDetails.RelatedPlaylists;
import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.lang.IllegalArgumentException;



public class PromoCodeScraper {

  private static final String API_KEY = "";
  private static final String APPLICATION_NAME = "promotube";

  private YouTube youtube;

  private PromoCodeScraper(YouTube youtube){
      this.youtube = youtube;
  }

  private PromoCodeScraper(){}

  public String scrapeChannelUploadPlaylist(String channelId){
      return "";
  }

  public String scrapeVideosFromChannel(String uploadId){
      return "";
  }

  public static PromoCodeScraper getInstance(){
      return new PromoCodeScraper();
  }

  public static PromoCodeScraper newInstance(YouTube youtube){
      return new PromoCodeScraper(youtube);
  }
}