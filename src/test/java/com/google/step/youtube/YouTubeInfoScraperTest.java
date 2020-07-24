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
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.YouTube.Channels;
import com.google.api.services.youtube.YouTube.PlaylistItems;
import com.google.api.services.youtube.YouTube.Videos;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.YouTube.Search;
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
public final class YouTubeInfoScraperTest {

    private YouTubeInfoScraper scraper;
    private Channels.List mockListChannels;
    private Videos.List mockListVideos;
    private Search.List mockListSearch;
    private PlaylistItems.List mockListPlaylistItems;

    private static final String NONEXISTENT_CHANNEL_ID = "NONEXISTENT_CHANNEL_ID";
    private static final String NONEXISTENT_UPLOAD_ID = "NONEXISTENT_UPLOAD_ID";
    private static final String NONEXISTENT_USERNAME = "NONEXISTENT_USERNAME";
    private static final String NONEXISTENT_VIDEO_ID = "NONEXISTENT__VIDEO_ID";
    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static final String USERNAME = "USERNAME";
    private static final String UPLOAD_ID = "UPLOAD_ID";
    private static final String VIDEO_ID = "VIDEO_ID";
    private static final String VIDEO_TITLE = "VIDEO_TITLE";
    private static final String KEYWORD = "KEYWORD";
    private static final String NO_RESULTS_KEYWORD = "NO_RESULTS_KEYWORD";
    private static final List<String> EMPTY_VIDEO_ID_LIST = Arrays.asList();
    private static final List<String> VIDEO_ID_LIST = Arrays.asList();
    private static final long MAX_RESULTS = 100;
    private static final Date DATE = new Date(0L);

    @Before
    public void setUp() throws IOException {
        YouTube mockYouTubeClient = mock(YouTube.class);

        Channels mockChannels = mock(YouTube.Channels.class);
        mockListChannels = mock(YouTube.Channels.List.class);
        when(mockYouTubeClient.channels()).thenReturn(mockChannels);
        when(mockChannels.list("contentDetails")).thenReturn(mockListChannels);
        when(mockListChannels.setId(anyString())).thenReturn(mockListChannels);
        when(mockListChannels.setForUsername(anyString())).thenReturn(mockListChannels);

        PlaylistItems mockPlaylistItems = mock(YouTube.PlaylistItems.class);
        mockListPlaylistItems = mock(YouTube.PlaylistItems.List.class);
        when(mockYouTubeClient.playlistItems()).thenReturn(mockPlaylistItems);
        when(mockPlaylistItems.list("snippet")).thenReturn(mockListPlaylistItems);
        when(mockListPlaylistItems.setMaxResults(MAX_RESULTS)).thenReturn(mockListPlaylistItems);
        when(mockListPlaylistItems.setPlaylistId(anyString())).thenReturn(mockListPlaylistItems);

        Videos mockVideos = mock(YouTube.Videos.class);
        mockListVideos = mock(Videos.List.class);
        when(mockYouTubeClient.videos()).thenReturn(mockVideos);
        when(mockVideos.list("snippet")).thenReturn(mockListVideos);
        when(mockListVideos.setId(anyString())).thenReturn(mockListVideos);
        when(mockListVideos.setFields("items(id, snippet(publishedAt, title, description))"))
                .thenReturn(mockListVideos);

        Search mockSearch = mock(Search.class);
        mockListSearch = mock(Search.List.class);
        when(mockYouTubeClient.search()).thenReturn(mockSearch);
        when(mockSearch.list("snippet")).thenReturn(mockListSearch);
        when(mockListSearch.setMaxResults(MAX_RESULTS)).thenReturn(mockListSearch);
        when(mockListSearch.setQ(anyString())).thenReturn(mockListSearch);
    
        scraper = new YouTubeInfoScraper(mockYouTubeClient);
    }

    @Test
    public void scrapeChannelUploadPlaylist_nonExistentChannelId() throws IOException {
        ChannelListResponse testChannelResponse = new ChannelListResponse();
        when(mockListChannels.execute()).thenReturn(testChannelResponse);
        Optional<String> actual = scraper.scrapeChannelUploadPlaylist(NONEXISTENT_CHANNEL_ID);
        assertThat(actual.isPresent(), equalTo(false));
    }

