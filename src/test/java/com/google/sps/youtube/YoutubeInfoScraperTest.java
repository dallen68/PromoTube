// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.youtube;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.any;

import java.util.ArrayList;
import java.util.Arrays;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelContentDetails.RelatedPlaylists;
import com.google.api.services.youtube.model.ChannelContentDetails;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.YouTube;
import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/** */
@RunWith(MockitoJUnitRunner.class)
public final class YoutubeInfoScraperTest {

  private YoutubeInfoScraper scrape;
  private ChannelListResponse mockResponse;
  private YoutubeResponse apiResponse; 
    
  @Before
  public void setUp() {
    apiResponse = mock(YoutubeResponse.class);
    scrape = scrape.setYoutubeAndGetNewInstance(
        new YouTube.Builder(new NetHttpTransport(),
        JacksonFactory.getDefaultInstance(),
        null)
        .setApplicationName("APPLICATION_NAME")
        .setYouTubeRequestInitializer(new YouTubeRequestInitializer("API_KEY"))
        .build());
  }

  @Test
  public void scrapeChannelUploadPlaylist_nonExistentChannelId() throws Exception{
      ChannelListResponse mockResponse = new ChannelListResponse();
      when(apiResponse.getYoutubeChannelListResponse("CHANNELID_THAT_DOES_NOT_EXIST")).thenReturn(mockResponse.setItems(null));
      String actual = scrape.scrapeChannelUploadPlaylist(apiResponse, "CHANNELID_THAT_DOES_NOT_EXIST");
      Assert.assertEquals("null", actual);
  }

 @Test
  public void scrapeChannelUploadPlaylist_emptyList() throws Exception{
      ChannelListResponse mockResponse = new ChannelListResponse();
      when(apiResponse.getYoutubeChannelListResponse("CHANNELID")).thenReturn(mockResponse.setItems(Arrays.asList()));
      String actual = scrape.scrapeChannelUploadPlaylist(apiResponse, "CHANNELID");
      Assert.assertEquals("null", actual);
  }

  @Test
  public void scrapeChannelUploadPlaylist_channelExists() throws Exception{
      mockResponse = new ChannelListResponse();
      Channel channel = new Channel();
      channel.setContentDetails(new ChannelContentDetails().setRelatedPlaylists(
          new RelatedPlaylists().setUploads("UPLOAD_ID")));
      mockResponse.setItems(Arrays.asList(channel));
      when(apiResponse.getYoutubeChannelListResponse("CHANNELID_THAT_EXISTS")).thenReturn(mockResponse);
      String actual = scrape.scrapeChannelUploadPlaylist(apiResponse, "CHANNELID_THAT_EXISTS");
      Assert.assertEquals("UPLOAD_ID", actual);
  }

  @Test (expected = IOException.class)
  public void scrapeChannelUploadPlaylist_IOException() throws Exception{
      when(apiResponse.getYoutubeChannelListResponse("IOEXCEPTION")).thenThrow(IOException.class);
      String actual = scrape.scrapeChannelUploadPlaylist(apiResponse, "IOEXCEPTION");
  }
}