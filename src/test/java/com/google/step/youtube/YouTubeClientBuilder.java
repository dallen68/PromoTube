package com.google.step.youtube;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Channels;
import com.google.api.services.youtube.YouTube.PlaylistItems;
import com.google.api.services.youtube.YouTube.Search;
import com.google.api.services.youtube.YouTube.Videos;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelContentDetails;
import com.google.api.services.youtube.model.ChannelContentDetails.RelatedPlaylists;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

public class YouTubeClientBuilder {

    private static Channels.List mockListChannels;
    private static Videos.List mockListVideos;
    private static Search.List mockListSearch;
    private static PlaylistItems.List mockListPlaylistItems;

    private static final String UPLOAD_ID = "UPLOAD_ID";
    private static final String VIDEO_ID = "VIDEO_ID";
    private static final String VIDEO_TITLE = "VIDEO_TITLE";
    private static final Date DATE = new Date(0L);

    public static final String KEYWORD = "KEYWORD";
    public static final String PROMOCODE = "PROMOCODE";
    public static final String PROMOCODE_DESCRIPTION = "Get 20% off your first monthly box and enter the code "
            + PROMOCODE;
    public static final String DESCRIPTION = "DESCRIPTION WITH NO PROMOCODE";
    public static final String KEYWORD_DESCRIPTION = "Get 25% off your first purchase at " + KEYWORD + " with the code "
            + PROMOCODE;

    public static YouTube create() throws IOException {
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
        when(mockListPlaylistItems.setMaxResults(YouTubeInfoScraper.MAX_PLAYLIST_RESULTS))
                .thenReturn(mockListPlaylistItems);
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
        when(mockListSearch.setMaxResults(YouTubeInfoScraper.MAX_SEARCH_RESULTS)).thenReturn(mockListSearch);
        when(mockListSearch.setQ(anyString())).thenReturn(mockListSearch);

        return mockYouTubeClient;
    }

    public static PromoCode newPromoCode() {
        return PromoCode.create(PROMOCODE, PROMOCODE_DESCRIPTION, VIDEO_ID, VIDEO_TITLE, DATE);
    }

    public Channels.List getMockListChannels() {
        return mockListChannels;
    }

    public PlaylistItems.List getMockListPlaylistItems() {
        return mockListPlaylistItems;
    }

    public Videos.List getMockListVideos() {
        return mockListVideos;
    }

    public Search.List getMockListSearch() {
        return mockListSearch;
    }

    public ChannelListResponse newBasicChannelResponse() {
        ChannelListResponse testChannelResponse = new ChannelListResponse();
        Channel channel = new Channel();
        channel.setContentDetails(
                new ChannelContentDetails().setRelatedPlaylists(new RelatedPlaylists().setUploads(UPLOAD_ID)));
        testChannelResponse.setItems(Arrays.asList(channel));
        return testChannelResponse;
    }

    public PlaylistItemListResponse newBasicPlaylistResponse() {
        PlaylistItemListResponse testPlaylistResponse = new PlaylistItemListResponse();
        testPlaylistResponse.setItems(Arrays.asList(newBasicPlaylistItem(PROMOCODE_DESCRIPTION)));
        return testPlaylistResponse;
    }

    public PlaylistItem newBasicPlaylistItem(String description) {
        return new PlaylistItem()
                .setSnippet(new PlaylistItemSnippet().setDescription(description).setPublishedAt(new DateTime(DATE))
                        .setTitle(VIDEO_TITLE).setResourceId(new ResourceId().setVideoId(VIDEO_ID)));
    }

    public VideoListResponse newBasicVideoListResponse() {
        VideoListResponse testVideoResponse = new VideoListResponse();
        testVideoResponse.setItems(Arrays.asList(new Video(), new Video()));
        return testVideoResponse;
    }

    public Video newBasicVideoResponse(String description) {
        return new Video().setId(VIDEO_ID).setSnippet(new VideoSnippet().setTitle(VIDEO_TITLE)
                .setDescription(description).setPublishedAt(new DateTime(DATE)));
    }

    public SearchListResponse newBaSearchListResponse() {
        SearchListResponse testSearchResponse = new SearchListResponse();
        testSearchResponse.setItems(Arrays.asList(newBasicSearchResult(), newBasicSearchResult()));
        return testSearchResponse;
    }

    public SearchResult newBasicSearchResult() {
        return new SearchResult().setId(new ResourceId().setVideoId(VIDEO_ID));
    }
}
