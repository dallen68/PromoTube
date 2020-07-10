package com.google.step.servlets;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.any;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.anyString;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;


import com.google.step.youtube.YoutubeInfoScraper;
import com.google.step.youtube.PromoCode;
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class PromoCodeServletTest {
        
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

    @Mock
    HttpServletRequest request;
 
    @Mock
    HttpServletResponse response;

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
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
    public void incorrectChannelIdRequest() throws ServletException, IOException {
        when(request.getParameter("formInput")).thenReturn(CHANNEL_ID_NONEXISTENT);
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);

        PromoCodeServlet promoCodeServlet = new PromoCodeServlet();
        promoCodeServlet.init();
        promoCodeServlet.doGet(request, response);
        String result = sw.getBuffer().toString();

        assertThat(result , equalTo("false\n"));

    }
}
