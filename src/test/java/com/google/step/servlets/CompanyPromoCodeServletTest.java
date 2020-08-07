package com.google.step.servlets;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.repackaged.com.google.common.collect.ImmutableList;
import com.google.step.youtube.PromoCode;
import com.google.step.youtube.YouTubeInfoScraper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;

@RunWith(MockitoJUnitRunner.class)
public final class CompanyPromoCodeServletTest {

    private CompanyPromoCodeServlet servlet;

    private static final String NO_IDS_COMPANY_NAME = "NO_IDS_COMPANY_NAME";
    private static final String COMPANY_NAME = "COMPANY_NAME";
    private static final List<String> VIDEO_IDS = ImmutableList.of("VIDEO_ID");
    private static final String VIDEO_ID = "VIDEO_ID";
    private static final String VIDEO_TITLE = "VIDEO_TITLE";
    private static final String IOEXCEPTION_CHANNEL_ID = "IOEXCEPTION_CHANNEL_ID";
    private static final Date DATE = new Date(0);
    private static final String CHANNEL_NAME = "CHANNEL_NAME";
    private static final String SNIPPET = "SNIPPET";
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private StringWriter sw;
    private PrintWriter pw;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private YouTubeInfoScraper infoScraper;

    @Before
    public void setup() throws IOException {
        infoScraper = mock(YouTubeInfoScraper.class);
        servlet = new CompanyPromoCodeServlet(infoScraper);
        sw = new StringWriter();
        pw = new PrintWriter(sw);
    }

    @Test
    public void noVideoIds() throws IOException {
        when(request.getParameter(servlet.REQUEST_PARAMETER)).thenReturn(NO_IDS_COMPANY_NAME);

        when(response.getWriter()).thenReturn(pw);
        when(infoScraper.scrapeVideoIdsFromSearch(NO_IDS_COMPANY_NAME)).thenReturn(Optional.empty());

        servlet.doGet(request, response);
        String result = sw.getBuffer().toString();
        assertThat(result, equalTo("[]"+LINE_SEPARATOR));
    }

    @Test
    public void noPromoCodesFromVideos() throws IOException {
        when(request.getParameter(servlet.REQUEST_PARAMETER)).thenReturn(COMPANY_NAME);

        when(response.getWriter()).thenReturn(pw);
        when(infoScraper.scrapeVideoIdsFromSearch(COMPANY_NAME)).thenReturn(Optional.of(VIDEO_IDS));
        when(infoScraper.scrapePromoCodesFromVideos(COMPANY_NAME, VIDEO_IDS)).thenReturn(Optional.empty());

        servlet.doGet(request, response);
        String result = sw.getBuffer().toString();

        assertThat(result, equalTo("[]"+LINE_SEPARATOR));
    }

    @Test
    public void promoCodesScrapedFromNonEmptyVideos() throws IOException {
        when(request.getParameter(servlet.REQUEST_PARAMETER)).thenReturn(COMPANY_NAME);

        when(response.getWriter()).thenReturn(pw);
        when(infoScraper.scrapeVideoIdsFromSearch(COMPANY_NAME)).thenReturn(Optional.of(VIDEO_IDS));
        when(infoScraper.scrapePromoCodesFromVideos(COMPANY_NAME, VIDEO_IDS))
        .thenReturn(Optional.of(Arrays.asList(
            PromoCode.builder().setPromoCode(CHANNEL_NAME).setSnippet(SNIPPET).setVideoId(VIDEO_ID)
                .setVideoTitle(VIDEO_TITLE).setVideoUploadDate(DATE).build()
        )));
        servlet.doGet(request, response);
        String result = sw.getBuffer().toString();

        assertThat(result, equalTo(
                "[{\"promoCode\":\"CHANNEL_NAME\",\"snippet\":\"SNIPPET\",\"videoId\":\"VIDEO_ID\",\"videoTitle\":\"VIDEO_TITLE\","
                        + "\"videoUploadDate\":\"" + DateFormat.getDateTimeInstance().format(DATE) + "\"}]"+LINE_SEPARATOR));
    }

    @Test
    public void companyRequestThrowsException() throws IOException {
        when(request.getParameter(servlet.REQUEST_PARAMETER)).thenReturn(IOEXCEPTION_CHANNEL_ID);

        when(response.getWriter()).thenReturn(pw);
        when(infoScraper.scrapeVideoIdsFromSearch(IOEXCEPTION_CHANNEL_ID)).thenThrow(IOException.class);

        servlet.doGet(request, response);
        String result = sw.getBuffer().toString();

        assertThat(result, equalTo("[]"+LINE_SEPARATOR));
    }
}
