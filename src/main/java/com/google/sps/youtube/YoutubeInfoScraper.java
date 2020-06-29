
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
import com.google.sps.youtube.YoutubeResponse;
import java.util.Properties; 
import java.io.FileInputStream;


public class YoutubeInfoScraper implements YoutubeResponse {

  // TODO: Add seperate file to hold API Key  
  private String API_KEY = "";
  private static final String APPLICATION_NAME = "promotube";

  private YouTube youtube;

  private YoutubeInfoScraper(YouTube youtube){
      this.youtube = youtube;
  }

  private YoutubeInfoScraper(){
    youtube = new YouTube.Builder(new NetHttpTransport(),
        JacksonFactory.getDefaultInstance(),
        null)
        .setApplicationName(APPLICATION_NAME)
        .setYouTubeRequestInitializer(new YouTubeRequestInitializer(API_KEY))
        .build();
  }

  /**
  * @param ytResponse Pass in this object or any other object that implements YoutubeResponse.
  * @param channelId Id of a channel.  Can be found in channel's urls.
  * e.g. https://www.youtube.com/channel/<CHANNEL-ID>
  * @return string of the channel's upload playlist id.  Null if id is invalid or 
  * no items were found.
  */
  public String scrapeChannelUploadPlaylist(YoutubeResponse ytResponse, String channelId)
    throws IOException{
        ChannelListResponse response = ytResponse.getYoutubeChannelListResponse(channelId);
        if(response==null || response.getItems()==null || response.getItems().size()==0){
            return "null";
        }
        List<Channel> channelsInfo = response.getItems();
        return channelsInfo.get(0).getContentDetails().getRelatedPlaylists().getUploads();
   }

  @Override
  public ChannelListResponse getYoutubeChannelListResponse(String channelId)
    throws IOException{ 
        return youtube.channels().list("contentDetails").setId(channelId).execute();
   }

  public static YoutubeInfoScraper getNewInstance(){
      return new YoutubeInfoScraper();
  }

  public static YoutubeInfoScraper setYoutubeAndGetNewInstance(YouTube youtube){
      return new YoutubeInfoScraper(youtube);
  }
}