    @Test
    public void scrapeChannelUploadPlaylist_channelExists() throws IOException {
        ChannelListResponse testChannelResponse = new ChannelListResponse();
        Channel channel = new Channel();
        channel.setContentDetails(
                new ChannelContentDetails().setRelatedPlaylists(new RelatedPlaylists().setUploads(UPLOAD_ID)));
        testChannelResponse.setItems(Arrays.asList(channel));
        when(mockListChannels.execute()).thenReturn(testChannelResponse);
        Optional<String> actual = scraper.scrapeChannelUploadPlaylist(CHANNEL_ID);
        assertThat(actual.get(), equalTo(UPLOAD_ID));
    }

    @Test
    public void scrapeUserUploadPlaylist_nonExistentUserName() throws IOException {
        ChannelListResponse testChannelResponse = new ChannelListResponse();
        when(mockListChannels.execute()).thenReturn(testChannelResponse);
        Optional<String> actual = scraper.scrapeUserUploadPlaylist(NONEXISTENT_USERNAME);
        assertThat(actual.isPresent(), equalTo(false));
    }

    @Test
    public void scrapeUserUploadPlaylist_userExists() throws IOException {
        ChannelListResponse testChannelResponse = new ChannelListResponse();
        Channel channel = new Channel();
        channel.setContentDetails(
                new ChannelContentDetails().setRelatedPlaylists(new RelatedPlaylists().setUploads(UPLOAD_ID)));
        testChannelResponse.setItems(Arrays.asList(channel));
        when(mockListChannels.execute()).thenReturn(testChannelResponse);
        Optional<String> actual = scraper.scrapeUserUploadPlaylist(USERNAME);
        assertThat(actual.get(), equalTo(UPLOAD_ID));
    }

    @Test
    public void scrapePlaylistItems_nonExistentUploadId() throws IOException {
        PlaylistItemListResponse testPlaylistResponse = new PlaylistItemListResponse();
        when(mockListPlaylistItems.execute()).thenReturn(testPlaylistResponse);
        Optional<List<PlaylistItem>> actual = scraper.scrapePlaylistItems(NONEXISTENT_UPLOAD_ID);
        assertThat(actual.isPresent(), equalTo(false));
    }

    @Test
    public void scrapePlaylistItems_uploadIdExists() throws IOException {
        PlaylistItemListResponse testPlaylistResponse = new PlaylistItemListResponse();
        testPlaylistResponse.setItems(Arrays.asList(new PlaylistItem(), new PlaylistItem()));
        when(mockListPlaylistItems.execute()).thenReturn(testPlaylistResponse);
        Optional<List<PlaylistItem>> actual = scraper.scrapePlaylistItems(UPLOAD_ID);
        assertThat(actual.get(), equalTo(testPlaylistResponse.getItems()));
    }

    @Test
    public void scrapePromoCodesFromPlaylist_nonExistentUploadId() throws IOException {
        PlaylistItemListResponse testPlaylistResponse = new PlaylistItemListResponse();
        when(mockListPlaylistItems.execute()).thenReturn(testPlaylistResponse);
        Optional<List<PromoCode>> actual = scraper.scrapePromoCodesFromPlaylist(NONEXISTENT_UPLOAD_ID);
        assertThat(actual.isPresent(), equalTo(false));
    }

    @Test
    public void scrapePromoCodesFromPlaylist_oneItem() throws IOException {
        PlaylistItemListResponse testPlaylistResponse = new PlaylistItemListResponse();
        String description = "Get 20% off your first monthly box and enter the code RO at checkout!";
        testPlaylistResponse.setItems(Arrays.asList(new PlaylistItem()
                .setSnippet(new PlaylistItemSnippet().setDescription(description).setPublishedAt(new DateTime(DATE))
                        .setTitle(VIDEO_TITLE).setResourceId(new ResourceId().setVideoId(VIDEO_ID)))));
        when(mockListPlaylistItems.execute()).thenReturn(testPlaylistResponse);
        Optional<List<PromoCode>> actual = scraper.scrapePromoCodesFromPlaylist(UPLOAD_ID);
        assertThat(actual.get(),
                equalTo(Arrays.asList(
                        PromoCode.create("RO", "Get 20% off your first monthly box and enter the code RO at checkout!",
                                VIDEO_ID, VIDEO_TITLE, DATE))));
    }

    @Test
    public void scrapePromoCodesFromPlaylist_noItems() throws IOException {
        PlaylistItemListResponse testPlaylistResponse = new PlaylistItemListResponse();
        when(mockListPlaylistItems.execute()).thenReturn(testPlaylistResponse);
        Optional<List<PromoCode>> actual = scraper.scrapePromoCodesFromPlaylist(UPLOAD_ID);
        assertThat(actual.isPresent(), equalTo(false));
    }

