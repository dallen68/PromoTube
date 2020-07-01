
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
import java.util.Optional;

/**
*  Scrapes a channel's upload playlist and scrapes the channel's videos + descriptions 
*/
public class YoutubeInfoScraper {

  // TODO: Add seperate file to hold API Key  
  private static final String API_KEY = "";
  private static final String APPLICATION_NAME = "promotube";

  private final YouTube youTubeClient;

  public YoutubeInfoScraper(YouTube youTubeClient) {
      this.youTubeClient = youTubeClient;
  }

  public YoutubeInfoScraper() {
    this(new YouTube.Builder(new NetHttpTransport(),
        JacksonFactory.getDefaultInstance(),
        /* httpRequestInitializer= */ null)
        .setApplicationName(APPLICATION_NAME)
        .setYouTubeRequestInitializer(new YouTubeRequestInitializer(API_KEY))
        .build());
  }

  /**
  * @param ytResponse Pass in this object or any other object that implements YoutubeResponse.
  * @param channelId Id of a channel.  Can be found in channel's urls.
  * e.g. https://www.youtube.com/channel/<CHANNEL-ID>
  * @return an optional string of the channel's upload playlist id. The optional will be empty 
  * if id is invalid or no items were found.
  */
  public Optional<String> scrapeChannelUploadPlaylist(String channelId)
    throws IOException {
        ChannelListResponse response = youTubeClient.channels().list("contentDetails").setId(channelId).execute();
        // getItems() return null when no items match the criteria (channelId). 
        if (response.getItems()==null || response.getItems().isEmpty()){
            return Optional.empty();
        }
        List<Channel> channelsInfo = response.getItems();
        // Since we are only requesting one channel-id, we only get one item back.  
        return Optional.of(channelsInfo.get(0).getContentDetails().getRelatedPlaylists().getUploads());
   }
}
