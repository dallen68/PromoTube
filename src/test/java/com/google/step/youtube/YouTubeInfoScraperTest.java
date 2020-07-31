package com.google.step.youtube;

import static com.google.step.youtube.YouTubeClientBuilder.PROMOCODE_DESCRIPTION;
import static com.google.step.youtube.YouTubeClientBuilder.KEYWORD_DESCRIPTION;
import static com.google.step.youtube.YouTubeClientBuilder.DESCRIPTION;
import static com.google.step.youtube.YouTubeClientBuilder.PROMOCODE;
import static com.google.step.youtube.YouTubeClientBuilder.KEYWORD;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.YouTube.Channels;
import com.google.api.services.youtube.YouTube.PlaylistItems;
import com.google.api.services.youtube.YouTube.Videos;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.YouTube.Search;
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

    private YouTubeClientBuilder ytClientBuilder;
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
    private static final String NO_RESULTS_KEYWORD = "NO_RESULTS_KEYWORD";
    private static final List<String> EMPTY_VIDEO_ID_LIST = Arrays.asList();
    private static final List<String> VIDEO_ID_LIST = Arrays.asList();
    private static final Date DATE = new Date(0L);

    @Before
    public void setUp() throws IOException {
        ytClientBuilder = new YouTubeClientBuilder();
        scraper = new YouTubeInfoScraper(YouTubeClientBuilder.create());
        mockListChannels = ytClientBuilder.getMockListChannels();
        mockListPlaylistItems = ytClientBuilder.getMockListPlaylistItems();
        mockListVideos = ytClientBuilder.getMockListVideos();
        mockListSearch = ytClientBuilder.getMockListSearch();
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
        when(mockListChannels.execute()).thenReturn(ytClientBuilder.newBasicChannelResponse());
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
        when(mockListChannels.execute()).thenReturn(ytClientBuilder.newBasicChannelResponse());
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
        PlaylistItemListResponse testPlaylistResponse = ytClientBuilder.newBasicPlaylistResponse();
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
        PlaylistItemListResponse testPlaylistResponse = ytClientBuilder.newBasicPlaylistResponse();
        when(mockListPlaylistItems.execute()).thenReturn(testPlaylistResponse);
        Optional<List<PromoCode>> actual = scraper.scrapePromoCodesFromPlaylist(UPLOAD_ID);
        assertThat(actual.get(), equalTo(
                Arrays.asList(PromoCode.create(PROMOCODE, PROMOCODE_DESCRIPTION, VIDEO_ID, VIDEO_TITLE, DATE))));
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
        testPlaylistResponse.setItems(Arrays.asList(ytClientBuilder.newBasicPlaylistItem(PROMOCODE_DESCRIPTION),
                ytClientBuilder.newBasicPlaylistItem(PROMOCODE_DESCRIPTION),
                ytClientBuilder.newBasicPlaylistItem(DESCRIPTION)));
        when(mockListPlaylistItems.execute()).thenReturn(testPlaylistResponse);
        Optional<List<PromoCode>> actual = scraper.scrapePromoCodesFromPlaylist(UPLOAD_ID);
        assertThat(actual.get(),
                equalTo(Arrays.asList(YouTubeClientBuilder.newPromoCode(), YouTubeClientBuilder.newPromoCode())));
    }

    @Test
    public void scrapePromoCodesFromPlaylist_oneItemNoCodeFound() throws IOException {
        PlaylistItemListResponse testPlaylistResponse = new PlaylistItemListResponse();
        testPlaylistResponse.setItems(Arrays.asList(ytClientBuilder.newBasicPlaylistItem(DESCRIPTION)));
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
        VideoListResponse testVideoResponse = ytClientBuilder.newBasicVideoListResponse();
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
        SearchListResponse testSearchResponse = ytClientBuilder.newBaSearchListResponse();
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
        testVideoResponse.setItems(Arrays.asList(ytClientBuilder.newBasicVideoResponse(KEYWORD_DESCRIPTION),
                ytClientBuilder.newBasicVideoResponse(KEYWORD_DESCRIPTION),
                ytClientBuilder.newBasicVideoResponse(DESCRIPTION)));
        when(mockListVideos.execute()).thenReturn(testVideoResponse);
        Optional<List<PromoCode>> actual = scraper.scrapePromoCodesFromVideos(KEYWORD, VIDEO_ID_LIST);
        assertThat(actual.get(),
                equalTo(Arrays.asList(PromoCode.create(PROMOCODE, KEYWORD_DESCRIPTION, VIDEO_ID, VIDEO_TITLE, DATE),
                        PromoCode.create(PROMOCODE, KEYWORD_DESCRIPTION, VIDEO_ID, VIDEO_TITLE, DATE))));
    }

    @Test
    public void scrapePromoCodesFromVideos_oneItemNoCodesFound() throws IOException {
        VideoListResponse testVideoResponse = new VideoListResponse();
        String descriptionWithNoCode = KEYWORD + " is a great company!";
        testVideoResponse.setItems(Arrays.asList(ytClientBuilder.newBasicVideoResponse(descriptionWithNoCode)));
        when(mockListVideos.execute()).thenReturn(testVideoResponse);
        Optional<List<PromoCode>> actual = scraper.scrapePromoCodesFromVideos(KEYWORD, VIDEO_ID_LIST);
        assertThat(actual.get().isEmpty(), equalTo(true));
    }
}
