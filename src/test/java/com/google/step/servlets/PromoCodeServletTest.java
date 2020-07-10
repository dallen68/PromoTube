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
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

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
        
    private PromoCodeServlet servlet;

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

    @Mock
    YoutubeInfoScraper infoScraper;

    @Before
    public void setup() throws IOException {
        //MockitoAnnotations.initMocks(this);
        
        infoScraper = mock(YoutubeInfoScraper.class);
        servlet = new PromoCodeServlet(infoScraper);
    }

    @Test
    public void incorrectChannelIdRequest() throws ServletException, IOException {
        when(request.getParameter("formInput")).thenReturn(CHANNEL_ID_NONEXISTENT);
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);
        when(infoScraper.scrapeChannelUploadPlaylist(anyString())).thenReturn(Optional.empty());

        servlet.doGet(request, response);
        String result = sw.getBuffer().toString();

        assertThat(result , equalTo("false\n"));
    }

    @Test
    public void correctChannelIdRequest() throws ServletException, IOException {
        when(request.getParameter("formInput")).thenReturn(CHANNEL_ID);
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);
        when(infoScraper.scrapeChannelUploadPlaylist(CHANNEL_ID)).thenReturn(Optional.of(UPLOAD_ID));
        when(infoScraper.scrapePromoCodesFromPlaylist(UPLOAD_ID)).thenReturn(Optional.of(Arrays.asList(
                PromoCode.create("LINUS", VIDEO_ID, new Date(MOCK_DATE)))));
        servlet.doGet(request, response);
        String result = sw.getBuffer().toString();

        assertThat(result , equalTo("[{\"promoCode\":\"LINUS\",\"videoId\":\"VIDEO_ID\",\"videoUploadDate\":\"Jan 1, 1970, 12:00:00 AM\"}]\n"));
    }

    @Test
    public void channelIdRequestThrowsException() throws ServletException, IOException {
        when(request.getParameter("formInput")).thenReturn(CHANNEL_ID_NONEXISTENT);
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);
        when(infoScraper.scrapeChannelUploadPlaylist(anyString())).thenThrow(IOException.class);

        servlet.doGet(request, response);
        String result = sw.getBuffer().toString();

        assertThat(result , equalTo("false\n"));
    }
}