    /* Multiple items with one item having no promo-code in the description. */
    @Test
    public void scrapePromoCodesFromPlaylist_multipleItemsSomeCodesFound() throws IOException {
        PlaylistItemListResponse testPlaylistResponse = new PlaylistItemListResponse();
        String description1 = "Get 20% off your first monthly box when you sign up at http://boxofawesome.com";
        String description2 = "Use code LINUS and get 25% off GlassWire";
        String descriptionWithNoCode = "Check out our UPDATED version which has all the NEW ELEMENTS here: "
                + "\n https://youtu.be/rz4Dd1I_fX0  The TEETH Song (Memorize Every Tooth): https://youtu.be/PI3hne8C8rU"
                + "\nDownload on ITUNES: http://bit.ly/12AeW99 ";
        testPlaylistResponse.setItems(Arrays.asList(
                new PlaylistItem().setSnippet(
                        new PlaylistItemSnippet().setDescription(description1).setPublishedAt(new DateTime(DATE))
                                .setTitle(VIDEO_TITLE).setResourceId(new ResourceId().setVideoId(VIDEO_ID))),
                new PlaylistItem().setSnippet(
                        new PlaylistItemSnippet().setDescription(description2).setPublishedAt(new DateTime(DATE))
                                .setTitle(VIDEO_TITLE).setResourceId(new ResourceId().setVideoId(VIDEO_ID))),
                new PlaylistItem().setSnippet(new PlaylistItemSnippet().setDescription(descriptionWithNoCode)
                        .setPublishedAt(new DateTime(DATE)).setTitle(VIDEO_TITLE)
                        .setResourceId(new ResourceId().setVideoId(VIDEO_ID)))));
        when(mockListPlaylistItems.execute()).thenReturn(testPlaylistResponse);
        Optional<List<PromoCode>> actual = scraper.scrapePromoCodesFromPlaylist(UPLOAD_ID);
        assertThat(actual.get(), equalTo(Arrays.asList(
                PromoCode.create("http://boxofawesome.com",
                        "Get 20% off your first monthly box when you sign up at http://boxofawesome.com", VIDEO_ID,
                        VIDEO_TITLE, DATE),
                PromoCode.create("LINUS", "Use code LINUS and get 25% off GlassWire", VIDEO_ID, VIDEO_TITLE, DATE))));
    }

    @Test
    public void scrapePromoCodesFromPlaylist_oneItemNoCodeFound() throws IOException {
        PlaylistItemListResponse testPlaylistResponse = new PlaylistItemListResponse();
        String descriptionWithNoCode = "Check out our UPDATED version which has all the NEW ELEMENTS here: "
                + "\n https://youtu.be/rz4Dd1I_fX0  The TEETH Song (Memorize Every Tooth): https://youtu.be/PI3hne8C8rU"
                + "\nDownload on ITUNES: http://bit.ly/12AeW99 ";
        testPlaylistResponse.setItems(Arrays.asList(new PlaylistItem().setSnippet(
                new PlaylistItemSnippet().setDescription(descriptionWithNoCode).setPublishedAt(new DateTime(DATE))
                        .setTitle(VIDEO_TITLE).setResourceId(new ResourceId().setVideoId(VIDEO_ID)))));
        when(mockListPlaylistItems.execute()).thenReturn(testPlaylistResponse);
        Optional<List<PromoCode>> actual = scraper.scrapePromoCodesFromPlaylist(UPLOAD_ID);
        assertThat(actual.get().isEmpty(), equalTo(true));
    }

    @Test
    public void scrapeVideoInformation_returnNull() throws IOException {
        VideoListResponse testVideoResponse = new VideoListResponse();
        when(mockListVideos.execute()).thenReturn(testVideoResponse);
        Optional<List<Video>> actual = scraper.scrapeVideoInformation(Arrays.asList(NONEXISTENT_VIDEO_ID));
        assertThat(actual.isPresent(), equalTo(false));
    }

    @Test
    public void scrapeVideoInformation_validRequest() throws IOException {
        VideoListResponse testVideoResponse = new VideoListResponse();
        testVideoResponse.setItems(Arrays.asList(new Video(), new Video()));
        when(mockListVideos.execute()).thenReturn(testVideoResponse);
        Optional<List<Video>> actual = scraper.scrapeVideoInformation(Arrays.asList(VIDEO_ID, VIDEO_ID));
        assertThat(actual.get(), equalTo(Arrays.asList(new Video(), new Video())));
    }

