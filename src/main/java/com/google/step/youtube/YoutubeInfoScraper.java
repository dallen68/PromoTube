
package com.google.step.youtube;

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
import com.google.step.youtube.YoutubeResponse;
import java.util.Properties; 
import java.util.Optional;
import java.io.FileInputStream;

/**
*  Scrapes a channel's upload playlist and scrapes the channel's videos + descriptions 
*/
public class YoutubeInfoScraper implements YoutubeResponse {

  // TODO: Add seperate file to hold API Key  
  private static final String API_KEY = "";
  private static final String APPLICATION_NAME = "promotube";

  private YouTube youTubeClient;

  public YoutubeInfoScraper(YouTube youTubeClient) {
      this.youTubeClient = youTubeClient;
  }

  public YoutubeInfoScraper() {
    this(new YouTube.Builder(new NetHttpTransport(),
        JacksonFactory.getDefaultInstance(),
        null)
        .setApplicationName(APPLICATION_NAME)
        .setYouTubeRequestInitializer(new YouTubeRequestInitializer(API_KEY))
        .build());
  }

  /**
  * @param ytResponse Pass in this object or any other object that implements YoutubeResponse.
  * @param channelId Id of a channel.  Can be found in channel's urls.
  * e.g. https://www.youtube.com/channel/<CHANNEL-ID>
  * @return string of the channel's upload playlist id.  Null if id is invalid or 
  * no items were found.
  */
  public Optional<String> scrapeChannelUploadPlaylist(YoutubeResponse ytResponse, String channelId)
    throws IOException {
        ChannelListResponse response = ytResponse.getYoutubeChannelListResponse(channelId);
        if (response==null || response.getItems()==null || response.getItems().isEmpty()){
            return Optional.empty();
        }
        List<Channel> channelsInfo = response.getItems();
        return Optional.of(channelsInfo.get(0).getContentDetails().getRelatedPlaylists().getUploads());
   }

  @Override
  public ChannelListResponse getYoutubeChannelListResponse(String channelId)
    throws IOException{ 
        return youTubeClient.channels().list("contentDetails").setId(channelId).execute();
   }
}
