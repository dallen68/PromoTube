package com.google.step.servlets;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import com.google.step.youtube.YouTubeInfoScraper;
import com.google.step.youtube.PromoCode;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class ChannelPromoCodeServletTest {

    private ChannelPromoCodeServlet servlet;

    private static final String NONEXISTENT_CHANNEL_ID = "NONEXISTENT_CHANNEL_ID";
    private static final String CHANNEL_ID_URL = "https://www.youtube.com/channel/CHANNEL_ID";
    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static final String USERNAME_URL = "https://www.youtube.com/user/USERNAME";
    private static final String USERNAME = "USERNAME";
    private static final String UPLOAD_ID = "UPLOAD_ID";
    private static final String VIDEO_ID = "VIDEO_ID";
    private static final String VIDEO_TITLE = "VIDEO_TITLE";
    private static final String IOEXCEPTION_CHANNEL_ID = "IOEXCEPTION_CHANNEL_ID";
    private static final Date DATE = new Date(0);
    private static final String CHANNEL_NAME = "CHANNEL_NAME";
    private static final String SNIPPET = "SNIPPET";

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private YouTubeInfoScraper infoScraper;

    @Before
    public void setup() throws IOException {
        infoScraper = mock(YouTubeInfoScraper.class);
        servlet = new ChannelPromoCodeServlet(infoScraper);
    }

    @Test
    public void incorrectChannelIdRequest() throws IOException {
        when(request.getParameter(servlet.REQUEST_PARAMETER)).thenReturn(NONEXISTENT_CHANNEL_ID);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);
        when(infoScraper.scrapeChannelUploadPlaylist(NONEXISTENT_CHANNEL_ID)).thenReturn(Optional.empty());

        servlet.doGet(request, response);
        String result = sw.getBuffer().toString();

        assertThat(result, equalTo("[]\n"));
    }

    @Test
    public void correctChannelIdRequest() throws IOException {
        when(request.getParameter(servlet.REQUEST_PARAMETER)).thenReturn(CHANNEL_ID_URL);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);
        when(infoScraper.scrapeChannelUploadPlaylist(CHANNEL_ID)).thenReturn(Optional.of(UPLOAD_ID));

        when(infoScraper.scrapePromoCodesFromPlaylist(UPLOAD_ID)).thenReturn(
                Optional.of(Arrays.asList(PromoCode.create(CHANNEL_NAME, SNIPPET, VIDEO_ID, VIDEO_TITLE, DATE))));
        servlet.doGet(request, response);
        String result = sw.getBuffer().toString();

        assertThat(result, equalTo(
                "[{\"promoCode\":\"CHANNEL_NAME\",\"snippet\":\"SNIPPET\",\"videoId\":\"VIDEO_ID\",\"videoTitle\":\"VIDEO_TITLE\","
                        + "\"videoUploadDate\":\"" + DateFormat.getDateTimeInstance().format(DATE) + "\"}]\n"));
    }

    @Test
    public void correctUserIdRequest() throws IOException {
        when(request.getParameter(servlet.REQUEST_PARAMETER)).thenReturn(USERNAME_URL);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);
        when(infoScraper.scrapeUserUploadPlaylist(USERNAME)).thenReturn(Optional.of(UPLOAD_ID));
        when(infoScraper.scrapePromoCodesFromPlaylist(UPLOAD_ID)).thenReturn(
                Optional.of(Arrays.asList(PromoCode.create(CHANNEL_NAME, SNIPPET, VIDEO_ID, VIDEO_TITLE, DATE))));
        servlet.doGet(request, response);
        String result = sw.getBuffer().toString();

        assertThat(result, equalTo(
                "[{\"promoCode\":\"CHANNEL_NAME\",\"snippet\":\"SNIPPET\",\"videoId\":\"VIDEO_ID\",\"videoTitle\":\"VIDEO_TITLE\","
                        + "\"videoUploadDate\":\"" + DateFormat.getDateTimeInstance().format(DATE) + "\"}]\n"));
    }

    @Test
    public void channelIdRequestThrowsException() throws IOException {
        when(request.getParameter(servlet.REQUEST_PARAMETER)).thenReturn(IOEXCEPTION_CHANNEL_ID);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);
        when(infoScraper.scrapeChannelUploadPlaylist(IOEXCEPTION_CHANNEL_ID)).thenThrow(IOException.class);

        servlet.doGet(request, response);
        String result = sw.getBuffer().toString();

        assertThat(result, equalTo("[]\n"));
    }

    @Test
    public void correctChannelIdRequestNoCodes() throws IOException {
        when(request.getParameter(servlet.REQUEST_PARAMETER)).thenReturn(CHANNEL_ID_URL);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);
        when(infoScraper.scrapeChannelUploadPlaylist(CHANNEL_ID)).thenReturn(Optional.of(UPLOAD_ID));
        when(infoScraper.scrapePromoCodesFromPlaylist(UPLOAD_ID)).thenReturn(Optional.empty());
        servlet.doGet(request, response);
        String result = sw.getBuffer().toString();

        assertThat(result, equalTo("[]\n"));
    }
}