    @Test
    public void scrapeVideoIdsFromSearch_returnNull() throws IOException {
        SearchListResponse testSearchResponse = new SearchListResponse();
        when(mockListSearch.execute()).thenReturn(testSearchResponse);
        Optional<List<String>> actual = scraper.scrapeVideoIdsFromSearch(NO_RESULTS_KEYWORD);
        assertThat(actual.isPresent(), equalTo(false));
    }

    @Test
    public void scrapeVideoIdsFromSearch_returnVideoIds() throws IOException {
        SearchListResponse testSearchResponse = new SearchListResponse();
        testSearchResponse.setItems(Arrays.asList(new SearchResult().setId(new ResourceId().setVideoId(VIDEO_ID)),
                new SearchResult().setId(new ResourceId().setVideoId(VIDEO_ID))));
        when(mockListSearch.execute()).thenReturn(testSearchResponse);
        Optional<List<String>> actual = scraper.scrapeVideoIdsFromSearch(KEYWORD);
        assertThat(actual.get(), equalTo(Arrays.asList(VIDEO_ID, VIDEO_ID)));
    }

    @Test
    public void scrapePromoCodesFromVideos_emptyVideoIdList() throws IOException {
        VideoListResponse testVideoResponse = new VideoListResponse();
        when(mockListVideos.execute()).thenReturn(testVideoResponse);
        Optional<List<PromoCode>> actual = scraper.scrapePromoCodesFromVideos(KEYWORD, EMPTY_VIDEO_ID_LIST);
        assertThat(actual.isPresent(), equalTo(false));
    }

    /* Multiple items with one item having no promo-code in the description. */
    @Test
    public void scrapePromoCodesFromVideos_multipleItemsSomeCodesFound() throws IOException {
        VideoListResponse testVideoResponse = new VideoListResponse();
        // Two company names one promo-code found.
        String snippet = "DISCOUNT!!! For a limited time only, take 25% off your first purchase at " + KEYWORD
                + " with the code OVDOINGTHINGS25. This offer is valid online only. ";
        String description1 = snippet + "\nMust apply code at " + KEYWORD + " checkout. Expires August 31, 2020.";
        // One keyword two promo-codes found.
        String description2 = "And if you want to order food through " + KEYWORD + " go to "
                + "https://pmfleet.app.link/zyoaw9s3R6 and use my code A1JZN";
        String descriptionWithNoCode = KEYWORD + " is a great company!";
        testVideoResponse.setItems(Arrays.asList(
                new Video().setId(VIDEO_ID)
                        .setSnippet(new VideoSnippet().setTitle(VIDEO_TITLE).setDescription(description1)
                                .setPublishedAt(new DateTime(DATE))),
                new Video().setId(VIDEO_ID)
                        .setSnippet(new VideoSnippet().setTitle(VIDEO_TITLE).setDescription(description2)
                                .setPublishedAt(new DateTime(DATE))),
                new Video().setId(VIDEO_ID).setSnippet(new VideoSnippet().setTitle(VIDEO_TITLE)
                        .setDescription(descriptionWithNoCode).setPublishedAt(new DateTime(DATE)))));
        when(mockListVideos.execute()).thenReturn(testVideoResponse);
        Optional<List<PromoCode>> actual = scraper.scrapePromoCodesFromVideos(KEYWORD, VIDEO_ID_LIST);
        assertThat(actual.get(), equalTo(Arrays.asList(
                PromoCode.create("OVDOINGTHINGS25", snippet, VIDEO_ID, VIDEO_TITLE, DATE),
                PromoCode.create("A1JZN", description2, VIDEO_ID, VIDEO_TITLE, DATE),
                PromoCode.create("https://pmfleet.app.link/zyoaw9s3R6", description2, VIDEO_ID, VIDEO_TITLE, DATE))));
    }

    @Test
    public void scrapePromoCodesFromVideos_oneItemNoCodesFound() throws IOException {
        VideoListResponse testVideoResponse = new VideoListResponse();
        String descriptionWithNoCode = KEYWORD + " is a great company!";
        testVideoResponse.setItems(Arrays.asList(new Video().setId(VIDEO_ID).setSnippet(new VideoSnippet()
                .setTitle(VIDEO_TITLE).setDescription(descriptionWithNoCode).setPublishedAt(new DateTime(DATE)))));
        when(mockListVideos.execute()).thenReturn(testVideoResponse);
        Optional<List<PromoCode>> actual = scraper.scrapePromoCodesFromVideos(KEYWORD, VIDEO_ID_LIST);
        assertThat(actual.get().isEmpty(), equalTo(true));
    }
}
