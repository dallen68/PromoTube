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

package com.google.step.youtube;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.anyString;

import java.util.Arrays;
import java.util.Optional;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelContentDetails.RelatedPlaylists;
import com.google.api.services.youtube.model.ChannelContentDetails;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Channels;
import com.google.api.services.youtube.YouTube.PlaylistItems;
import com.google.api.services.youtube.model.PlaylistItem;
import java.util.List;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class YoutubeInfoScraperTest {

  private YoutubeInfoScraper scraper;
  private ChannelListResponse mockChannelResponse; 
  private Channels.List mockListChannels;
  private PlaylistItems.List mockListPlaylistItems;
  private PlaylistItemListResponse mockPlaylistResponse;
  
  private static final String CHANNEL_ID = "CHANNEL_ID";
  private static final String CHANNEL_ID_THAT_DOES_NOT_EXIST = "CHANNEL_ID_THAT_DOES_NOT_EXIST";
  private static final String CHANNEL_ID_THAT_EXISTS = "CHANNEL_ID_THAT_EXISTS";
  private static final String UPLOAD_ID_THAT_DOES_NOT_EXIST = "UPLOAD_ID_THAT_DOES_NOT_EXIST";
  private static final String UPLOAD_ID = "UPLOAD_ID";
  private static final String IOEXCEPTION = "IOEXCEPTION";
  
  @Before
  public void setUp() throws IOException {
      YouTube mockYouTubeClient = mock(YouTube.class);

      Channels mockChannels = mock(YouTube.Channels.class);
      mockListChannels = mock(YouTube.Channels.List.class);
      when(mockYouTubeClient.channels()).thenReturn(mockChannels);
      when(mockChannels.list("contentDetails")).thenReturn(mockListChannels);
      when(mockListChannels.setId(anyString())).thenReturn(mockListChannels);

      PlaylistItems mockPlaylistItems = mock(YouTube.PlaylistItems.class);
      mockListPlaylistItems = mock(YouTube.PlaylistItems.List.class);
      when(mockYouTubeClient.playlistItems()).thenReturn(mockPlaylistItems);
      when(mockPlaylistItems.list("snippet")).thenReturn(mockListPlaylistItems);
      when(mockListPlaylistItems.setMaxResults(50L)).thenReturn(mockListPlaylistItems);
      when(mockListPlaylistItems.setPlaylistId(anyString())).thenReturn(mockListPlaylistItems);

      scraper = new YoutubeInfoScraper(mockYouTubeClient);
  }

  @Test
  public void scrapeChannelUploadPlaylist_nonExistentChannelId() throws IOException {
      mockChannelResponse = new ChannelListResponse();
      when(mockListChannels.execute()).thenReturn(mockChannelResponse.setItems(null));
      Optional<String> actual = scraper.scrapeChannelUploadPlaylist(CHANNEL_ID_THAT_DOES_NOT_EXIST);
      assertThat(false, equalTo(actual.isPresent()));
  }

 @Test
  public void scrapeChannelUploadPlaylist_emptyList() throws IOException {
      mockChannelResponse = new ChannelListResponse();
      when(mockListChannels.execute()).thenReturn(mockChannelResponse.setItems(Arrays.asList()));
      Optional<String> actual = scraper.scrapeChannelUploadPlaylist(CHANNEL_ID);
      assertThat(false, equalTo(actual.isPresent()));
  }

  @Test
  public void scrapeChannelUploadPlaylist_channelExists() throws IOException {
      mockChannelResponse = new ChannelListResponse();
      Channel channel = new Channel();
      channel.setContentDetails(new ChannelContentDetails().setRelatedPlaylists(
          new RelatedPlaylists().setUploads(UPLOAD_ID)));
      mockChannelResponse.setItems(Arrays.asList(channel));
      when(mockListChannels.execute()).thenReturn(mockChannelResponse);
      Optional<String> actual = scraper.scrapeChannelUploadPlaylist(CHANNEL_ID_THAT_EXISTS);
      assertThat(UPLOAD_ID, equalTo(actual.get()));
  }

  @Test 
  public void scrapeChannelUploadPlaylist_IOException() throws IOException {
      when(mockListChannels.execute()).thenThrow(IOException.class);
      assertThrows(IOException.class, () -> scraper.scrapeChannelUploadPlaylist(IOEXCEPTION));
  }

  @Test
  public void getPlaylistItems_nonExistentUploadId() throws IOException {
      mockPlaylistResponse = new PlaylistItemListResponse();
      when(mockListPlaylistItems.execute()).thenReturn(mockPlaylistResponse.setItems(null));
      Optional<List<PlaylistItem>> actual = scraper.getPlaylistItems(UPLOAD_ID_THAT_DOES_NOT_EXIST);
      assertThat(false, equalTo(actual.isPresent()));
  }

  @Test
  public void getPlaylistItems_emptyList() throws IOException {
      mockPlaylistResponse = new PlaylistItemListResponse();
      when(mockListPlaylistItems.execute()).thenReturn(mockPlaylistResponse.setItems(Arrays.asList()));
      Optional<List<PlaylistItem>> actual = scraper.getPlaylistItems(UPLOAD_ID);
      assertThat(false, equalTo(actual.isPresent()));
  }

  @Test
  public void getPlaylistItems_uploadIdExists() throws IOException {
      mockPlaylistResponse = new PlaylistItemListResponse();
      mockPlaylistResponse.setItems(Arrays.asList(new PlaylistItem(), new PlaylistItem()));
      when(mockListPlaylistItems.execute()).thenReturn(mockPlaylistResponse);
      Optional<List<PlaylistItem>> actual = scraper.getPlaylistItems(UPLOAD_ID);
      assertThat(mockPlaylistResponse.getItems(), equalTo(actual.get()));
  }

  @Test 
  public void getPlaylistItems_IOException() throws IOException {
      when(mockListPlaylistItems.execute()).thenThrow(IOException.class);
      assertThrows(IOException.class, () -> scraper.getPlaylistItems(IOEXCEPTION));
  }
}
