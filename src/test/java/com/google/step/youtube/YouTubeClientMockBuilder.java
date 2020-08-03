package com.google.step.youtube;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Channels;
import com.google.api.services.youtube.YouTube.PlaylistItems;
import com.google.api.services.youtube.YouTube.Search;
import com.google.api.services.youtube.YouTube.Videos;
import java.io.IOException;

public class YouTubeClientMockBuilder {

    private YouTube mockYouTubeClient;
    private Channels.List mockListChannels;
    private Videos.List mockListVideos;
    private Search.List mockListSearch;
    private PlaylistItems.List mockListPlaylistItems;

    YouTubeClientMockBuilder() throws IOException {
        mockYouTubeClient = mock(YouTube.class);

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
    }

    public YouTube getYouTubeMock() {
        return mockYouTubeClient;
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
}
