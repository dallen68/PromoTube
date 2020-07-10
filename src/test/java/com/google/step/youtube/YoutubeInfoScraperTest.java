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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.ChannelContentDetails;
import com.google.api.services.youtube.model.ChannelContentDetails.RelatedPlaylists;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.YouTube.Channels;
import com.google.api.services.youtube.YouTube.PlaylistItems;
import com.google.api.services.youtube.YouTube;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class YoutubeInfoScraperTest {

    private YoutubeInfoScraper scraper;
    private ChannelListResponse mockChannelResponse;
    private Channels.List mockListChannels;
    private PlaylistItems.List mockListPlaylistItems;
    private PlaylistItemListResponse mockPlaylistResponse;

    private static final String CHANNEL_ID_NONEXISTENT = "CHANNEL_ID_NONEXISTENT";
    private static final String UPLOAD_ID_NONEXISTENT = "UPLOAD_ID_NONEXISTENT";
    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static final String UPLOAD_ID = "UPLOAD_ID";
    private static final String VIDEO_ID = "VIDEO_ID";
    private static final String IOEXCEPTION = "IOEXCEPTION";
    private static final long MOCK_DATE = 0L;

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
        Optional<String> actual = scraper.scrapeChannelUploadPlaylist(CHANNEL_ID_NONEXISTENT);
        assertThat(actual.isPresent(), equalTo(false));
    }

    @Test
    public void scrapeChannelUploadPlaylist_emptyList() throws IOException {
        mockChannelResponse = new ChannelListResponse();
        when(mockListChannels.execute()).thenReturn(mockChannelResponse.setItems(Arrays.asList()));
        Optional<String> actual = scraper.scrapeChannelUploadPlaylist(CHANNEL_ID);
        assertThat(actual.isPresent(), equalTo(false));
    }

    @Test
    public void scrapeChannelUploadPlaylist_channelExists() throws IOException {
        mockChannelResponse = new ChannelListResponse();
        Channel channel = new Channel();
        channel.setContentDetails(
                new ChannelContentDetails().setRelatedPlaylists(new RelatedPlaylists().setUploads(UPLOAD_ID)));
        mockChannelResponse.setItems(Arrays.asList(channel));
        when(mockListChannels.execute()).thenReturn(mockChannelResponse);
        Optional<String> actual = scraper.scrapeChannelUploadPlaylist(CHANNEL_ID);
        assertThat(actual.get(), equalTo(UPLOAD_ID));
    }

    @Test
    public void scrapeChannelUploadPlaylist_IOException() throws IOException {
        when(mockListChannels.execute()).thenThrow(IOException.class);
        assertThrows(IOException.class, () -> scraper.scrapeChannelUploadPlaylist(IOEXCEPTION));
    }

    @Test
    public void scrapePlaylistItems_nonExistentUploadId() throws IOException {
        mockPlaylistResponse = new PlaylistItemListResponse();
        when(mockListPlaylistItems.execute()).thenReturn(mockPlaylistResponse.setItems(null));
        Optional<List<PlaylistItem>> actual = scraper.scrapePlaylistItems(UPLOAD_ID_NONEXISTENT);
        assertThat(actual.isPresent(), equalTo(false));
    }

    @Test
    public void scrapePlaylistItems_emptyList() throws IOException {
        mockPlaylistResponse = new PlaylistItemListResponse();
        when(mockListPlaylistItems.execute()).thenReturn(mockPlaylistResponse.setItems(Arrays.asList()));
        Optional<List<PlaylistItem>> actual = scraper.scrapePlaylistItems(UPLOAD_ID);
        assertThat(actual.isPresent(), equalTo(false));
    }

    @Test
    public void scrapePlaylistItems_uploadIdExists() throws IOException {
        mockPlaylistResponse = new PlaylistItemListResponse();
        mockPlaylistResponse.setItems(Arrays.asList(new PlaylistItem(), new PlaylistItem()));
        when(mockListPlaylistItems.execute()).thenReturn(mockPlaylistResponse);
        Optional<List<PlaylistItem>> actual = scraper.scrapePlaylistItems(UPLOAD_ID);
        assertThat(actual.get(), equalTo(mockPlaylistResponse.getItems()));
    }

    @Test
    public void scrapePlaylistItems_IOException() throws IOException {
        when(mockListPlaylistItems.execute()).thenThrow(IOException.class);
        assertThrows(IOException.class, () -> scraper.scrapePlaylistItems(IOEXCEPTION));
    }

    @Test
    public void scrapePromoCodesFromPlaylist_nonExistentUploadId() throws IOException {
        mockPlaylistResponse = new PlaylistItemListResponse();
        when(mockListPlaylistItems.execute()).thenReturn(mockPlaylistResponse.setItems(null));
        Optional<List<PromoCode>> actual = scraper.scrapePromoCodesFromPlaylist(UPLOAD_ID_NONEXISTENT);
        assertThat(actual.isPresent(), equalTo(false));
    }

    @Test
    public void scrapePromoCodesFromPlaylist_oneItem() throws IOException {
        mockPlaylistResponse = new PlaylistItemListResponse();
        String description = "Get 20% off your first monthly box and enter the code RO at checkout!";
        mockPlaylistResponse.setItems(
                Arrays.asList(new PlaylistItem().setSnippet(new PlaylistItemSnippet().setDescription(description)
                        .setPublishedAt(new DateTime(MOCK_DATE)).setResourceId(new ResourceId().setVideoId(VIDEO_ID)))));
        when(mockListPlaylistItems.execute()).thenReturn(mockPlaylistResponse);
        Optional<List<PromoCode>> actual = scraper.scrapePromoCodesFromPlaylist(UPLOAD_ID);
        assertThat(actual.get(), equalTo(Arrays.asList(PromoCode.create("RO", VIDEO_ID, new Date(MOCK_DATE)))));
    }

    @Test
    public void scrapePromoCodesFromPlaylist_noItems() throws IOException {
        mockPlaylistResponse = new PlaylistItemListResponse();
        when(mockListPlaylistItems.execute()).thenReturn(mockPlaylistResponse.setItems(Arrays.asList()));
        Optional<List<PromoCode>> actual = scraper.scrapePromoCodesFromPlaylist(UPLOAD_ID);
        assertThat(actual.isPresent(), equalTo(false));
    }

    /** Multiple items with one item having no promo-code in the description. */
    @Test
    public void scrapePromoCodesFromPlaylist_multipleItemsSomeCodesFound() throws IOException {
        mockPlaylistResponse = new PlaylistItemListResponse();
        String description1 = "Get 20% off your first monthly box when you sign up at http://boxofawesome.com";
        String description2 = "Use code LINUS and get 25% off GlassWire";
        String descriptionWithNoCode = "Check out our UPDATED version which has all the NEW ELEMENTS here: "
                + "\n https://youtu.be/rz4Dd1I_fX0  The TEETH Song (Memorize Every Tooth): https://youtu.be/PI3hne8C8rU"
                + "\nDownload on ITUNES: http://bit.ly/12AeW99 ";
        mockPlaylistResponse.setItems(Arrays.asList(
                new PlaylistItem().setSnippet(new PlaylistItemSnippet().setDescription(description1)
                        .setPublishedAt(new DateTime(MOCK_DATE)).setResourceId(new ResourceId().setVideoId(VIDEO_ID))),
                new PlaylistItem().setSnippet(new PlaylistItemSnippet().setDescription(description2)
                        .setPublishedAt(new DateTime(MOCK_DATE)).setResourceId(new ResourceId().setVideoId(VIDEO_ID))),
                new PlaylistItem().setSnippet(new PlaylistItemSnippet().setDescription(descriptionWithNoCode)
                        .setPublishedAt(new DateTime(MOCK_DATE))
                        .setResourceId(new ResourceId().setVideoId(VIDEO_ID)))));
        when(mockListPlaylistItems.execute()).thenReturn(mockPlaylistResponse);
        Optional<List<PromoCode>> actual = scraper.scrapePromoCodesFromPlaylist(UPLOAD_ID);
        assertThat(actual.get(), equalTo(Arrays.asList(PromoCode.create("http://boxofawesome.com", VIDEO_ID, new Date(MOCK_DATE)),
                PromoCode.create("LINUS", VIDEO_ID, new Date(MOCK_DATE)))));
    }

    @Test
    public void scrapePromoCodesFromPlaylist_oneItemNoCodeFound() throws IOException {
        mockPlaylistResponse = new PlaylistItemListResponse();
        String descriptionWithNoCode = "Check out our UPDATED version which has all the NEW ELEMENTS here: "
                + "\n https://youtu.be/rz4Dd1I_fX0  The TEETH Song (Memorize Every Tooth): https://youtu.be/PI3hne8C8rU"
                + "\nDownload on ITUNES: http://bit.ly/12AeW99 ";
        mockPlaylistResponse.setItems(Arrays
                .asList(new PlaylistItem().setSnippet(new PlaylistItemSnippet().setDescription(descriptionWithNoCode)
                        .setPublishedAt(new DateTime(MOCK_DATE)).setResourceId(new ResourceId().setVideoId(VIDEO_ID)))));
        when(mockListPlaylistItems.execute()).thenReturn(mockPlaylistResponse);
        Optional<List<PromoCode>> actual = scraper.scrapePromoCodesFromPlaylist(UPLOAD_ID);
        assertThat(actual.get().isEmpty(), equalTo(true));
    }

    @Test
    public void scrapePromoCodesFromPlaylist_IOException() throws IOException {
        when(mockListPlaylistItems.execute()).thenThrow(IOException.class);
        assertThrows(IOException.class, () -> scraper.scrapePromoCodesFromPlaylist(IOEXCEPTION));
    }
}
